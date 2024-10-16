package com.alibaba.matrix.extension.util;

import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.factory.GuiceInstanceFactory;
import com.alibaba.matrix.extension.factory.ObjectInstanceFactory;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.model.ExtensionImplType;
import com.alibaba.matrix.extension.model.config.Bean;
import com.alibaba.matrix.extension.model.config.Extension;
import com.alibaba.matrix.extension.model.config.ExtensionImpl;
import com.alibaba.matrix.extension.model.config.ExtensionScope;
import com.alibaba.matrix.extension.model.config.Guice;
import com.alibaba.matrix.extension.model.config.ObjectT;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.util.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class AnnotationLoader {

    public static Map<Class<?>, Extension> loadAnnotation(List<String> scanPackages) {
        if (CollectionUtils.isEmpty(scanPackages)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0002-0001"));
        }
        log.info("Annotation scanPackages: {}", scanPackages);

        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(scanPackages.toArray(new String[0])).scan()) {
            return loadExtensions(scanResult);
        } catch (Throwable t) {
            return ExceptionUtils.rethrow(t);
        }
    }

    private static Map<Class<?>, Extension> loadExtensions(ScanResult scanResult) throws Exception {
        Map<Class<?>, Extension> extensionMap = new LinkedHashMap<>();

        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.alibaba.matrix.extension.annotation.Extension.class)) {
            Class<?> extension = classInfo.loadClass();
            com.alibaba.matrix.extension.annotation.Extension extensionAnnotation = extension.getAnnotation(com.alibaba.matrix.extension.annotation.Extension.class);
            if (extensionAnnotation == null) {
                continue;
            }
            // if (!extension.isInterface()) {
            //    throw new ExtensionException(String.format("@Extension:[%s] is not a interface.", extension.getName()));
            // }

            String desc = extensionAnnotation.desc();
            log.info("Loaded @Extension: [{}].", Logger.formatExt(extension, desc));

            Object base = loadExtensionBase(extension, scanResult);
            Map<String, ExtensionScope> scopeMap = loadExtensionScopeMap(extension, scanResult);

            extensionMap.put(extension, new Extension(extension, desc, base, scopeMap));
        }

        if (extensionMap.isEmpty()) {
            log.warn("No extension found by @Extension annotation.");
        }

        return extensionMap;
    }

    private static Object loadExtensionBase(Class<?> extension, ScanResult scanResult) throws Exception {
        Class<?> baseClazz = null;
        ExtensionImplType baseType = null;
        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(ExtensionBase.class)) {
            if (!classInfo.extendsSuperclass(extension) && !classInfo.implementsInterface(extension)) {
                continue;
            }
            Class<?> clazz = classInfo.loadClass();
            if (!extension.isAssignableFrom(clazz)) {
                continue;
            }
            ExtensionBase extensionBase = clazz.getAnnotation(ExtensionBase.class);
            if (extensionBase == null) {
                continue;
            }

            Class<?> belong = extensionBase.belong();
            if (belong != Object.class && belong != extension) {
                log.info("@ExtensionBase: type:[{}] for extension:[{}].", clazz.getName(), belong.getName());
                continue;
            }

            if (baseClazz != null) {
                throw new ExtensionException(Message.format("MATRIX-EXTENSION-0002-0002", extension.getName()));
            }
            baseClazz = clazz;
            baseType = extensionBase.type();
        }

        if (baseClazz == null || baseType == null) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0002-0003", extension.getName()));
        }

        Object base;
        switch (baseType) {
            case OBJECT:
                base = ObjectInstanceFactory.getObjectInstance(new ObjectT(baseClazz.getName()));
                break;
            case BEAN:
                base = SpringBeanFactory.getSpringBean(new Bean(baseClazz.getName()));
                break;
            case GUICE:
                base = GuiceInstanceFactory.getGuiceInstance(new Guice(baseClazz.getName()));
                break;
            default:
                throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0012", baseType));
        }

        if (!extension.isInstance(base)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0009", base, extension.getName()));
        }
        log.info("Loaded @ExtensionBase: extension:[{}] base:[{}].", extension.getName(), Logger.formatBase(base));

        return base;
    }

    private static Map<String, ExtensionScope> loadExtensionScopeMap(Class<?> extension, ScanResult scanResult) throws Exception {

        Map<String, Map<String, List<Wrapper>>> scope2code2wrappers = new LinkedHashMap<>();
        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.alibaba.matrix.extension.annotation.ExtensionImpl.class)) {
            if (!classInfo.extendsSuperclass(extension) && !classInfo.implementsInterface(extension)) {
                continue;
            }
            Class<?> clazz = classInfo.loadClass();
            if (!extension.isAssignableFrom(clazz)) {
                continue;
            }
            com.alibaba.matrix.extension.annotation.ExtensionImpl extensionImpl = clazz.getAnnotation(com.alibaba.matrix.extension.annotation.ExtensionImpl.class);
            if (extensionImpl == null) {
                continue;
            }

            Class<?> belong = extensionImpl.belong();
            if (belong != Object.class && belong != extension) {
                log.info("@ExtensionImpl: type:[{}] belongs extension:[{}].", clazz.getName(), belong.getName());
                continue;
            }

            Wrapper wrapper = loadImplWrapper(clazz, extensionImpl);
            for (String scope : wrapper.scope) {
                for (String code : wrapper.code) {
                    scope2code2wrappers.computeIfAbsent(scope, _K -> new LinkedHashMap<>()).computeIfAbsent(code, _K -> new LinkedList<>()).add(wrapper);
                }
            }
        }

        if (MapUtils.isEmpty(scope2code2wrappers)) {
            log.warn("{}", Message.format("MATRIX-EXTENSION-0002-0004", extension.getName()));
        }

        Map<String, ExtensionScope> scopeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, List<Wrapper>>> entry : scope2code2wrappers.entrySet()) {
            String scope = entry.getKey();
            Map<String, List<Wrapper>> code2wrappers = entry.getValue();

            scopeMap.put(scope, convertToExtensionScope(extension, scope, code2wrappers));
        }

        return scopeMap;
    }

    private static Wrapper loadImplWrapper(Class<?> impl, com.alibaba.matrix.extension.annotation.ExtensionImpl extensionImpl) {
        Wrapper wrapper = new Wrapper();
        wrapper.scope = extensionImpl.scope();
        wrapper.code = extensionImpl.code();
        wrapper.type = extensionImpl.type();
        wrapper.priority = extensionImpl.priority();
        wrapper.lazy = extensionImpl.lazy();
        wrapper.desc = extensionImpl.desc();

        switch (extensionImpl.type()) {
            case OBJECT:
                wrapper.object = new ObjectT(impl.getName());
                break;
            case BEAN:
                wrapper.bean = new Bean(impl.getName());
                break;
            case GUICE:
                wrapper.guice = new Guice(impl.getName());
                break;
            default:
                throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0005", extensionImpl.type().name()));
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
            log.info("Loaded @ExtensionImpl: extension:[{}] scope:[{}] code:[{}] -> [{}].", extension.getName(), scope, code, impls.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));
        }

        return new ExtensionScope(scope, code2impls);
    }

    private static ExtensionImpl convertToImpl(Class<?> extension, String scope, String code, Wrapper wrapper) throws Exception {

        if (wrapper.object != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type.name(), wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.object = wrapper.object;
            impl.instance = wrapper.lazy ? null : ObjectInstanceFactory.getObjectInstance(wrapper.object);
            return impl;
        }

        if (wrapper.bean != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type.name(), wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.bean = wrapper.bean;
            impl.instance = wrapper.lazy ? null : SpringBeanFactory.getSpringBean(wrapper.bean);
            return impl;
        }

        if (wrapper.guice != null) {
            ExtensionImpl impl = new ExtensionImpl(scope, code, wrapper.type.name(), wrapper.priority, wrapper.lazy, wrapper.desc);
            impl.guice = wrapper.guice;
            impl.instance = wrapper.lazy ? null : GuiceInstanceFactory.getGuiceInstance(wrapper.guice);
            return impl;
        }

        throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0005", wrapper.type));
    }

    private static class Wrapper implements Serializable {
        private static final long serialVersionUID = -7603465850850920008L;
        public String[] scope;
        public String[] code;
        public int priority = 0;
        public String desc;
        public ExtensionImplType type;
        public boolean lazy = false;

        public ObjectT object;
        public Bean bean;
        public Guice guice;
    }
}
