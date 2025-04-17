package com.alibaba.matrix.extension;

import com.alibaba.matrix.extension.core.ExtensionManager;
import com.alibaba.matrix.extension.core.config.Extension;
import com.alibaba.matrix.extension.core.config.ExtensionImpl;
import com.alibaba.matrix.extension.core.config.ExtensionNamespace;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.alibaba.matrix.extension.router.BaseExtensionRouter;
import com.alibaba.matrix.extension.router.ExtensionRouter;
import com.alibaba.matrix.extension.util.AnnotationLoader;
import com.alibaba.matrix.extension.util.Logger;
import com.alibaba.matrix.extension.util.Message;
import com.alibaba.matrix.extension.util.XmlLoader;
import com.google.common.base.Preconditions;
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
import java.util.stream.Collectors;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;
import static com.alibaba.matrix.base.util.MatrixUtils.resolveProjectVersion;
import static com.alibaba.matrix.extension.util.Logger.log;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 2.0
 * @since 2021/09/30 10:31.
 */
@Data
@SuppressWarnings("all")
public class ExtensionFrameworkRegister {

    private static final AtomicBoolean started = new AtomicBoolean(false);

    private boolean enableAnnotationScan = false;

    private List<String> scanPackages = Collections.emptyList();

    private boolean enableXmlConfig = false;

    private List<String> configLocations = Collections.emptyList();

    private ExtensionRouter extensionRouter = new BaseExtensionRouter();

    private List<ExtensionPlugin> extensionPlugins = Collections.emptyList();

    public void start() {
        init();
    }

    public void init() {
        if (!started.compareAndSet(false, true)) {
            log.warn("[Matrix-Extension] is Started !");
            return;
        }

        log.info("{}", Message.format("MATRIX-EXTENSION-0000-0000", resolveProjectVersion(ExtensionFrameworkRegister.class, "matrix-extension")));
        ExtensionManager.router = extensionRouter;
        ExtensionManager.plugins = loadExtensionPlugins();
        ExtensionManager.extensionMap = loadExtensionDefinitions();
        log.info("Extension load summary: {}", toExtensionSummary(ExtensionManager.extensionMap));
        log.info("{}", Message.format("MATRIX-EXTENSION-0000-0001"));

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

        Map<String, ExtensionNamespace> namespaceMap1 = extension1.namespaceMap == null ? Collections.emptyMap() : extension1.namespaceMap;
        Map<String, ExtensionNamespace> namespaceMap2 = extension2.namespaceMap == null ? Collections.emptyMap() : extension2.namespaceMap;

        MapDifference<String, ExtensionNamespace> difference = Maps.difference(namespaceMap1, namespaceMap2);
        Map<String, ExtensionNamespace> resultNamespaceMap = new LinkedHashMap<>();
        resultNamespaceMap.putAll(difference.entriesOnlyOnLeft());
        resultNamespaceMap.putAll(difference.entriesOnlyOnRight());
        for (Map.Entry<String, MapDifference.ValueDifference<ExtensionNamespace>> entry : difference.entriesDiffering().entrySet()) {
            resultNamespaceMap.put(entry.getKey(), resoleNamespaceDifference(extension1.clazz, entry.getValue()));
        }

        return new Extension(extension1.clazz, extension1.desc, extension1.base, resultNamespaceMap);
    }

    private ExtensionNamespace resoleNamespaceDifference(Class<?> ext, MapDifference.ValueDifference<ExtensionNamespace> namespaceDifference) {
        ExtensionNamespace namespace1 = namespaceDifference.leftValue();
        ExtensionNamespace namespace2 = namespaceDifference.rightValue();

        Preconditions.checkState(namespace1 != null && namespace2 != null);
        Preconditions.checkState(namespace1.namespace != null && namespace2.namespace != null && StringUtils.equals(namespace1.namespace, namespace2.namespace));

        Map<String, List<ExtensionImpl>> code2impls1 = namespace1.code2impls == null ? Collections.emptyMap() : namespace1.code2impls;
        Map<String, List<ExtensionImpl>> code2impls2 = namespace2.code2impls == null ? Collections.emptyMap() : namespace2.code2impls;

        MapDifference<String, List<ExtensionImpl>> difference = Maps.difference(code2impls1, code2impls2);

        Map<String, List<ExtensionImpl>> resultCode2impls = new LinkedHashMap<>();
        resultCode2impls.putAll(difference.entriesOnlyOnLeft());
        resultCode2impls.putAll(difference.entriesOnlyOnRight());

        for (Map.Entry<String, MapDifference.ValueDifference<List<ExtensionImpl>>> entry : difference.entriesDiffering().entrySet()) {
            resultCode2impls.put(entry.getKey(), resoleImplsDifference(ext, namespace1.namespace, entry.getKey(), entry.getValue()));
        }

        return new ExtensionNamespace(namespace1.namespace, resultCode2impls);
    }

    private List<ExtensionImpl> resoleImplsDifference(Class<?> ext, String namespace, String code, MapDifference.ValueDifference<List<ExtensionImpl>> difference) {
        List<ExtensionImpl> impls1 = difference.leftValue() == null ? Collections.emptyList() : difference.leftValue();
        List<ExtensionImpl> impls2 = difference.rightValue() == null ? Collections.emptyList() : difference.rightValue();

        Preconditions.checkState(impls1.stream().allMatch(impl -> StringUtils.equals(namespace, impl.namespace) && StringUtils.equals(code, impl.code)));
        Preconditions.checkState(impls2.stream().allMatch(impl -> StringUtils.equals(namespace, impl.namespace) && StringUtils.equals(code, impl.code)));

        List<ExtensionImpl> implsResult = new ArrayList<>(impls1.size() + impls2.size());
        implsResult.addAll(impls1);
        implsResult.addAll(impls2);

        implsResult.sort(Comparator.comparingInt(o -> o.priority));
        log.info("[MERGE] ExtensionImpl: ext:[{}] namespace:[{}] code:[{}] -> [{}].", ext.getName(), namespace, code, implsResult.stream().map(Logger::formatImpl).collect(Collectors.joining(", ")));

        return implsResult;
    }

    private String toExtensionSummary(Map<Class<?>, Extension> extensionMap) {
        Map<String, Object> summary = new LinkedHashMap<>();
        for (Extension extension : extensionMap.values()) {
            summary.put(Logger.formatExt(extension.clazz, extension.desc), toNamespaceSummary(extension.base, extension.namespaceMap));
        }
        return jsonMapper.toJson(summary);
    }

    private Map<String, Object> toNamespaceSummary(Object base, Map<String, ExtensionNamespace> namespaceMap) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("_BASE_IMPL_", Logger.formatBase(base));
        for (ExtensionNamespace namespace : namespaceMap.values()) {
            summary.put(namespace.namespace, toCodeSummary(namespace.code2impls));
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
