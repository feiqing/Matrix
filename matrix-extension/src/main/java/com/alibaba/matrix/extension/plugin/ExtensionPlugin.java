package com.alibaba.matrix.extension.plugin;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/8/12 11:20.
 */
public interface ExtensionPlugin {

    Object invoke(ExtensionInvocation invocation);
}
