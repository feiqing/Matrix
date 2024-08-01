package com.alibaba.matrix.extension.utils;

import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.factory.DubboServiceFactory;
import com.alibaba.matrix.extension.factory.GroovyServiceFactory;
import com.alibaba.matrix.extension.factory.HsfServiceFactory;
import com.alibaba.matrix.extension.factory.HttpServiceFactory;
import com.alibaba.matrix.extension.model.Bean;
import com.alibaba.matrix.extension.model.Dubbo;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Groovy;
import com.alibaba.matrix.extension.model.Hsf;
import com.alibaba.matrix.extension.model.Http;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.model.Scope;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_SCOPE;
import static com.alibaba.matrix.extension.utils.Logger.log;
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
            throw new ExtensionException("Xml configLocations Must not be empty.");
        }
        // todo msg
        log.info("Xml configLocations: {}", configLocations);

        try {
            Map<Class<?>, Extension> extensionMap = new HashMap<>();
            for (String configLocation : configLocations) {
                try {
                    Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configLocation);
                    if (resources == null || resources.length == 0) {
                        // todo msg
                        log.error("No resources founded by configLocation:[{}]", configLocation);
                        continue;
                    }

                    loadResources(resources, extensionMap, applicationContext);
                } catch (IOException e) {
                    // todo msg
                    log.error("parse configLocation:[{}] error.", configLocation, e);
                }
            }
            return extensionMap;
        } catch (ClassNotFoundException e) {
            throw new ExtensionException(e);
        }
    }

    public static void loadResources(Resource[] resources, Map<Class<?>, Extension> extensionMap, ApplicationContext applicationContext) throws ClassNotFoundException {
        for (Resource resource : resources) {
            String file = resource.getFilename();
            // todo msg
            log.info("Loading extensions from file:[{}] ...", file);

            try {
                Element document = saxReader.read(resource.getInputStream()).getRootElement();
                loadExtensionsDocument(file, document, extensionMap, applicationContext);
            } catch (DocumentException | IOException e) {
                // todo msg
                log.error("read file:[{}] error", file, e);
            }
        }
    }

    private static void loadExtensionsDocument(String file, Element document, Map<Class<?>, Extension> extensionMap, ApplicationContext applicationContext) throws ClassNotFoundException {
        for (Iterator<Element> iterator = document.elementIterator(); iterator.hasNext(); ) {
            Extension extension = loadExtensionElement(file, iterator.next(), applicationContext);
            if (extensionMap.containsKey(extension.clazz)) {
                // todo msg
                throw new ExtensionException("duplicated in file" + file);
            }
            extensionMap.put(extension.clazz, extension);
        }
    }


    private static Extension loadExtensionElement(String file, Element extensionElement, ApplicationContext applicationContext) throws ClassNotFoundException {
        Class<?> ext = Class.forName(getAttrValNoneNull(extensionElement, file, "<Extension/>", "class"));

        // todo 需要吗?
        if (!ext.isInterface()) {
            // todo msg
            throw new ExtensionException(String.format("<Extension/>: ext: [%s] is not a interface.", ext.getName()));
        }

        String desc = extensionElement.attributeValue("desc");

        log.info("Loaded <Extension/>: [{}].", Logger.formatExt(ext, desc));

        Object base = applicationContext.getBean(getAttrValNoneNull(extensionElement, file, "<Extension/>", "base"));
        log.info("Loaded <ExtensionBase/>: ext:[{}] base:[{}].", ext.getName(), Logger.formatBase(base));

        Map<String, Scope> scopeMap = loadExtensionScopeMap(ext, file, extensionElement, applicationContext);

        return new Extension(ext, desc, base, scopeMap);
    }

    private static Map<String, Scope> loadExtensionScopeMap(Class<?> ext, String file, Element extensionElement, ApplicationContext applicationContext) throws ClassNotFoundException {

        Map<String, Map<String, List<Wrapper>>> scope2code2wrappers = new HashMap<>();
        for (Iterator<Element> iterator = extensionElement.elementIterator(); iterator.hasNext(); ) {
            Wrapper wrapper = loadExtensionImplElement(file, iterator.next());
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

    private static Wrapper loadExtensionImplElement(String file, Element extensionImplElement) {
        Wrapper wrapper = new Wrapper();

        String scope = ofNullable(extensionImplElement.attributeValue("scope")).orElse(BASE_SCOPE);
        String code = getAttrValNoneNull(extensionImplElement, file, "<ExtensionImpl/>", "code");

        wrapper.scope = Splitter.on(",").trimResults().omitEmptyStrings().split(scope);
        wrapper.code = Splitter.on(",").trimResults().omitEmptyStrings().split(code);
        wrapper.desc = extensionImplElement.attributeValue("desc");
        wrapper.priority = ofNullable(extensionImplElement.attributeValue("priority")).map(Integer::valueOf).orElse(0);

        ExtImpl.Type type = ExtImpl.Type.fromStr(getAttrValNoneNull(extensionImplElement, file, "<ExtensionImpl/>", "type"));
        wrapper.type = type.name();
        switch (type) {
            case BEAN:
                wrapper.bean = loadingBean(file, extensionImplElement.element("bean"));
                break;
            case HSF:
                wrapper.hsf = loadingHsf(file, extensionImplElement.element("hsf"));
                break;
            case DUBBO:
                wrapper.dubbo = loadingDubbo(file, extensionImplElement.element("dubbo"));
                break;
            case HTTP:
                wrapper.http = loadingHttp(file, extensionImplElement.element("http"));
                break;
            case GROOVY:
                wrapper.groovy = loadingGroovy(file, extensionImplElement.element("groovy"));
                break;
            default:
                throw new ExtensionException(String.format("type:[%s] not support for scope:[%s] code:[%s] in file:[%s].", type, scope, code, file));
        }

        return wrapper;
    }

    private static Scope convertToExtensionScope(Class<?> ext, String scope, Map<String, List<Wrapper>> code2wrappers, ApplicationContext applicationContext) throws ClassNotFoundException {

        Map<String, List<Impl>> code2impls = new HashMap<>();

        for (String code : code2wrappers.keySet()) {
            List<Wrapper> wrappers = code2wrappers.get(code);
            // 先排序一次, 后序如果同code合并, 还需要再次排序
            wrappers.sort(Comparator.comparingInt(wrapper -> wrapper.priority));

            List<Impl> impls = new ArrayList<>(wrappers.size());
            for (Wrapper wrapper : wrappers) {
                impls.add(convertToImpl(ext, scope, code, wrapper, applicationContext));
            }

            code2impls.put(code, impls);
            log.info("Loaded <ExtensionImpl/>: ext:[{}] scope:[{}] code:[{}] -> [{}].", ext.getName(), scope, code, impls.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));
        }

        return new Scope(scope, code2impls);
    }

    private static Impl convertToImpl(Class<?> ext, String scope, String code, Wrapper wrapper, ApplicationContext applicationContext) throws ClassNotFoundException {
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

        // todo not found
        throw new ExtensionException("Could not resolve impl type: " + wrapper.type + " .");
    }

    private static Bean loadingBean(String file, Element element) {
        String tag = "<bean/>";
        if (element == null) {
            throw new ExtensionException(String.format("Tag %s definition can not be null in file:[%s].", tag, file));
        }

        String name = element.attributeValue("name");
        String clazz = element.attributeValue("class");
        if (StringUtils.isAllEmpty(name, clazz)) {
            // todo msg 不能全为空
            throw new ExtensionException("Bean name and class could not all empty.");
        }

        Bean bean = new Bean(name, clazz);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> bean.lazy = lazy);

        return bean;
    }

    private static Hsf loadingHsf(String file, Element element) {
        String tag = "<hsf/>";
        if (element == null) {
            throw new ExtensionException(String.format("Tag %s definition can not be null in file:[%s].", tag, file));
        }

        String service = getAttrValNoneNull(element, file, tag, "service");
        String version = getAttrValNoneNull(element, file, tag, "version");

        Hsf hsf = new Hsf(service, version);
        ofNullable(element.attributeValue("group")).ifPresent(group -> hsf.group = group);
        ofNullable(element.attributeValue("timeout")).map(Integer::valueOf).ifPresent(timeout -> hsf.timeout = timeout);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> hsf.lazy = lazy);

        return hsf;
    }

    private static Dubbo loadingDubbo(String file, Element element) {
        String tag = "<dubbo/>";
        if (element == null) {
            throw new ExtensionException(String.format("Tag %s definition can not be null in file:[%s].", tag, file));
        }

        String version = element.attributeValue("version");
        String group = element.attributeValue("group");
        if (StringUtils.isAllEmpty(version, group)) {
            // todo msg 不能全为空
            throw new ExtensionException("Dubbo version and group all empty.");
        }

        Dubbo dubbo = new Dubbo(version, group);
        ofNullable(element.attributeValue("timeout")).map(Integer::valueOf).ifPresent(timeout -> dubbo.timeout = timeout);
        ofNullable(element.attributeValue("check")).map(Boolean::valueOf).ifPresent(check -> dubbo.check = check);
        ofNullable(element.attributeValue("lazy")).map(Boolean::valueOf).ifPresent(lazy -> dubbo.lazy = lazy);
        ofNullable(element.attributeValue("filter")).ifPresent(filter -> dubbo.filter = filter);

        return dubbo;
    }

    private static Http loadingHttp(String file, Element element) {
        String tag = "<http/>";
        if (element == null) {
            throw new ExtensionException(String.format("Tag %s definition can not be null in file:[%s].", tag, file));
        }

        String url = getAttrValNoneNull(element, file, tag, "url");
        Http http = new Http(url);

        ofNullable(element.attributeValue("method")).ifPresent(method -> http.method = method);
        ofNullable(element.attributeValue("camelToUnderline")).map(Boolean::valueOf).ifPresent(camelToUnderline -> http.camelToUnderline = camelToUnderline);

        return http;
    }

    private static Groovy loadingGroovy(String file, Element element) {
        String tag = "<groovy/>";
        if (element == null) {
            throw new ExtensionException(String.format("Tag %s definition can not be null in file:[%s].", tag, file));
        }

        String protocol = getAttrValNoneNull(element, file, tag, "protocol");
        String path = getAttrValNoneNull(element, file, tag, "path");

        return new Groovy(protocol, path);
    }

    private static String getAttrValNoneNull(Element element, String file, String tag, String attr) {
        String value = element.attributeValue(attr);
        if (Strings.isNullOrEmpty(value)) {
            throw new ExtensionException(String.format("Tag: %s attr '%s' can not be empty in file:[%s].", tag, attr, file));
        }
        return value;
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
        return HttpServiceFactory.getHttpService(ext, http);
    }

    private static Object getGroovyService(Class<?> ext, Groovy groovy) {
        return GroovyServiceFactory.getGroovyService(ext, groovy);
    }

    private static class Wrapper implements Serializable {

        private static final long serialVersionUID = -287883352030585384L;

        public Iterable<String> scope;
        public Iterable<String> code;
        public int priority;
        public String desc;
        public String type;

        public Bean bean;
        public Hsf hsf;
        public Dubbo dubbo;
        public Http http;
        public Groovy groovy;
    }
}
