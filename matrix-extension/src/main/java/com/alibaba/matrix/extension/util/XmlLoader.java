package com.alibaba.matrix.extension.util;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.factory.DubboServiceFactory;
import com.alibaba.matrix.extension.factory.GroovyServiceFactory;
import com.alibaba.matrix.extension.factory.HsfServiceFactory;
import com.alibaba.matrix.extension.factory.HttpServiceFactory;
import com.alibaba.matrix.extension.factory.ObjectInstanceFactory;
import com.alibaba.matrix.extension.factory.SpELServiceFactory;
import com.alibaba.matrix.extension.model.Bean;
import com.alibaba.matrix.extension.model.Dubbo;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Groovy;
import com.alibaba.matrix.extension.model.Hsf;
import com.alibaba.matrix.extension.model.Http;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.model.ObjectT;
import com.alibaba.matrix.extension.model.Scope;
import com.alibaba.matrix.extension.model.SpEL;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
 * @since 2023/8/11 19:38.
 */
public class XmlLoader {

    private static final String EXTENSION_XML_CONFIG_LOCATION_DEMO = "classpath*:matrix-extension-*.xml";

    private static final SAXReader saxReader = new SAXReader();

    public static Map<Class<?>, Extension> loadXml(List<String> configLocations, ApplicationContext applicationContext) {
        if (CollectionUtils.isEmpty(configLocations)) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0001").getMessage());
        }
        log.info("Xml configLocations: {}", configLocations);

        try {
            Map<Class<?>, Extension> extensionMap = new HashMap<>();
            for (String configLocation : configLocations) {
                try {
                    Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configLocation);
                    if (ArrayUtils.isEmpty(resources)) {
                        log.error("No resources founded by configLocation:[{}]", configLocation);
                        continue;
                    }

                    loadResources(resources, extensionMap, applicationContext);
                } catch (IOException e) {
                    log.error("Parse config:[{}] error.", configLocation, e);
                }
            }
            return extensionMap;
        } catch (Throwable t) {
            return ExceptionUtils.rethrow(t);
        }
    }

    public static void loadResources(Resource[] resources, Map<Class<?>, Extension> extensionMap, ApplicationContext applicationContext) throws Exception {
        for (Resource resource : resources) {
            String file = resource.getFilename();
            log.info("Loading extensions from config:[{}] ...", file);

            Element document = saxReader.read(resource.getInputStream()).getRootElement();
            loadExtensionsDocument(file, document, extensionMap, applicationContext);
        }
    }

    private static void loadExtensionsDocument(String file, Element document, Map<Class<?>, Extension> extensionMap, ApplicationContext applicationContext) throws Exception {
        for (Iterator<Element> iterator = document.elementIterator(); iterator.hasNext(); ) {
            Extension extension = loadExtensionElement(iterator.next(), applicationContext);
            if (extensionMap.containsKey(extension.clazz)) {
                throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0002", extension.clazz.getName(), file).getMessage());
            }
            extensionMap.put(extension.clazz, extension);
        }
    }


    private static Extension loadExtensionElement(Element extensionElement, ApplicationContext applicationContext) throws Exception {
        Class<?> ext = Class.forName(getAttrValNoneNull(extensionElement, "<Extension/>", "class"));

        // if (!ext.isInterface()) {
        //    throw new ExtensionException(String.format("<Extension/>: ext: [%s] is not a interface.", ext.getName()));
        // }

        String desc = extensionElement.attributeValue("desc");

        log.info("Loaded <Extension/>: [{}].", Logger.formatExt(ext, desc));

        Object base = applicationContext.getBean(getAttrValNoneNull(extensionElement, "<Extension/>", "base"));
        if (!ext.isInstance(base)) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0009", base, ext.getName()).getMessage());
        }
        log.info("Loaded <ExtensionBase/>: ext:[{}] base:[{}].", ext.getName(), Logger.formatBase(base));

        Map<String, Scope> scopeMap = loadExtensionScopeMap(ext, extensionElement, applicationContext);

        return new Extension(ext, desc, base, scopeMap);
    }

    private static Map<String, Scope> loadExtensionScopeMap(Class<?> ext, Element extensionElement, ApplicationContext applicationContext) throws Exception {

        Map<String, Map<String, List<Wrapper>>> scope2code2wrappers = new HashMap<>();
        for (Iterator<Element> iterator = extensionElement.elementIterator(); iterator.hasNext(); ) {
            Wrapper wrapper = loadExtensionImplElement(iterator.next());
            for (String scope : wrapper.scope) {
                for (String code : wrapper.code) {
                    scope2code2wrappers.computeIfAbsent(scope, _K -> new HashMap<>()).computeIfAbsent(code, _K -> new LinkedList<>()).add(wrapper);
                }
            }
        }

        Map<String, Scope> scopeMap = new HashMap<>();
        for (Map.Entry<String, Map<String, List<Wrapper>>> entry : scope2code2wrappers.entrySet()) {
            String scope = entry.getKey();
            Map<String, List<Wrapper>> code2wrappers = entry.getValue();

            scopeMap.put(scope, convertToExtensionScope(ext, scope, code2wrappers, applicationContext));
        }

        return scopeMap;
    }

    private static Wrapper loadExtensionImplElement(Element extensionImplElement) {
        Wrapper wrapper = new Wrapper();

        String scope = ofNullable(extensionImplElement.attributeValue("scope")).orElse(BASE_SCOPE);
        String code = getAttrValNoneNull(extensionImplElement, "<ExtensionImpl/>", "code");

        wrapper.scope = Splitter.on(",").trimResults().omitEmptyStrings().split(scope);
        wrapper.code = Splitter.on(",").trimResults().omitEmptyStrings().split(code);
        wrapper.desc = extensionImplElement.attributeValue("desc");
        wrapper.priority = ofNullable(extensionImplElement.attributeValue("priority")).map(Integer::valueOf).orElse(0);

        ExtImpl.Type type = ExtImpl.Type.fromStr(getAttrValNoneNull(extensionImplElement, "<ExtensionImpl/>", "type"));
        // 后续有可能得话, 可以把这部分搞成Jar包的扩展、XML的scope扩展
        wrapper.type = type.name();
        switch (type) {
            case OBJECT:
                wrapper.object = loadingObject(extensionImplElement.element("object"));
                break;
            case BEAN:
                wrapper.bean = loadingBean(extensionImplElement.element("bean"));
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
            default:
                throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0005", type).getMessage());
        }

        return wrapper;
    }

    private static Scope convertToExtensionScope(Class<?> ext, String scope, Map<String, List<Wrapper>> code2wrappers, ApplicationContext applicationContext) throws Exception {

        Map<String, List<Impl>> code2impls = new HashMap<>();

        for (String code : code2wrappers.keySet()) {
            List<Wrapper> wrappers = code2wrappers.get(code);
            // 先排序一次, 后序如果同code合并, 还需要再次排序
            wrappers.sort(Comparator.comparingInt(wrapper -> wrapper.priority));

            List<Impl> impls = new ArrayList<>(wrappers.size());
            for (Wrapper wrapper : wrappers) {
                Impl impl = convertToImpl(ext, scope, code, wrapper, applicationContext);

                if(impl.instance != null && !ext.isInstance(impl.instance)){
                    throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0010", impl.instance, ext.getName()).getMessage());
                }

                impls.add(impl);
            }

            code2impls.put(code, impls);
            log.info("Loaded <ExtensionImpl/>: ext:[{}] scope:[{}] code:[{}] -> [{}].", ext.getName(), scope, code, impls.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));
        }

        return new Scope(scope, code2impls);
    }

    private static Impl convertToImpl(Class<?> ext, String scope, String code, Wrapper wrapper, ApplicationContext applicationContext) throws Exception {

        if (wrapper.object != null) {
            Impl impl = new Impl(scope, code, wrapper.type, wrapper.priority, wrapper.desc);
            impl.object = wrapper.object;
            impl.instance = getObjectInstance(wrapper.object);
            return impl;
        }

        if (wrapper.bean != null) {
            Impl impl = new Impl(scope, code, wrapper.type, wrapper.priority, wrapper.desc);
            impl.bean = wrapper.bean;
            impl.instance = getSpringBean(wrapper.bean, applicationContext);
            return impl;
        }

        if (wrapper.hsf != null) {
            Impl impl = new Impl(scope, code, wrapper.type, wrapper.priority, wrapper.desc);
            impl.hsf = wrapper.hsf;
            impl.instance = getHsfService(wrapper.hsf);
            return impl;
        }

        if (wrapper.dubbo != null) {
            Impl impl = new Impl(scope, code, wrapper.type, wrapper.priority, wrapper.desc);
            impl.dubbo = wrapper.dubbo;
            impl.instance = getDubboService(ext, wrapper.dubbo);
            return impl;
        }

        if (wrapper.http != null) {
            Impl impl = new Impl(scope, code, wrapper.type, wrapper.priority, wrapper.desc);
            impl.http = wrapper.http;
            impl.instance = getHttpService(ext, wrapper.http);
            return impl;
        }

        if (wrapper.groovy != null) {
            Impl impl = new Impl(scope, code, wrapper.type, wrapper.priority, wrapper.desc);
            impl.groovy = wrapper.groovy;
            impl.instance = getGroovyService(ext, wrapper.groovy);
            return impl;
        }

        if (wrapper.spel != null) {
            Impl impl = new Impl(scope, code, wrapper.type, wrapper.priority, wrapper.desc);
            impl.spel = wrapper.spel;
            impl.instance = getSpELService(ext, wrapper.spel);
            return impl;
        }

        throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0006", wrapper.type).getMessage());
    }

    private static ObjectT loadingObject(Element element) {
        String tag = "<object/>";
        if (element == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0003", tag).getMessage());
        }

        String clazz = getAttrValNoneNull(element, tag, "class");
        ObjectT object = new ObjectT(clazz);
        ofNullable(element.attributeValue("args")).ifPresent(args -> object.args = args);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> object.lazy = lazy);

        return object;
    }

    private static Bean loadingBean(Element element) {
        if (element == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0003", "<bean/>").getMessage());
        }

        String name = element.attributeValue("name");
        String clazz = element.attributeValue("class");
        if (StringUtils.isAllEmpty(name, clazz)) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0007").getMessage());
        }

        Bean bean = new Bean(name, clazz);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> bean.lazy = lazy);

        return bean;
    }

    private static Hsf loadingHsf(Element element) {
        String tag = "<hsf/>";
        if (element == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0003", tag).getMessage());
        }

        String service = getAttrValNoneNull(element, tag, "service");
        String version = getAttrValNoneNull(element, tag, "version");

        Hsf hsf = new Hsf(service, version);
        ofNullable(element.attributeValue("group")).ifPresent(group -> hsf.group = group);
        ofNullable(element.attributeValue("timeout")).map(Integer::valueOf).ifPresent(timeout -> hsf.timeout = timeout);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> hsf.lazy = lazy);

        return hsf;
    }

    private static Dubbo loadingDubbo(Element element) {
        if (element == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0003", "<dubbo/>").getMessage());
        }

        String version = element.attributeValue("version");
        String group = element.attributeValue("group");
        if (StringUtils.isAllEmpty(version, group)) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0008").getMessage());
        }

        Dubbo dubbo = new Dubbo(version, group);
        ofNullable(element.attributeValue("timeout")).map(Integer::valueOf).ifPresent(timeout -> dubbo.timeout = timeout);
        ofNullable(element.attributeValue("check")).map(Boolean::valueOf).ifPresent(check -> dubbo.check = check);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> dubbo.lazy = lazy);
        ofNullable(element.attributeValue("filter")).ifPresent(filter -> dubbo.filter = filter);

        return dubbo;
    }

    private static Http loadingHttp(Element element) {
        String tag = "<http/>";
        if (element == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0003", tag).getMessage());
        }

        String schema = getAttrValNoneNull(element, tag, "schema");
        String host = getAttrValNoneNull(element, tag, "host");
        String path = getAttrValNoneNull(element, tag, "path");
        Http http = new Http(schema, host, path);

        ofNullable(element.attributeValue("port")).map(Integer::parseInt).ifPresent(port -> http.port = port);
        ofNullable(element.attributeValue("method")).ifPresent(method -> http.method = method);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> http.lazy = lazy);

        return http;
    }

    private static Groovy loadingGroovy(Element element) {
        String tag = "<groovy/>";
        if (element == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0003", tag).getMessage());
        }

        String protocol = getAttrValNoneNull(element, tag, "protocol");
        String path = getAttrValNoneNull(element, tag, "path");

        Groovy groovy = new Groovy(protocol, path);

        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> groovy.lazy = lazy);

        return groovy;
    }

    private static SpEL loadingSpEL(Element element) {
        String tag = "<spel/>";
        if (element == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0003", tag).getMessage());
        }

        String protocol = getAttrValNoneNull(element, tag, "protocol");
        String path = getAttrValNoneNull(element, tag, "path");

        SpEL spel = new SpEL(protocol, path);

        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> spel.lazy = lazy);

        return spel;
    }

    private static String getAttrValNoneNull(Element element, String tag, String attr) {
        String value = element.attributeValue(attr);
        if (StringUtils.isBlank(value)) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0004", tag, attr).getCode());
        }
        return value;
    }

    private static Object getObjectInstance(ObjectT object) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!object.lazy) {
            Preconditions.checkState(StringUtils.isNotEmpty(object.clazz));
            return ObjectInstanceFactory.newInstance(object);
        }
        return null;
    }

    private static Object getSpringBean(Bean bean, ApplicationContext applicationContext) throws ClassNotFoundException {
        if (!bean.lazy) {
            Preconditions.checkState(!(StringUtils.isAllEmpty(bean.name, bean.clazz)));
            if (bean.name != null) {
                return applicationContext.getBean(bean.name);
            } else {
                return applicationContext.getBean(Class.forName(bean.clazz));
            }
        }
        return null;
    }

    private static Object getHsfService(Hsf hsf) {
        if (!hsf.lazy) {
            return HsfServiceFactory.getHsfService(hsf);
        }
        return null;
    }

    private static Object getDubboService(Class<?> ext, Dubbo dubbo) {
        if (!dubbo.lazy) {
            return DubboServiceFactory.getDubboService(ext, dubbo);
        }
        return null;
    }

    private static Object getHttpService(Class<?> ext, Http http) {
        if (!http.lazy) {
            return HttpServiceFactory.getHttpService(ext, http);
        }
        return null;
    }

    private static Object getGroovyService(Class<?> ext, Groovy groovy) {
        if (!groovy.lazy) {
            return GroovyServiceFactory.getGroovyService(ext, groovy);
        }
        return null;
    }

    private static Object getSpELService(Class<?> ext, SpEL spel) {
        if (!spel.lazy) {
            return SpELServiceFactory.getSpELService(ext, spel);
        }
        return null;
    }

    private static class Wrapper implements Serializable {

        private static final long serialVersionUID = -287883352030585384L;

        public Iterable<String> scope;
        public Iterable<String> code;
        public int priority;
        public String desc;
        public String type;

        public ObjectT object;
        public Bean bean;
        public Hsf hsf;
        public Dubbo dubbo;
        public Http http;
        public Groovy groovy;
        public SpEL spel;
    }
}
