package com.alibaba.matrix.config.service;

import com.alibaba.matrix.base.util.MatrixServiceLoader;
import com.alibaba.matrix.config.service.provider.PropertiesConfigService;

import java.util.List;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
public class ConfigServiceProvider {

    public static List<ConfigService> configServices;

    static {
        configServices = MatrixServiceLoader.loadServices(ConfigService.class, new PropertiesConfigService());
    }
}
