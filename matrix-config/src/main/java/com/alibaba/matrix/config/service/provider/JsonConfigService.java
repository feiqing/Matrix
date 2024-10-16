package com.alibaba.matrix.config.service.provider;

import com.alibaba.matrix.config.service.ConfigService;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2021/2/23 20:00.
 */
public class JsonConfigService implements ConfigService {

    private final ConcurrentMap<String, String> configs = new ConcurrentHashMap<>();

    public JsonConfigService() {
        this("classpath*:matrix-config-*.json");
    }

    public JsonConfigService(String configLocation) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(configLocation);
            for (Resource resource : resources) {
                String json = CharStreams.toString(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
                configs.putAll(jsonMapper.fromJson(json, new TypeToken<Map<String, String>>() {
                }));
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
