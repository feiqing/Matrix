package com.alibaba.matrix.config.service.provider;

import com.alibaba.matrix.config.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2021/2/23 20:00.
 */
public class PropertiesConfigService implements ConfigService {

    private final ConcurrentMap<String, String> configs = new ConcurrentHashMap<>();

    public PropertiesConfigService() {
        this("classpath*:matrix-config-*.properties");
    }

    public PropertiesConfigService(String configLocation) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configLocation);
            for (Resource resource : resources) {
                Properties properties = new Properties();
                if (StringUtils.endsWith(resource.getFilename(), "xml")) {
                    properties.loadFromXML(resource.getInputStream());
                } else {
                    properties.load(resource.getInputStream());
                }

                for (String key : properties.stringPropertyNames()) {
                    configs.put(key, properties.getProperty(key));
                }
            }
        } catch (IOException e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public String getConfig(String namespace, String key) {
        if (StringUtils.isBlank(namespace)) {
            return configs.get(key);
        } else {
            return configs.get(namespace + "." + key);
        }
    }

    @Override
    public void addConfigListener(String namespace, String key, Consumer<String> consumer) {
        // noop
    }
}
