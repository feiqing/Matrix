package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.core.ExtensionManager;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtExecCtx;
import com.alibaba.matrix.extension.model.ExtImpl;
import com.alibaba.matrix.extension.util.Logger;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:48.
 */
public class BaseExtensionRouter implements ExtensionRouter {

    @Override
    public List<ExtImpl> route(ExtExecCtx ctx) {
        String scope = ctx.scope;
        String code = ctx.code;
        Class<?> ext = ctx.ext;
        Preconditions.checkArgument(!Strings.isNullOrEmpty(scope));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(code));

        List<ExtImpl> impls = ExtensionManager.getExtensionImpls(scope, code, ext);
        if (CollectionUtils.isEmpty(impls)) {
            throw new ExtensionRuntimeException("Can not find [" + ext.getName() + "] extension impl for scope: " + scope + ", code: " + code);
        }

        Logger.log.info("Route ext:[{}] scope:[{}] code:[{}] -> impls:[{}].", ext.getName(), scope, code, impls.stream().map(impl -> String.format("%s(%s)", impl.instance, impl.type)).collect(Collectors.joining(", ")));

        return impls;
    }
}
