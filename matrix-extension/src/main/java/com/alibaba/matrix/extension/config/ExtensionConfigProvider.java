package com.alibaba.matrix.extension.config;

import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/27 22:07.
 */
public class ExtensionConfigProvider {

    public static final ExtensionParallelConfig parallel;

    public static final ExtensionExperimentConfig experiment;

    static {
        parallel = MatrixServiceLoader.loadService(ExtensionParallelConfig.class, new DefaultExtensionParallelConfig());
        experiment = MatrixServiceLoader.loadService(ExtensionExperimentConfig.class, new DefaultExtensionExperimentConfig());
    }
}
