package com.alibaba.matrix.extension.util;

import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.factory.DubboServiceFactory;
import com.alibaba.matrix.extension.factory.GroovyServiceFactory;
import com.alibaba.matrix.extension.factory.GuiceInstanceFactory;
import com.alibaba.matrix.extension.factory.HsfServiceFactory;
import com.alibaba.matrix.extension.factory.HttpServiceFactory;
import com.alibaba.matrix.extension.factory.ObjectInstanceFactory;
import com.alibaba.matrix.extension.factory.ProviderInstanceFactory;
import com.alibaba.matrix.extension.factory.SpELServiceFactory;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.model.ExtensionImplType;
import com.alibaba.matrix.extension.model.config.Bean;
import com.alibaba.matrix.extension.model.config.Dubbo;
import com.alibaba.matrix.extension.model.config.Extension;
import com.alibaba.matrix.extension.model.config.ExtensionImpl;
import com.alibaba.matrix.extension.model.config.ExtensionScope;
import com.alibaba.matrix.extension.model.config.Groovy;
import com.alibaba.matrix.extension.model.config.Guice;
import com.alibaba.matrix.extension.model.config.Hsf;
import com.alibaba.matrix.extension.model.config.Http;
import com.alibaba.matrix.extension.model.config.ObjectT;
import com.alibaba.matrix.extension.model.config.Provider;
import com.alibaba.matrix.extension.model.config.SpEL;
import com.google.common.base.Splitter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_SCOPE;
import static com.alibaba.matrix.extension.util.Logger.log;
import static java.util.Optional.ofNullable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class XmlLoader {

    private static final String EXTENSION_XML_CONFIG_LOCATION_DEMO = "classpath*:matrix-extension-*.xml";

    private static final SAXReader saxReader = new SAXReader();

    public static Map<Class<?>, Extension> loadXml(List<String> configLocations) {
        if (CollectionUtils.isEmpty(configLocations)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0001"));
        }
        log.info("Xml configLocations: {}", configLocations);

        try {
            Map<Class<?>, Extension> extensionMap = new LinkedHashMap<>();
            for (String configLocation : configLocations) {
                try {
                    Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configLocation);
                    if (ArrayUtils.isEmpty(resources)) {
                        log.error("No resources founded by configLocation:[{}]", configLocation);
                        continue;
                    }

                    loadResources(resources, extensionMap);
                } catch (IOException e) {
                    log.error("Parse config:[{}] error.", configLocation, e);
                }
            }
            return extensionMap;
        } catch (Throwable t) {
            return ExceptionUtils.rethrow(t);
        }
    }

    public static void loadResources(Resource[] resources, Map<Class<?>, Extension> extensionMap) throws Exception {
        for (Resource resource : resources) {
            String file = resource.getFilename();
            log.info("Loading extensions from [{}] ...", file);

            Element document = saxReader.read(resource.getInputStream()).getRootElement();
            loadExtensions(document, extensionMap);
        }
    }

    private static void loadExtensions(Element document, Map<Class<?>, Extension> extensionMap) throws Exception {
        for (Iterator<Element> iterator = document.elementIterator(); iterator.hasNext(); ) {
            Extension extension = loadExtension(iterator.next());
            if (extensionMap.containsKey(extension.clazz)) {
                throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0002", extension.clazz.getName()));
            }
            extensionMap.put(extension.clazz, extension);
        }
    }


    private static Extension loadExtension(Element extensionElement) throws Exception {
        Class<?> extension = Class.forName(getAttrValNoneNull(extensionElement, "<Extension/>", "class"));

        // if (!ext.isInterface()) {
        //    throw new ExtensionException(String.format("<Extension/>: extension: [%s] is not a interface.", ext.getName()));
        // }

        String desc = extensionElement.attributeValue("desc");
        log.info("Loaded <Extension/>: [{}].", Logger.formatExt(extension, desc));

        Object base = loadExtensionBase(extension, extensionElement);
        Map<String, ExtensionScope> scopeMap = loadExtensionScopeMap(extension, extensionElement);

        return new Extension(extension, desc, base, scopeMap);
    }

    private static Object loadExtensionBase(Class<?> extension, Element extensionElement) throws Exception {
        Element extensionBaseElement = extensionElement.element("ExtensionBase");
        if (extensionBaseElement == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0011"));
        }

        Object base;
        ExtensionImplType type = ExtensionImplType.fromStr(getAttrValNoneNull(extensionBaseElement, "<ExtensionBase/>", "type"));
        switch (type) {
            case OBJECT:
                base = ObjectInstanceFactory.getObjectInstance(loadingObject(extensionBaseElement.element("object")));
                break;
            case BEAN:
                base = SpringBeanFactory.getSpringBean(loadingBean(extensionBaseElement.element("bean")));
                break;
            case GUICE:
                base = GuiceInstanceFactory.getGuiceInstance(loadingGuice(extensionBaseElement.element("guice")));
                break;
            case PROVIDER:
                base = ProviderInstanceFactory.getProviderInstance(loadingProvider(extensionBaseElement.element("provider")));
                break;
            default:
                throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0012", type));
        }

        if (!extension.isInstance(base)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0009", base, extension.getName()));
        }
        log.info("Loaded <ExtensionBase/>: extension:[{}] base:[{}].", extension.getName(), Logger.formatBase(base));

        return base;
    }

    private static Map<String, ExtensionScope> loadExtensionScopeMap(Class<?> extension, Element extensionElement) throws Exception {

        Map<String, Map<String, List<Wrapper>>> scope2code2wrappers = new LinkedHashMap<>();
        for (Iterator<Element> iterator = extensionElement.elementIterator("ExtensionImpl"); iterator.hasNext(); ) {
            Wrapper wrapper = loadImplWrapper(iterator.next());
            for (String scope : wrapper.scope) {
                for (String code : wrapper.code) {
                    scope2code2wrappers.computeIfAbsent(scope, _K -> new LinkedHashMap<>()).computeIfAbsent(code, _K -> new LinkedList<>()).add(wrapper);
                }
            }
        }

        if (MapUtils.isEmpty(scope2code2wrappers)) {
            log.warn("{}", Message.format("MATRIX-EXTENSION-0001-0006", extension.getName()));
        }

        Map<String, ExtensionScope> scopeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, List<Wrapper>>> entry : scope2code2wrappers.entrySet()) {
            String scope = entry.getKey();
            Map<String, List<Wrapper>> code2wrappers = entry.getValue();

            scopeMap.put(scope, convertToExtensionScope(extension, scope, code2wrappers));
        }

        return scopeMap;
    }

    private static Wrapper loadImplWrapper(Element extensionImplElement) {
        Wrapper wrapper = new Wrapper();

        String scope = ofNullable(extensionImplElement.attributeValue("scope")).orElse(BASE_SCOPE);
        String code = getAttrValNoneNull(extensionImplElement, "<ExtensionImpl/>", "code");

        wrapper.scope = Splitter.on(",").trimResults().omitEmptyStrings().split(scope);
        wrapper.code = Splitter.on(",").trimResults().omitEmptyStrings().split(code);
        wrapper.desc = extensionImplElement.attributeValue("desc");

        ofNullable(extensionImplElement.attributeValue("priority")).map(Integer::valueOf).ifPresent(priority -> wrapper.priority = priority);
        ofNullable(extensionImplElement.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> wrapper.lazy = lazy);

        ExtensionImplType type = ExtensionImplType.fromStr(getAttrValNoneNull(extensionImplElement, "<ExtensionImpl/>", "type"));
        // 后续有可能得话, 可以把这部分搞成Jar包的扩展、XML的scope扩展
        wrapper.type = type.name();
        switch (type) {
            case OBJECT:
                wrapper.object = loadingObject(extensionImplElement.element("object"));
                break;
            case BEAN:
                wrapper.bean = loadingBean(extensionImplElement.element("bean"));
                break;
            case GUICE:
                wrapper.guice = loadingGuice(extensionImplElement.element("guice"));
                break;
            case HSF:
                wrapper.hsf = loadingHsf(extensionImplElement.element("hsf"));
                break;
            case DUBBO:
                wrapper.dubbo = loadingDubbo(extensionImplElement.element("dubbo"));
                break;
            case HTTP:
                wrapper.http = loadingHttp(extensionImplElement.element("http"));
                break;
            case GROOVY:
                wrapper.groovy = loadingGroovy(extensionImplElement.element("groovy"));
                break;
            case SPEL:
                wrapper.spel = loadingSpEL(extensionImplElement.element("spel"));
                break;
            case PROVIDER:
                wrapper.provider = loadingProvider(extensionImplElement.element("provider"));
                break;
            default:
                throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0005", type));
        }

        return wrapper;
    }

    private static ExtensionScope convertToExtensionScope(Class<?> extension, String scope, Map<String, List<Wrapper>> code2wrappers) throws Exception {

        Map<String, List<ExtensionImpl>> code2impls = new LinkedHashMap<>();

        for (String code : code2wrappers.keySet()) {
            List<Wrapper> wrappers = code2wrappers.get(code);
            // 先排序一次, 后序如果同code合并, 还需要再次排序
            wrappers.sort(Comparator.comparingInt(wrapper -> wrapper.priority));

            List<ExtensionImpl> impls = new ArrayList<>(wrappers.size());
            for (Wrapper wrapper : wrappers) {
                ExtensionImpl impl = convertToImpl(extension, scope, code, wrapper);

                if (impl.instance != null && !extension.isInstance(impl.instance)) {
                    throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0010", impl.instance, extension.getName()));
                }

                impls.add(impl);
            }

            code2impls.put(code, impls);
            log.info("Loaded <ExtensionImpl/>: extension:[{}] scope:[{}] code:[{}] -> [{}].", extension.getName(), scope, code, impls.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));
        }

        return new ExtensionScope(scope, code2impls);
    }

    private static ExtensionImpl convertToImpl(Class<?> extension, String scope, String code, Wrapper wrapper) throws Exception {

        if (wrapper.object != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.object = wrapper.object;
            impl.instance = wrapper.lazy ? null : ObjectInstanceFactory.getObjectInstance(wrapper.object);
            return impl;
        }

        if (wrapper.bean != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.bean = wrapper.bean;
            impl.instance = wrapper.lazy ? null : SpringBeanFactory.getSpringBean(wrapper.bean);
            return impl;
        }

        if (wrapper.guice != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.guice = wrapper.guice;
            impl.instance = wrapper.lazy ? null : GuiceInstanceFactory.getGuiceInstance(wrapper.guice);
            return impl;
        }

        if (wrapper.hsf != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.hsf = wrapper.hsf;
            impl.instance = wrapper.lazy ? null : HsfServiceFactory.getHsfService(wrapper.hsf);
            return impl;
        }

        if (wrapper.dubbo != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.dubbo = wrapper.dubbo;
            impl.instance = wrapper.lazy ? null : DubboServiceFactory.getDubboService(extension, wrapper.dubbo);
            return impl;
        }

        if (wrapper.http != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.http = wrapper.http;
            impl.instance = wrapper.lazy ? null : HttpServiceFactory.getHttpService(extension, wrapper.http);
            return impl;
        }

        if (wrapper.groovy != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.groovy = wrapper.groovy;
            impl.instance = wrapper.lazy ? null : GroovyServiceFactory.getGroovyService(extension, wrapper.groovy);
            return impl;
        }

        if (wrapper.spel != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.spel = wrapper.spel;
            impl.instance = wrapper.lazy ? null : SpELServiceFactory.getSpELService(extension, wrapper.spel);
            return impl;
        }

        if (wrapper.provider != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type, wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.provider = wrapper.provider;
            impl.instance = wrapper.lazy ? null : ProviderInstanceFactory.getProviderInstance(wrapper.provider);
            return impl;
        }

        throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0005", wrapper.type));
    }

    private static ObjectT loadingObject(Element element) {
        String tag = "<object/>";
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", tag));
        }

        String clazz = getAttrValNoneNull(element, tag, "class");
        ObjectT object = new ObjectT(clazz);
        ofNullable(element.attributeValue("arg0")).ifPresent(arg0 -> object.arg0 = arg0);

        return object;
    }

    private static Bean loadingBean(Element element) {
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", "<bean/>"));
        }

        String name = element.attributeValue("name");
        String clazz = element.attributeValue("class");
        if (StringUtils.isAllEmpty(name, clazz)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0007"));
        }

        return new Bean(name, clazz);
    }

    private static Guice loadingGuice(Element element) {
        String tag = "<guice/>";
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", tag));
        }

        String clazz = getAttrValNoneNull(element, tag, "class");
        Guice guice = new Guice(clazz);
        ofNullable(element.attributeValue("name")).ifPresent(name -> guice.name = name);

        return guice;
    }

    private static Hsf loadingHsf(Element element) {
        String tag = "<hsf/>";
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", tag));
        }

        String service = getAttrValNoneNull(element, tag, "service");
        String version = getAttrValNoneNull(element, tag, "version");

        Hsf hsf = new Hsf(service, version);
        ofNullable(element.attributeValue("group")).ifPresent(group -> hsf.group = group);
        ofNullable(element.attributeValue("timeout")).map(Integer::valueOf).ifPresent(timeout -> hsf.timeout = timeout);

        return hsf;
    }

    private static Dubbo loadingDubbo(Element element) {
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", "<dubbo/>"));
        }

        String version = element.attributeValue("version");
        String group = element.attributeValue("group");
        if (StringUtils.isAllEmpty(version, group)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0008"));
        }

        Dubbo dubbo = new Dubbo(version, group);
        ofNullable(element.attributeValue("timeout")).map(Integer::valueOf).ifPresent(timeout -> dubbo.timeout = timeout);
        ofNullable(element.attributeValue("check")).map(Boolean::valueOf).ifPresent(check -> dubbo.check = check);
        ofNullable(element.attributeValue("filter")).ifPresent(filter -> dubbo.filter = filter);
        ofNullable(element.attributeValue("application-name")).ifPresent(applicationName -> dubbo.applicationName = applicationName);
        ofNullable(element.attributeValue("registry-address")).ifPresent(registryAddress -> dubbo.registryAddress = registryAddress);

        return dubbo;
    }

    private static Http loadingHttp(Element element) {
        String tag = "<http/>";
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", tag));
        }

        String schema = getAttrValNoneNull(element, tag, "schema");
        String host = getAttrValNoneNull(element, tag, "host");
        String path = getAttrValNoneNull(element, tag, "path");
        Http http = new Http(schema, host, path);

        ofNullable(element.attributeValue("port")).map(Integer::parseInt).ifPresent(port -> http.port = port);
        ofNullable(element.attributeValue("method")).ifPresent(method -> http.method = method);

        return http;
    }

    private static Groovy loadingGroovy(Element element) {
        String tag = "<groovy/>";
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", tag));
        }

        String protocol = getAttrValNoneNull(element, tag, "protocol");
        String path = getAttrValNoneNull(element, tag, "path");

        return new Groovy(protocol, path);
    }

    private static SpEL loadingSpEL(Element element) {
        String tag = "<spel/>";
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", tag));
        }

        String protocol = getAttrValNoneNull(element, tag, "protocol");
        String path = getAttrValNoneNull(element, tag, "path");

        return new SpEL(protocol, path);
    }

    private static Provider loadingProvider(Element element) {
        String tag = "<provider/>";
        if (element == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0003", tag));
        }

        String clazz = getAttrValNoneNull(element, tag, "class");
        String method = getAttrValNoneNull(element, tag, "method");

        Provider provider = new Provider(clazz, method);

        ofNullable(element.attributeValue("arg0")).ifPresent(arg0 -> provider.arg0 = arg0);

        return provider;
    }

    private static String getAttrValNoneNull(Element element, String tag, String attr) {
        String value = element.attributeValue(attr);
        if (StringUtils.isBlank(value)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0004", tag, attr));
        }
        return value;
    }

    private static class Wrapper implements Serializable {

        private static final long serialVersionUID = -287883352030585384L;

        public Iterable<String> scope;
        public Iterable<String> code;
        public int priority = 0;
        public String desc;
        public String type;
        public boolean lazy = false;

        public ObjectT object;
        public Bean bean;
        public Guice guice;
        public Hsf hsf;
        public Dubbo dubbo;
        public Http http;
        public Groovy groovy;
        public SpEL spel;
        public Provider provider;
    }
}
