package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.extension.model.ExtExecCtx;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class DefaultExtensionExperimentConfig implements ExtensionExperimentConfig {

    @Override
    public boolean enableJobExecutor(ExtExecCtx ctx) {
        return true;
    }
}
