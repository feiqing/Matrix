package com.alibaba.matrix.extension.util;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.model.Bean;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.model.Scope;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.model.ExtImpl.Type.BEAN;
import static com.alibaba.matrix.extension.util.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class AnnotationLoader {

    public static Map<Class<?>, Extension> loadAnnotation(List<String> scanPackages, ApplicationContext applicationContext) {
        if (CollectionUtils.isEmpty(scanPackages)) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0002-0001").getMessage());
        }
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

            if (extensionBase.belong() != Object.class && extensionBase.belong() != extension) {
                log.info("@ExtensionBase: bean:[{}] type:[{}] for ext:[{}].", bean, bean.getClass().getName(), extensionBase.belong().getName());
                continue;
            }

            if (extensionBase.belong() == Object.class || extensionBase.belong() == extension) {
                log.info("Loaded @ExtensionBase: ext:[{}] base:[{}].", extension.getName(), Logger.formatBase(bean));
                if (baseImpl != null) {
                    throw new ExtensionException(Message.of("MATRIX-EXTENSION-0002-0002", extension.getName(), baseImpl).getMessage());
                }
                baseImpl = bean;
            }
        }

        if (baseImpl == null) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0002-0003", extension.getName()).getMessage());
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

            if (extensionImpl.belong() != Object.class && extensionImpl.belong() != extension) {
                log.info("@ExtensionImpl: bean:[{}] type:[{}] belongs ext:[{}].", bean, bean.getClass().getName(), extensionImpl.belong().getName());
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
            // if (!clazz.isInterface()) {
            //    throw new ExtensionException(String.format("@Extension:[%s] is not a interface.", clazz.getName()));
            // }

            String desc = clazz.getAnnotation(com.alibaba.matrix.extension.annotation.Extension.class).desc();

            log.info("Loaded @Extension: [{}].", Logger.formatExt(clazz, desc));
            extensions.put(clazz, desc);
        }

        if (extensions.isEmpty()) {
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
