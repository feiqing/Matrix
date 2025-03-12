package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.extension.model.ExtensionExecuteContext;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public interface ExtensionExperimentConfig {

    boolean enableJobExecutor(ExtensionExecuteContext ctx);

}
