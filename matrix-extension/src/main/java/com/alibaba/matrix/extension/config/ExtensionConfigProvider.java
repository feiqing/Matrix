package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
public class ExtensionConfigProvider {

    public static final ExtensionParallelConfig parallel;

    public static final ExtensionExperimentConfig experiment;

    static {
        parallel = MatrixServiceLoader.loadService(ExtensionParallelConfig.class, new DefaultExtensionParallelConfig());
        experiment = MatrixServiceLoader.loadService(ExtensionExperimentConfig.class, new DefaultExtensionExperimentConfig());
    }
}
