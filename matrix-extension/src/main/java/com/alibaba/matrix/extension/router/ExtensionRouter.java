package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;

import java.util.List;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/11/13 10:42.
 */
public interface ExtensionRouter {

    /**
     * Get The Extension Implementations
     *
     * @param context
     * @return
     */
    List<ExtensionImplEntity> route(ExtensionExecuteContext context);
}
