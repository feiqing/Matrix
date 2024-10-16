package com.alibaba.matrix.extension.factory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.matrix.extension.model.config.Dubbo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Slf4j
public class DubboServiceFactory {

    public static Object getDubboService(Class<?> ext, Dubbo dubbo) {
        Preconditions.checkArgument(!StringUtils.isAllEmpty(dubbo.version, dubbo.group));

        ReferenceConfig<Object> reference = new ReferenceConfig<>();
        reference.setInterface(ext);
        reference.setCheck(dubbo.check);
        
        if (dubbo.version != null) {
            reference.setVersion(dubbo.version);
        }
        if (dubbo.group != null) {
            reference.setGroup(dubbo.group);
        }
        if (dubbo.timeout != null) {
            reference.setTimeout(dubbo.timeout);
        }
        if (dubbo.filter != null) {
            reference.setFilter(dubbo.filter);
        }

        if (StringUtils.isNotBlank(dubbo.applicationName)) {
            reference.setApplication(new ApplicationConfig(dubbo.applicationName));
        }

        if (StringUtils.isNotBlank(dubbo.registryAddress)) {
            reference.setRegistry(new RegistryConfig(dubbo.registryAddress));
        }

        Object serviceObj = reference.get();

        Preconditions.checkState(serviceObj != null, String.format("Dubbo service:[%s] version:[%s] group:[%s] init failed.", ext.getName(), dubbo.version, dubbo.group));
        log.info("Dubbo service:[{}] version:[{}] group:[{}] init success.", ext.getName(), dubbo.version, dubbo.group);

        return serviceObj;
    }
}
