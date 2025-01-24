package com.alibaba.matrix.config.service.provider;

import com.alibaba.matrix.config.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2021/2/23 20:00.
 */
public class YamlConfigService implements ConfigService {

    private static final ConcurrentMap<String, String> configs = new ConcurrentHashMap<>();

    public YamlConfigService() {
        this("classpath*:matrix-config-*.y*ml");
    }

    public YamlConfigService(String configLocation) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configLocation);
            for (Resource resource : resources) {
                // tips: Recursive optimization(todo)
                Yaml yaml = new Yaml();
                Map<String, String> config = yaml.load(resource.getInputStream());
                configs.putAll(config);
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
