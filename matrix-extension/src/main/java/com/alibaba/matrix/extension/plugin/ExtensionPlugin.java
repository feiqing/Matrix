
package com.alibaba.matrix.extension.plugin;

/**
 * Represents an extension plugin that can be invoked with an {@link ExtensionInvocation}.
 * Implementations of this interface define custom behavior that can be executed during the invocation process.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/8/12 11:20
 */
public interface ExtensionPlugin {

    /**
     * Invokes the extension plugin with the given invocation context.
     *
     * @param invocation the invocation context containing necessary information for the plugin execution
     * @return the result of the plugin execution
     */
    Object invoke(ExtensionInvocation invocation);
}