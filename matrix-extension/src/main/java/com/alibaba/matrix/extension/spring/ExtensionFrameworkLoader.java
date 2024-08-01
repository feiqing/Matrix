package com.alibaba.matrix.extension.spring;

import com.alibaba.fastjson.JSON;
import com.alibaba.matrix.extension.core.ExtensionContainer;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.model.Scope;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.alibaba.matrix.extension.utils.AnnotationLoader;
import com.alibaba.matrix.extension.utils.Logger;
import com.alibaba.matrix.extension.utils.XmlLoader;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;
import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/22 21:49.
 */
@Data
// @SuppressWarnings("all")
public class ExtensionFrameworkLoader implements ApplicationListener<ContextRefreshedEvent> {

    private static final AtomicBoolean started = new AtomicBoolean(false);

    private boolean enableAnnotationScan = false;

    private List<String> scanPackages = Collections.emptyList();

    private boolean enableXmlConfig = false;

    private List<String> configLocations = Collections.emptyList();

    private List<ExtensionPlugin> extensionPlugins = Collections.emptyList();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!started.compareAndSet(false, true)) {
            log.warn("[Matrix-Extension] is Started !");
            return;
        }

        log.info("[Matrix-Extension] Version: [{}] Starting...", resolveProjectVersion());
        SpringBeanFactory.setApplicationContext(event.getApplicationContext());
        ExtensionContainer.plugins = loadExtensionPlugins();
        ExtensionContainer.extensionMap = loadExtensionDefinitions(event.getApplicationContext());
        log.info("Extension loaded summary: {}", toExtensionSummary(ExtensionContainer.extensionMap));
        log.info("[Matrix-Extension] Started Success!");

        if (!enableAnnotationScan && !enableXmlConfig) {
            log.warn("enableAnnotationScan and enableXmlConfig all switch off!");
        }
    }

    private ExtensionPlugin[] loadExtensionPlugins() {
        try {
            ExtensionPlugin[] plugins = new ExtensionPlugin[extensionPlugins.size()];
            for (int i = 0; i < extensionPlugins.size(); ++i) {
                plugins[i] = extensionPlugins.get(i);
                log.info("Loaded ExtensionPlugin: [{}]", plugins[i]);
            }
            return plugins;
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
            annotationExtensionMap = AnnotationLoader.loadAnnotation(scanPackages, applicationContext);
        }

        return mergeExtensionMap(annotationExtensionMap, xmlExtensionMap);
    }


    private Map<Class<?>, Extension> mergeExtensionMap(Map<Class<?>, Extension> extensionMap1, Map<Class<?>, Extension> extensionMap2) {

        Map<Class<?>, Extension> resultExtensionMap = new HashMap<>();

        MapDifference<Class<?>, Extension> difference = Maps.difference(extensionMap1, extensionMap2);
        resultExtensionMap.putAll(difference.entriesOnlyOnLeft());
        resultExtensionMap.putAll(difference.entriesOnlyOnRight());
        for (Map.Entry<Class<?>, MapDifference.ValueDifference<Extension>> entry : difference.entriesDiffering().entrySet()) {
            resultExtensionMap.put(entry.getKey(), resolveExtensionDifference(entry.getValue()));
        }

        return ImmutableMap.copyOf(resultExtensionMap);
    }

    private Extension resolveExtensionDifference(MapDifference.ValueDifference<Extension> extensionDifference) {
        Extension extension1 = extensionDifference.leftValue();
        Extension extension2 = extensionDifference.rightValue();

        Preconditions.checkState(extension1 != null && extension2 != null);
        Preconditions.checkState(extension1.clazz != null && extension2.clazz != null && extension1.clazz == extension2.clazz);
        Preconditions.checkState(extension1.base != null && extension2.base != null && extension1.base == extension2.base);

        Map<String, Scope> scopeMap1 = extension1.scopeMap == null ? Collections.emptyMap() : extension1.scopeMap;
        Map<String, Scope> scopeMap2 = extension2.scopeMap == null ? Collections.emptyMap() : extension2.scopeMap;

        MapDifference<String, Scope> difference = Maps.difference(scopeMap1, scopeMap2);
        Map<String, Scope> resultScopeMap = new HashMap<>();
        resultScopeMap.putAll(difference.entriesOnlyOnLeft());
        resultScopeMap.putAll(difference.entriesOnlyOnRight());
        for (Map.Entry<String, MapDifference.ValueDifference<Scope>> entry : difference.entriesDiffering().entrySet()) {
            resultScopeMap.put(entry.getKey(), resoleScopeDifference(extension1.clazz, entry.getValue()));
        }

        return new Extension(extension1.clazz, extension1.desc, extension1.base, resultScopeMap);
    }

    private Scope resoleScopeDifference(Class<?> ext, MapDifference.ValueDifference<Scope> scopeDifference) {
        Scope scope1 = scopeDifference.leftValue();
        Scope scope2 = scopeDifference.rightValue();

        Preconditions.checkState(scope1 != null && scope2 != null);
        Preconditions.checkState(scope1.scope != null && scope2.scope != null && StringUtils.equals(scope1.scope, scope2.scope));

        Map<String, List<Impl>> code2impls1 = scope1.code2impls == null ? Collections.emptyMap() : scope1.code2impls;
        Map<String, List<Impl>> code2impls2 = scope2.code2impls == null ? Collections.emptyMap() : scope2.code2impls;

        MapDifference<String, List<Impl>> difference = Maps.difference(code2impls1, code2impls2);

        Map<String, List<Impl>> resultCode2impls = new HashMap<>();
        resultCode2impls.putAll(difference.entriesOnlyOnLeft());
        resultCode2impls.putAll(difference.entriesOnlyOnRight());

        for (Map.Entry<String, MapDifference.ValueDifference<List<Impl>>> entry : difference.entriesDiffering().entrySet()) {
            resultCode2impls.put(entry.getKey(), resoleImplsDifference(ext, scope1.scope, entry.getKey(), entry.getValue()));
        }

        return new Scope(scope1.scope, resultCode2impls);
    }

    private List<Impl> resoleImplsDifference(Class<?> ext, String scope, String code, MapDifference.ValueDifference<List<Impl>> difference) {
        List<Impl> impls1 = difference.leftValue() == null ? Collections.emptyList() : difference.leftValue();
        List<Impl> impls2 = difference.rightValue() == null ? Collections.emptyList() : difference.rightValue();

        Preconditions.checkState(impls1.stream().allMatch(impl -> StringUtils.equals(scope, impl.scope) && StringUtils.equals(code, impl.code)));
        Preconditions.checkState(impls2.stream().allMatch(impl -> StringUtils.equals(scope, impl.scope) && StringUtils.equals(code, impl.code)));

        List<Impl> implsResult = new ArrayList<>(impls1.size() + impls2.size());
        implsResult.addAll(impls1);
        implsResult.addAll(impls2);

        implsResult.sort(Comparator.comparingInt(o -> o.priority));
        log.info("! Merge ExtensionImpl: ext:[{}] scope:[{}] code:[{}] -> [{}].", ext.getName(), scope, code, implsResult.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));

        return implsResult;
    }

    private String resolveProjectVersion() {
        String version;
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        log.info("[Matrix-Extension] Project Path: [{}]", path);
        try (JarFile jar = new JarFile(path)) {
            if (!Strings.isNullOrEmpty(version = jar.getManifest().getMainAttributes().getValue("Implementation-Version"))) {
                return version;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (!Strings.isNullOrEmpty(version = getClass().getPackage().getImplementationVersion())) {
                return version;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (!Strings.isNullOrEmpty(version = StringUtils.substringAfter(StringUtils.substringBeforeLast(path, ".jar"), "matrix-extension-"))) {
                return version;
            }
        } catch (Throwable ignored) {
        }

        return "unresolved";
    }

    private String toExtensionSummary(Map<Class<?>, Extension> extensionMap) {
        Map<String, Object> summary = new LinkedHashMap<>();
        for (Extension extension : extensionMap.values()) {
            summary.put(Logger.formatExt(extension.clazz, extension.desc), toScopeSummary(extension.base, extension.scopeMap));
        }
        return JSON.toJSONString(summary, DisableCircularReferenceDetect);
    }

    private Map<String, Object> toScopeSummary(Object base, Map<String, Scope> scopeMap) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("_BASE_IMPL_", Logger.formatBase(base));
        for (Scope scope : scopeMap.values()) {
            summary.put(scope.scope, toCodeSummary(scope.code2impls));
        }
        return summary;
    }

    private Map<String, Object> toCodeSummary(Map<String, List<Impl>> code2impls) {
        Map<String, Object> summary = new LinkedHashMap<>();
        code2impls.forEach((code, impls) -> {
            List<String> implsDesc = impls.stream().map(Logger::formatImpl).collect(Collectors.toList());
            summary.put(code, implsDesc);
        });
        return summary;
    }

}