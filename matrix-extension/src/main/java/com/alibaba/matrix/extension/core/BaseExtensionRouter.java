package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.ExtensionRouter;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.utils.Logger;
import com.google.common.base.Strings;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:48.
 */
public class BaseExtensionRouter implements ExtensionRouter {

    private static final long serialVersionUID = -4295717349015031260L;

    @Override
    public List<ExtImpl> route(Class<?> ext, String group, Object[] args) {

        String code = ExtensionContext.getExtensionCode(group);
        if (Strings.isNullOrEmpty(code)) {
            throw new ExtensionRuntimeException("Can not find extension code for group: " + group);
        }

        List<ExtImpl> impls = ExtensionContainer.getExtensionImpls(ext, group, code);
        if (CollectionUtils.isEmpty(impls)) {
            throw new ExtensionRuntimeException("Can not find any extension impl for group: " + group + ", code: " + code);
        }

        Logger.log.info("Route ext:[{}] group:[{}] code:[{}] -> impls:[{}].", ext.getName(), group, code, impls.stream().map(impl -> String.format("%s(%s)", impl.instance, impl.type)).collect(Collectors.joining(", ")));

        return impls;
    }
}
