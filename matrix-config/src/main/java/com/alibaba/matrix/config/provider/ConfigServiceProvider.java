package com.alibaba.matrix.config.provider;

import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/28 11:27.
 */
public class ConfigServiceProvider {

    public static ConfigService configService;

    static {
        configService = MatrixServiceLoader.loadService(ConfigService.class, new NoopConfigService());
    }
}
