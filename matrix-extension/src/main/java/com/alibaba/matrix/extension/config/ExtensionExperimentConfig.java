package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.extension.model.ExtExecCtx;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/30 09:32.
 */
public interface ExtensionExperimentConfig {

    boolean enableJobExecutor(ExtExecCtx ctx);

}
