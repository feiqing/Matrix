package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.core.ExtensionExecuteContext;
import com.alibaba.matrix.extension.core.ExtensionImplEntity;

import java.util.List;

/**
 * This interface defines the contract for routing extension implementations based on the provided execution context.
 * Implementations of this interface are responsible for determining which extension implementations should be executed
 * given a specific context.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/11/13 10:42.
 */
public interface ExtensionRouter {

    /**
     * Routes and returns the list of extension implementations that should be executed based on the given context.
     *
     * @param context the execution context containing necessary information for routing
     * @return a list of {@link ExtensionImplEntity} representing the extension implementations to be executed
     */
    List<ExtensionImplEntity> route(ExtensionExecuteContext context);
}
