package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.core.ExtensionExecuteContext;
import com.alibaba.matrix.extension.core.ExtensionImplEntity;
import com.alibaba.matrix.extension.util.Logger;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/11/13 10:48.
 */
public class BaseLoggingExtensionRouter extends BaseExtensionRouter {

    @Override
    public List<ExtensionImplEntity> route(ExtensionExecuteContext ctx) {
        List<ExtensionImplEntity> impls = super.route(ctx);
        Logger.log.info("Route ext:[{}] namespace:[{}] codes:[{}] -> impls:[{}].", ctx.extension.getName(), ctx.namespace, StringUtils.join(ctx.codes, ','), impls.stream().map(impl -> String.format("%s(%s/%d)", impl.instance, impl.type, impl.priority)).collect(Collectors.joining(", ")));
        return impls;
    }
}
