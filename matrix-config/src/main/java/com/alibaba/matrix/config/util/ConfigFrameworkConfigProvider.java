package com.alibaba.matrix.config.util;

import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2025/3/26 13:57.
 */
public class ConfigFrameworkConfigProvider {

    public static final ConfigFrameworkParallelConfig parallel;

    static {
        parallel = MatrixServiceLoader.loadService(ConfigFrameworkParallelConfig.class, new DefaultConfigFrameworkParallelConfig());
    }
}
