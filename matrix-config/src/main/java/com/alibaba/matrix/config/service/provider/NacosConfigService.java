package com.alibaba.matrix.config.service.provider;

import com.alibaba.matrix.config.service.ConfigService;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.shaded.com.google.common.base.Strings;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2021/2/23 20:00.
 */
public class NacosConfigService implements ConfigService {

    private final long timeout;

    private final com.alibaba.nacos.api.config.ConfigService nacosConfigService;

    public NacosConfigService(String serverAddr, String namespace, long timeout) {
        this.timeout = timeout;

        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
        if (!Strings.isNullOrEmpty(namespace)) {
            properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
        }
        try {
            nacosConfigService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getConfig(String namespace, String key) {
        try {
            return nacosConfigService.getConfig(key, namespace, timeout);
        } catch (NacosException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public void addConfigListener(String namespace, String key, Consumer<String> consumer) {
        try {
            nacosConfigService.addListener(key, namespace, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String newData) {
                    consumer.accept(newData);
                }
            });
        } catch (NacosException e) {
            ExceptionUtils.rethrow(e);
        }
    }
}
