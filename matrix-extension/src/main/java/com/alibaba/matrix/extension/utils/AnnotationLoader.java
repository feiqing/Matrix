package com.alibaba.matrix.extension.utils;

import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.model.Bean;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Group;
import com.alibaba.matrix.extension.model.Impl;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.model.ExtImplType.BEAN;
import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/30 16:45.
 */
public class AnnotationLoader {

    public static Map<Class<?>, Extension> loadAnnotation(Map<Class<?>, Extension> externalExtensions, List<String> scanPackages, ApplicationContext applicationContext) {
        if (CollectionUtils.isEmpty(scanPackages)) {
            // todo msg
            throw new ExtensionException("Annotation scanPackages must contain at least one package.");
        }
        // todo msg
        log.info("Annotation scanPackages: {}", scanPackages);

        Set<Class<?>> extensions = loadExtensionDefs(externalExtensions.keySet(), scanPackages);
        Map<Class<?>, Extension> extensionMap = new HashMap<>(extensions.size());

        for (Class<?> extension : extensions) {
            Object base = loadExtensionBaseImpl(extension, applicationContext, externalExtensions);
            Map<String, Group> groupMap = loadExtensionGroupMap(extension, applicationContext);
            extensionMap.put(extension, new Extension(extension, base, groupMap));
        }

        return extensionMap;
    }

    private static Object loadExtensionBaseImpl(Class<?> extension, ApplicationContext applicationContext, Map<Class<?>, Extension> externalExtensions) {
        for (Object bean : applicationContext.getBeansOfType(extension).values()) {
            if (AopUtils.getTargetClass(bean).isAnnotationPresent(ExtensionBase.class)) {
                log.info("Loaded @ExtensionBase: ext:[{}] bean:[{}] type:[{}].", extension.getName(), bean, AopUtils.getTargetClass(bean).getName());
                return bean;
            }
        }

        // todo fix 两边不一致的情况

        // todo 需要这样解决吗?
        Extension externalExtension = externalExtensions.get(extension);
        if (externalExtension != null && externalExtension.base != null) {
            return externalExtension.base;
        }

        // todo msg
        throw new ExtensionException(String.format("Could not found base impl for extension:[%s]", extension.getName()));
    }

    private static Map<String, Group> loadExtensionGroupMap(Class<?> extension, ApplicationContext applicationContext) {

        Map<String, Map<String, List<Wrapper>>> group2code2wrappers = new HashMap<>();
        for (Map.Entry<String, ?> entry : applicationContext.getBeansOfType(extension).entrySet()) {
            String name = entry.getKey();
            Object bean = entry.getValue();
            if (!AopUtils.getTargetClass(bean).isAnnotationPresent(ExtensionImpl.class)) {
                continue;
            }

            ExtensionImpl extensionImpl = AopUtils.getTargetClass(bean).getAnnotation(ExtensionImpl.class);
            for (String group : extensionImpl.group()) {
                for (String code : extensionImpl.code()) {
                    Wrapper wrapper = new Wrapper();
                    wrapper.name = name;
                    wrapper.bean = bean;
                    wrapper.priority = extensionImpl.priority();
                    wrapper.desc = extensionImpl.desc();

                    group2code2wrappers.computeIfAbsent(group, _K -> new HashMap<>()).computeIfAbsent(code, _k -> new LinkedList<>()).add(wrapper);
                }
            }
        }

        Map<String, Group> groupMap = new HashMap<>();
        for (Map.Entry<String, Map<String, List<Wrapper>>> entry : group2code2wrappers.entrySet()) {
            String group = entry.getKey();
            Map<String, List<Wrapper>> code2wrappers = entry.getValue();

            groupMap.put(group, loadExtensionGroup(extension, group, code2wrappers));
        }

        return groupMap;
    }

    private static Group loadExtensionGroup(Class<?> ext, String group, Map<String, List<Wrapper>> code2wrappers) {

        Map<String, List<Impl>> code2impls = new HashMap<>();

        for (String code : code2wrappers.keySet()) {
            List<Wrapper> wrappers = code2wrappers.get(code);
            // 先排序一次, 后序如果同code合并, 还需要再次排序
            wrappers.sort(Comparator.comparingInt(wrapper -> wrapper.priority));

            List<Impl> impls = new ArrayList<>(wrappers.size());
            for (Wrapper wrapper : wrappers) {

                Impl impl = new Impl(group, code, BEAN.name(), wrapper.priority);
                impl.bean = new Bean(wrapper.name, wrapper.bean.getClass().getName());
                impl.instance = wrapper.bean;
                impl.desc = wrapper.desc;

                impls.add(impl);
            }

            code2impls.put(code, impls);
            log.info("Loaded @ExtensionImpl: ext:[{}] group:[{}] code:[{}] -> [{}].", ext.getName(), group, code, impls.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));
        }

        return new Group(group, code2impls);
    }

    private static Set<Class<?>> loadExtensionDefs(Set<Class<?>> externalExtensions, List<String> scanPackages) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(scanPackages.toArray(new String[0])));

        Set<Class<?>> extensions = new HashSet<>();
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
            log.info("Loaded @Extension: ext:[{}] desc:{}.", clazz.getName(), Logger.formatDesc(clazz.getAnnotation(com.alibaba.matrix.extension.annotation.Extension.class).desc()));
            extensions.add(clazz);
        }

        if (extensions.isEmpty()) {
            // todo msg
            log.warn("No extension found by @Extension annotation.");
        }

        if (!CollectionUtils.isEmpty(externalExtensions)) {
            extensions.addAll(externalExtensions);
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
