package com.alibaba.matrix.extension.factory;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.matrix.extension.model.Dubbo;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/30 13:13.
 */
public class DubboServiceFactory {

    private static final ConcurrentMap<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static Object getDubboService(Class<?> ext, Dubbo dubbo) {
        Preconditions.checkArgument(!StringUtils.isAllEmpty(dubbo.version, dubbo.group));
        String serviceKey = String.format("%s#%s#%s#%s#%s", ext.getName(), dubbo.version, dubbo.group, dubbo.timeout, dubbo.filter);

        return serviceMap.computeIfAbsent(serviceKey, _K -> {
            ReferenceConfig<Object> reference = new ReferenceConfig<>();
            reference.setInterface(ext);
            if (dubbo.version != null) {
                reference.setVersion(dubbo.version);
            }
            if (dubbo.group != null) {
                reference.setGroup(dubbo.group);
            }
            if (dubbo.timeout != null) {
                reference.setTimeout(dubbo.timeout);
            }
            if (dubbo.check != null) {
                reference.setCheck(dubbo.check);
            }
            if (dubbo.filter != null) {
                reference.setFilter(dubbo.filter);
            }

            Object serviceObj = reference.get();
            Preconditions.checkState(serviceObj != null, String.format("DubboService:[%s:%s:%s] init failed.", ext.getName(), dubbo.version, dubbo.group));
            log.info("DubboService:[{}:{}:{}] serviceObj:[{}] init success.", ext.getName(), dubbo.version, dubbo.group, serviceObj);

            return serviceObj;
        });
    }
}
