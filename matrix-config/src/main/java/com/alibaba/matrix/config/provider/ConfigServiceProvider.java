package com.alibaba.matrix.config.provider;

import com.alibaba.matrix.base.util.MatrixServiceLoader;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
public class ConfigServiceProvider {

    public static ConfigService configService;

    static {
        configService = MatrixServiceLoader.loadService(ConfigService.class, new NoopConfigService());
    }
}
