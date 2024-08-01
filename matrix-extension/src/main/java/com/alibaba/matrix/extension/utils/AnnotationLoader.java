package com.alibaba.matrix.extension.utils;

import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.model.Bean;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.model.Scope;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.model.ExtImpl.Type.BEAN;
import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/30 16:45.
 */
public class AnnotationLoader {

    public static Map<Class<?>, Extension> loadAnnotation(List<String> scanPackages, ApplicationContext applicationContext) {
        if (CollectionUtils.isEmpty(scanPackages)) {
            throw new ExtensionException("Annotation scanPackages MUST not be empty.");
        }
        // todo msg
        log.info("Annotation scanPackages: {}", scanPackages);

        Map<Class<?>, String> extensions = loadExtensionDefs(scanPackages);
        Map<Class<?>, Extension> extensionMap = new HashMap<>(extensions.size());

        for (Map.Entry<Class<?>, String> entry : extensions.entrySet()) {
            Class<?> extension = entry.getKey();
            String desc = entry.getValue();
            Object base = loadExtensionBaseImpl(extension, applicationContext);
            Map<String, Scope> scopeMap = loadExtensionScopeMap(extension, applicationContext);
            extensionMap.put(extension, new Extension(extension, desc, base, scopeMap));
        }

        return extensionMap;
    }

    private static Object loadExtensionBaseImpl(Class<?> extension, ApplicationContext applicationContext) {
        Object baseImpl = null;
        for (Object bean : applicationContext.getBeansOfType(extension).values()) {
            ExtensionBase extensionBase = AopUtils.getTargetClass(bean).getAnnotation(ExtensionBase.class);
            if (extensionBase == null) {
                continue;
            }

            // Preconditions.checkState(extensionBase.extension() == Object.class || extensionBase.extension().isAnnotationPresent(Extension.class));

            if (extensionBase.extension() != Object.class && extensionBase.extension() != extension) {
                log.debug("@ExtensionBase: bean:[{}] type:[{}] for ext:[{}].", bean, bean.getClass().getName(), extensionBase.extension().getName());
                continue;
            }

            if (extensionBase.extension() == Object.class || extensionBase.extension() == extension) {
                log.info("Loaded @ExtensionBase: ext:[{}] base:[{}].", extension.getName(), Logger.formatBase(bean));
                if (baseImpl != null) {
                    throw new ExtensionException(String.format("@ExtensionBase: ext:[%s] duplicated with bean:[%s] type:[%s].", extension.getName(), baseImpl, baseImpl.getClass().getName()));
                }
                baseImpl = bean;
            }
        }

        if (baseImpl == null) {
            // todo msg
            throw new ExtensionException(String.format("Could not found base impl for extension:[%s]", extension.getName()));
        }

        return baseImpl;
    }

    private static Map<String, Scope> loadExtensionScopeMap(Class<?> extension, ApplicationContext applicationContext) {

        Map<String, Map<String, List<Wrapper>>> scope2code2wrappers = new HashMap<>();
        for (Map.Entry<String, ?> entry : applicationContext.getBeansOfType(extension).entrySet()) {
            String name = entry.getKey();
            Object bean = entry.getValue();
            ExtensionImpl extensionImpl = AopUtils.getTargetClass(bean).getAnnotation(ExtensionImpl.class);
            if (extensionImpl == null) {
                continue;
            }

            if (extensionImpl.extension() != Object.class && extensionImpl.extension() != extension) {
                log.debug("@ExtensionImpl: bean:[{}] type:[{}] for ext:[{}].", bean, bean.getClass().getName(), extensionImpl.extension().getName());
                continue;
            }

            for (String scope : extensionImpl.scope()) {
                for (String code : extensionImpl.code()) {
                    Wrapper wrapper = new Wrapper();
                    wrapper.name = name;
                    wrapper.bean = bean;
                    wrapper.priority = extensionImpl.priority();
                    wrapper.desc = extensionImpl.desc();
                    scope2code2wrappers.computeIfAbsent(scope, _K -> new HashMap<>()).computeIfAbsent(code, _k -> new LinkedList<>()).add(wrapper);
                }
            }
        }

        Map<String, Scope> scopeMap = new HashMap<>();
        for (Map.Entry<String, Map<String, List<Wrapper>>> entry : scope2code2wrappers.entrySet()) {
            String scope = entry.getKey();
            Map<String, List<Wrapper>> code2wrappers = entry.getValue();

            scopeMap.put(scope, loadExtensionScope(extension, scope, code2wrappers));
        }

        return scopeMap;
    }

    private static Scope loadExtensionScope(Class<?> ext, String scope, Map<String, List<Wrapper>> code2wrappers) {

        Map<String, List<Impl>> code2impls = new HashMap<>();

        for (String code : code2wrappers.keySet()) {
            List<Wrapper> wrappers = code2wrappers.get(code);
            // 先排序一次, 后序如果同code合并, 还需要再次排序
            wrappers.sort(Comparator.comparingInt(wrapper -> wrapper.priority));

            List<Impl> impls = new ArrayList<>(wrappers.size());
            for (Wrapper wrapper : wrappers) {

                Impl impl = new Impl(scope, code, BEAN.name(), wrapper.priority, wrapper.desc);
                impl.bean = new Bean(wrapper.name, wrapper.bean.getClass().getName());
                impl.instance = wrapper.bean;

                impls.add(impl);
            }

            code2impls.put(code, impls);
            log.info("Loaded @ExtensionImpl: ext:[{}] scope:[{}] code:[{}] -> [{}].", ext.getName(), scope, code, impls.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));
        }

        return new Scope(scope, code2impls);
    }

    private static Map<Class<?>, String> loadExtensionDefs(List<String> scanPackages) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(scanPackages.toArray(new String[0])));

        Map<Class<?>, String> extensions = new HashMap<>();
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(com.alibaba.matrix.extension.annotation.Extension.class)) {
            // 防止继承Extension的实现类也被加载进来
            if (!clazz.isAnnotationPresent(com.alibaba.matrix.extension.annotation.Extension.class)) {
                continue;
            }
            // todo 需要吗?
            if (!clazz.isInterface()) {
                // todo msg
                throw new ExtensionException(String.format("@Extension:[%s] is not a interface.", clazz.getName()));
            }

            String desc = clazz.getAnnotation(com.alibaba.matrix.extension.annotation.Extension.class).desc();

            log.info("Loaded @Extension: [{}].", Logger.formatExt(clazz, desc));
            extensions.put(clazz, desc);
        }

        if (extensions.isEmpty()) {
            // todo msg
            log.warn("No extension found by @Extension annotation.");
        }

        return extensions;
    }

    private static class Wrapper implements Serializable {
        private static final long serialVersionUID = -7603465850850920008L;
        public String name;
        public Object bean;
        public int priority;
        public String desc;
    }
}
