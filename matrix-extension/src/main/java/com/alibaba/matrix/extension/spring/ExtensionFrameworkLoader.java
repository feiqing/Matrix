package com.alibaba.matrix.extension.spring;

import com.alibaba.matrix.extension.core.ExtensionContainer;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Group;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.alibaba.matrix.extension.utils.AnnotationLoader;
import com.alibaba.matrix.extension.utils.Logger;
import com.alibaba.matrix.extension.utils.XmlLoader;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/22 21:49.
 */
@Data
// @SuppressWarnings("all")
public class ExtensionFrameworkLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean enableAnnotationScan = false;

    private List<String> scanPackages = Collections.emptyList();

    private boolean enableXmlConfig = false;

    private List<String> configLocations = Collections.emptyList();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SpringBeanFactory.setApplicationContext(event.getApplicationContext());

        log.info("[Matrix-Extension] Starting...");
        ExtensionContainer.plugins = loadExtensionPlugins();
        ExtensionContainer.extensionMap = loadExtensionDefinitions(event.getApplicationContext());
        log.info("[Matrix-Extension] Started Success!");

        if (!enableAnnotationScan && !enableXmlConfig) {
            log.warn("enableAnnotationScan and enableXmlConfig all switch off!");
        }
    }

    private ExtensionPlugin[] loadExtensionPlugins() {
        try {
            List<ExtensionPlugin> plugins = new LinkedList<>();
            for (ExtensionPlugin plugin : ServiceLoader.load(ExtensionPlugin.class, ExtensionPlugin.class.getClassLoader())) {
                log.info("Loaded ExtensionPlugin: [{}]", plugin);
                plugins.add(plugin);
            }
            return plugins.toArray(new ExtensionPlugin[0]);
        } catch (Throwable t) {
            log.error("Loading ExtensionPlugin error.", t);
            throw new ExtensionException(t);
        }
    }

    private Map<Class<?>, Extension> loadExtensionDefinitions(ApplicationContext applicationContext) {
        Map<Class<?>, Extension> xmlExtensionMap = Collections.emptyMap();
        if (enableXmlConfig) {
            xmlExtensionMap = XmlLoader.loadXml(configLocations, applicationContext);
        }

        Map<Class<?>, Extension> annotationExtensionMap = Collections.emptyMap();
        if (enableAnnotationScan) {
            annotationExtensionMap = AnnotationLoader.loadAnnotation(xmlExtensionMap, scanPackages, applicationContext);
        }

        return mergeExtensionMap(annotationExtensionMap, xmlExtensionMap);
    }


    private Map<Class<?>, Extension> mergeExtensionMap(Map<Class<?>, Extension> extensionMap1, Map<Class<?>, Extension> extensionMap2) {

        Map<Class<?>, Extension> extensionMapResult = new HashMap<>();

        MapDifference<Class<?>, Extension> difference = Maps.difference(extensionMap1, extensionMap2);
        extensionMapResult.putAll(difference.entriesOnlyOnLeft());
        extensionMapResult.putAll(difference.entriesOnlyOnRight());
        for (Map.Entry<Class<?>, MapDifference.ValueDifference<Extension>> entry : difference.entriesDiffering().entrySet()) {
            extensionMapResult.put(entry.getKey(), resolveExtensionDifference(entry.getValue()));
        }

        return ImmutableMap.copyOf(extensionMapResult);
    }

    private Extension resolveExtensionDifference(MapDifference.ValueDifference<Extension> extensionDifference) {
        Extension extension1 = extensionDifference.leftValue();
        Extension extension2 = extensionDifference.rightValue();

        Preconditions.checkState(extension1 != null && extension2 != null);
        Preconditions.checkState(extension1.clazz != null && extension2.clazz != null && extension1.clazz == extension2.clazz);
        Preconditions.checkState(extension1.base != null && extension2.base != null && extension1.base == extension2.base);

        Map<String, Group> groupMap1 = extension1.groupMap == null ? Collections.emptyMap() : extension1.groupMap;
        Map<String, Group> groupMap2 = extension2.groupMap == null ? Collections.emptyMap() : extension2.groupMap;

        MapDifference<String, Group> difference = Maps.difference(groupMap1, groupMap2);
        Map<String, Group> groupMapResult = new HashMap<>();
        groupMapResult.putAll(difference.entriesOnlyOnLeft());
        groupMapResult.putAll(difference.entriesOnlyOnRight());
        for (Map.Entry<String, MapDifference.ValueDifference<Group>> entry : difference.entriesDiffering().entrySet()) {
            groupMapResult.put(entry.getKey(), resoleGroupDifference(extension1.clazz, entry.getValue()));
        }

        return new Extension(extension1.clazz, extension1.base, groupMapResult);
    }

    private Group resoleGroupDifference(Class<?> ext, MapDifference.ValueDifference<Group> groupDifference) {
        Group group1 = groupDifference.leftValue();
        Group group2 = groupDifference.rightValue();

        Preconditions.checkState(group1 != null && group2 != null);
        Preconditions.checkState(group1.group != null && group2.group != null && StringUtils.equals(group1.group, group2.group));

        Map<String, List<Impl>> code2impls1 = group1.code2impls == null ? Collections.emptyMap() : group1.code2impls;
        Map<String, List<Impl>> code2impls2 = group2.code2impls == null ? Collections.emptyMap() : group2.code2impls;

        MapDifference<String, List<Impl>> difference = Maps.difference(code2impls1, code2impls2);

        Map<String, List<Impl>> code2implsResult = new HashMap<>();
        code2implsResult.putAll(difference.entriesOnlyOnLeft());
        code2implsResult.putAll(difference.entriesOnlyOnRight());

        for (Map.Entry<String, MapDifference.ValueDifference<List<Impl>>> entry : difference.entriesDiffering().entrySet()) {
            code2implsResult.put(entry.getKey(), resoleImplsDifference(ext, group1.group, entry.getKey(), entry.getValue()));
        }

        return new Group(group1.group, code2implsResult);
    }

    private List<Impl> resoleImplsDifference(Class<?> ext, String group, String code, MapDifference.ValueDifference<List<Impl>> difference) {
        List<Impl> impls1 = difference.leftValue() == null ? Collections.emptyList() : difference.leftValue();
        List<Impl> impls2 = difference.rightValue() == null ? Collections.emptyList() : difference.rightValue();

        Preconditions.checkState(impls1.stream().allMatch(impl -> StringUtils.equals(group, impl.group) && StringUtils.equals(code, impl.code)));
        Preconditions.checkState(impls2.stream().allMatch(impl -> StringUtils.equals(group, impl.group) && StringUtils.equals(code, impl.code)));

        List<Impl> implsResult = new ArrayList<>(impls1.size() + impls2.size());
        implsResult.addAll(impls1);
        implsResult.addAll(impls2);

        implsResult.sort(Comparator.comparingInt(o -> o.priority));
        log.info("Merge ExtensionImpl: ext:[{}] group:[{}] code:[{}] -> [{}].", ext.getName(), group, code, implsResult.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));

        return implsResult;
    }
}