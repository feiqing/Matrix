package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.factory.DubboServiceFactory;
import com.alibaba.matrix.extension.factory.GroovyServiceFactory;
import com.alibaba.matrix.extension.factory.HsfServiceFactory;
import com.alibaba.matrix.extension.factory.HttpServiceFactory;
import com.alibaba.matrix.extension.factory.ObjectInstanceFactory;
import com.alibaba.matrix.extension.factory.SpELServiceFactory;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.model.Scope;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.alibaba.matrix.extension.router.ExtensionRouter;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.alibaba.matrix.extension.model.ExtImpl.Type.BASE;
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

    public static List<ExtImpl> getExtensionImpls(String scope, String code, Class<?> ext) {
        Extension _extension = extensionMap.get(ext);
        if (_extension == null) {
            throw new ExtensionRuntimeException(String.format("Extension:[%s] not found.", ext.getName()));
        }

        Scope _scope = _extension.scopeMap.get(scope);
        if (_scope == null) {
            throw new ExtensionRuntimeException(String.format("Extension:[%s] scope:[%s] not found.", ext.getName(), scope));
        }

        return _scope.CODE_2_IMPLS_CACHE.computeIfAbsent(code, _K -> {
            List<Impl> impls = _scope.code2impls.get(code);
            if (CollectionUtils.isEmpty(impls)) {
                return Collections.singletonList(new ExtImpl(BASE.name(), _extension.base));
            } else {
                return impls.stream().map(impl -> makeImpl(ext, impl)).collect(toList());
            }
        });
    }

    private static ExtImpl makeImpl(Class<?> ext, Impl impl) {
        if (impl.instance != null) {
            return new ExtImpl(impl.type, impl.instance, impl.desc);
        }

        // tips: 懒加载的具体加载
        try {
            if (impl.object != null) {
                impl.instance = ObjectInstanceFactory.newInstance(impl.object);
            } else if (impl.bean != null) {
                impl.instance = SpringBeanFactory.getSpringBean(impl.bean);
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

        Preconditions.checkState(impl.instance != null);
        if (!ext.isInstance(impl.instance)) {
            throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0010", impl.instance, ext.getName()).getMessage());
        }
        return new ExtImpl(impl.type, impl.instance, impl.desc);
    }
}
