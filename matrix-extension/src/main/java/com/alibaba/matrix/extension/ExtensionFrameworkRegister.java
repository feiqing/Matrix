package com.alibaba.matrix.extension;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.extension.core.ExtensionManager;
import com.alibaba.matrix.extension.model.config.Extension;
import com.alibaba.matrix.extension.model.config.ExtensionImpl;
import com.alibaba.matrix.extension.model.config.ExtensionScope;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.alibaba.matrix.extension.router.BaseExtensionRouter;
import com.alibaba.matrix.extension.router.ExtensionRouter;
import com.alibaba.matrix.extension.util.AnnotationLoader;
import com.alibaba.matrix.extension.util.Logger;
import com.alibaba.matrix.extension.util.XmlLoader;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;
import static com.alibaba.matrix.extension.util.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Data
public class ExtensionFrameworkRegister {

    private static final AtomicBoolean started = new AtomicBoolean(false);

    private boolean enableAnnotationScan = false;

    private List<String> scanPackages = Collections.emptyList();

    private boolean enableXmlConfig = false;

    private List<String> configLocations = Collections.emptyList();

    private ExtensionRouter extensionRouter = new BaseExtensionRouter();

    private List<ExtensionPlugin> extensionPlugins = Collections.emptyList();

    public void init() {
        if (!started.compareAndSet(false, true)) {
            log.warn("[Matrix-Extension] is Started !");
            return;
        }

        log.info("{}", Message.of("MATRIX-EXTENSION-0000-0000", resolveProjectVersion()).getMessage());
        ExtensionManager.router = extensionRouter;
        ExtensionManager.plugins = loadExtensionPlugins();
        ExtensionManager.extensionMap = loadExtensionDefinitions();
        log.info("Extension load summary: {}", toExtensionSummary(ExtensionManager.extensionMap));
        log.info("{}", Message.of("MATRIX-EXTENSION-0000-0001").getMessage());

        if (!enableAnnotationScan && !enableXmlConfig) {
            log.warn("enableAnnotationScan and enableXmlConfig all switch off!");
        }
    }

    private ExtensionPlugin[] loadExtensionPlugins() {
        ExtensionPlugin[] plugins = new ExtensionPlugin[extensionPlugins.size()];
        for (int i = 0; i < extensionPlugins.size(); ++i) {
            plugins[i] = extensionPlugins.get(i);
            log.info("Loaded ExtensionPlugin: [{}]", plugins[i]);
        }
        return plugins;
    }

    private Map<Class<?>, Extension> loadExtensionDefinitions() {
        Map<Class<?>, Extension> xmlExtensionMap = Collections.emptyMap();
        if (enableXmlConfig) {
            xmlExtensionMap = XmlLoader.loadXml(configLocations);
        }

        Map<Class<?>, Extension> annotationExtensionMap = Collections.emptyMap();
        if (enableAnnotationScan) {
            annotationExtensionMap = AnnotationLoader.loadAnnotation(scanPackages);
        }

        return mergeExtensionMap(annotationExtensionMap, xmlExtensionMap);
    }


    private Map<Class<?>, Extension> mergeExtensionMap(Map<Class<?>, Extension> extensionMap1, Map<Class<?>, Extension> extensionMap2) {

        Map<Class<?>, Extension> resultExtensionMap = new LinkedHashMap<>();

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

        Map<String, ExtensionScope> scopeMap1 = extension1.scopeMap == null ? Collections.emptyMap() : extension1.scopeMap;
        Map<String, ExtensionScope> scopeMap2 = extension2.scopeMap == null ? Collections.emptyMap() : extension2.scopeMap;

        MapDifference<String, ExtensionScope> difference = Maps.difference(scopeMap1, scopeMap2);
        Map<String, ExtensionScope> resultScopeMap = new LinkedHashMap<>();
        resultScopeMap.putAll(difference.entriesOnlyOnLeft());
        resultScopeMap.putAll(difference.entriesOnlyOnRight());
        for (Map.Entry<String, MapDifference.ValueDifference<ExtensionScope>> entry : difference.entriesDiffering().entrySet()) {
            resultScopeMap.put(entry.getKey(), resoleScopeDifference(extension1.clazz, entry.getValue()));
        }

        return new Extension(extension1.clazz, extension1.desc, extension1.base, resultScopeMap);
    }

    private ExtensionScope resoleScopeDifference(Class<?> ext, MapDifference.ValueDifference<ExtensionScope> scopeDifference) {
        ExtensionScope scope1 = scopeDifference.leftValue();
        ExtensionScope scope2 = scopeDifference.rightValue();

        Preconditions.checkState(scope1 != null && scope2 != null);
        Preconditions.checkState(scope1.scope != null && scope2.scope != null && StringUtils.equals(scope1.scope, scope2.scope));

        Map<String, List<ExtensionImpl>> code2impls1 = scope1.code2impls == null ? Collections.emptyMap() : scope1.code2impls;
        Map<String, List<ExtensionImpl>> code2impls2 = scope2.code2impls == null ? Collections.emptyMap() : scope2.code2impls;

        MapDifference<String, List<ExtensionImpl>> difference = Maps.difference(code2impls1, code2impls2);

        Map<String, List<ExtensionImpl>> resultCode2impls = new LinkedHashMap<>();
        resultCode2impls.putAll(difference.entriesOnlyOnLeft());
        resultCode2impls.putAll(difference.entriesOnlyOnRight());

        for (Map.Entry<String, MapDifference.ValueDifference<List<ExtensionImpl>>> entry : difference.entriesDiffering().entrySet()) {
            resultCode2impls.put(entry.getKey(), resoleImplsDifference(ext, scope1.scope, entry.getKey(), entry.getValue()));
        }

        return new ExtensionScope(scope1.scope, resultCode2impls);
    }

    private List<ExtensionImpl> resoleImplsDifference(Class<?> ext, String scope, String code, MapDifference.ValueDifference<List<ExtensionImpl>> difference) {
        List<ExtensionImpl> impls1 = difference.leftValue() == null ? Collections.emptyList() : difference.leftValue();
        List<ExtensionImpl> impls2 = difference.rightValue() == null ? Collections.emptyList() : difference.rightValue();

        Preconditions.checkState(impls1.stream().allMatch(impl -> StringUtils.equals(scope, impl.scope) && StringUtils.equals(code, impl.code)));
        Preconditions.checkState(impls2.stream().allMatch(impl -> StringUtils.equals(scope, impl.scope) && StringUtils.equals(code, impl.code)));

        List<ExtensionImpl> implsResult = new ArrayList<>(impls1.size() + impls2.size());
        implsResult.addAll(impls1);
        implsResult.addAll(impls2);

        implsResult.sort(Comparator.comparingInt(o -> o.priority));
        log.info("[MERGE] ExtensionImpl: ext:[{}] scope:[{}] code:[{}] -> [{}].", ext.getName(), scope, code, implsResult.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));

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
        return jsonMapper.toJson(summary);
    }

    private Map<String, Object> toScopeSummary(Object base, Map<String, ExtensionScope> scopeMap) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("_BASE_IMPL_", Logger.formatBase(base));
        for (ExtensionScope scope : scopeMap.values()) {
            summary.put(scope.scope, toCodeSummary(scope.code2impls));
        }
        return summary;
    }

    private Map<String, Object> toCodeSummary(Map<String, List<ExtensionImpl>> code2impls) {
        Map<String, Object> summary = new LinkedHashMap<>();
        code2impls.forEach((code, impls) -> {
            List<String> implsDesc = impls.stream().map(Logger::formatImpl).collect(Collectors.toList());
            summary.put(code, implsDesc);
        });
        return summary;
    }
}
