package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.alibaba.matrix.extension.util.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:48.
 */
public class LoggingExtensionRouter extends BaseExtensionRouter {

    @Override
    public List<ExtensionImplEntity> route(ExtensionExecuteContext context) {
        List<ExtensionImplEntity> impls = super.route(context);
        Logger.log.info("Route ext:[{}] scope:[{}] code:[{}] -> impls:[{}].", context.ext.getName(), context.scope, context.code, impls.stream().map(impl -> String.format("%s(%s/%d)", impl.instance, impl.type, impl.priority)).collect(Collectors.joining(", ")));
        return impls;
    }
}
