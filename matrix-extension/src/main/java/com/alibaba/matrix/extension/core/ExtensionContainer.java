package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.factory.DubboServiceFactory;
import com.alibaba.matrix.extension.factory.HsfServiceFactory;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.model.Group;
import com.alibaba.matrix.extension.model.Impl;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.google.common.base.Preconditions;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.alibaba.matrix.extension.model.ExtImplType.BASE;
import static java.util.stream.Collectors.toList;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 17:52.
 */
public class ExtensionContainer {

    public static ExtensionPlugin[] plugins;

    public static Map<Class<?>, Extension> extensionMap;

    public static List<ExtImpl> getExtensionImpls(Class<?> ext, String group, String code) {
        Extension _extension = extensionMap.get(ext);
        if (_extension == null) {
            throw new ExtensionRuntimeException(String.format("Extension:[%s] not found.", ext.getName()));
        }

        Group _group = _extension.groupMap.get(group);
        if (_group == null) {
            throw new ExtensionRuntimeException(String.format("Extension:[%s] Group:[%s] not found.", ext.getName(), group));
        }

        return _group.CODE_2_IMPLS_CACHE.computeIfAbsent(code, _K -> {
            List<Impl> impls = _group.code2impls.get(code);
            if (CollectionUtils.isEmpty(impls)) {
                return Collections.singletonList(new ExtImpl(BASE.name(), _extension.base));
            } else {
                return impls.stream().map(impl -> makeImpl(ext, impl)).collect(toList());
            }
        });
    }

    private static ExtImpl makeImpl(Class<?> ext, Impl impl) {
        if (impl.instance != null) {
            return new ExtImpl(impl.type, impl.instance);
        }

        // tips: 懒加载的具体加载
        try {
            if (impl.bean != null) {
                impl.instance = SpringBeanFactory.getSpringBean(impl.bean);
            } else if (impl.hsf != null) {
                impl.instance = HsfServiceFactory.getHsfService(impl.hsf);
            } else if (impl.dubbo != null) {
                impl.instance = DubboServiceFactory.getDubboService(ext, impl.dubbo);
            }
        } catch (Exception e) {
            throw new ExtensionRuntimeException(e);
        }

        Preconditions.checkState(impl.instance != null);
        return new ExtImpl(impl.type, impl.instance);
    }
}
