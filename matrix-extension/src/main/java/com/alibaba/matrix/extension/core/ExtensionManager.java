package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.factory.DubboServiceFactory;
import com.alibaba.matrix.extension.factory.GroovyServiceFactory;
import com.alibaba.matrix.extension.factory.GuiceInstanceFactory;
import com.alibaba.matrix.extension.factory.HsfServiceFactory;
import com.alibaba.matrix.extension.factory.HttpServiceFactory;
import com.alibaba.matrix.extension.factory.ObjectInstanceFactory;
import com.alibaba.matrix.extension.factory.SpELServiceFactory;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.alibaba.matrix.extension.model.config.Extension;
import com.alibaba.matrix.extension.model.config.ExtensionImpl;
import com.alibaba.matrix.extension.model.config.ExtensionScope;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.alibaba.matrix.extension.router.ExtensionRouter;
import com.alibaba.matrix.extension.util.Message;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.alibaba.matrix.extension.model.ExtensionImplType.BASE;
import static java.util.stream.Collectors.toList;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionManager {

    public static Map<Class<?>, Extension> extensionMap;

    public static ExtensionRouter router;

    public static ExtensionPlugin[] plugins;

    public static List<ExtensionImplEntity> getExtensionImpls(String scope, String code, Class<?> ext) {
        Extension extension = extensionMap.get(ext);
        if (extension == null) {
            throw new ExtensionRuntimeException(Message.format("MATRIX-EXTENSION-0000-0004", ext.getName()));
        }

        ExtensionScope extensionScope = extension.scopeMap.get(scope);
        if (extensionScope == null) {
            throw new ExtensionRuntimeException(Message.format("MATRIX-EXTENSION-0000-0005", ext.getName(), scope));
        }

        return extensionScope.CODE_TO_EXT_IMPLS_CACHE.computeIfAbsent(code, _K -> {
            List<ExtensionImpl> impls = extensionScope.code2impls.get(code);
            if (CollectionUtils.isEmpty(impls)) {
                return Collections.singletonList(new ExtensionImplEntity(BASE.name(), extension.base, 0, null));
            } else {
                return impls.stream().map(impl -> makeImplEntity(ext, impl)).collect(toList());
            }
        });
    }

    private static ExtensionImplEntity makeImplEntity(Class<?> ext, ExtensionImpl impl) {
        // tips: 懒加载的具体加载
        if (impl.instance == null) {
            try {
                if (impl.object != null) {
                    impl.instance = ObjectInstanceFactory.getObjectInstance(impl.object);
                } else if (impl.bean != null) {
                    impl.instance = SpringBeanFactory.getSpringBean(impl.bean);
                } else if (impl.guice != null) {
                    impl.instance = GuiceInstanceFactory.getGuiceInstance(impl.guice);
                } else if (impl.hsf != null) {
                    impl.instance = HsfServiceFactory.getHsfService(impl.hsf);
                } else if (impl.dubbo != null) {
                    impl.instance = DubboServiceFactory.getDubboService(ext, impl.dubbo);
                } else if (impl.http != null) {
                    impl.instance = HttpServiceFactory.getHttpService(ext, impl.http);
                } else if (impl.groovy != null) {
                    impl.instance = GroovyServiceFactory.getGroovyService(ext, impl.groovy);
                } else if (impl.spel != null) {
                    impl.instance = SpELServiceFactory.getSpELService(ext, impl.spel);
                }
            } catch (Exception e) {
                return ExceptionUtils.rethrow(e);
            }
        }

        Preconditions.checkState(impl.instance != null);
        if (!ext.isInstance(impl.instance)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0010", impl.instance, ext.getName()));
        }

        return new ExtensionImplEntity(impl.type, impl.instance, impl.priority, impl.desc);
    }
}
