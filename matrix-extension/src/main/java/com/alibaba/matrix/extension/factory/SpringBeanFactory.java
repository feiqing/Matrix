package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.config.Bean;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Slf4j
public class SpringBeanFactory {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanFactory.applicationContext = applicationContext;
    }

    public static Object getSpringBean(Bean bean) throws ClassNotFoundException {
        Preconditions.checkArgument(!StringUtils.isAllBlank(bean.name, bean.clazz));
        Object instance;
        if (bean.name != null) {
            instance = getSpringBean(bean.name);
        } else {
            instance = getSpringBean(Class.forName(bean.clazz));
        }

        Preconditions.checkState(instance != null, String.format("Spring bean get failed, bean class:[%s] name:[%s].", bean.clazz, bean.name));
        log.info("Spring bean get success, bean class:[{}] name:[{}].", bean.clazz, bean.name);
        return instance;
    }

    private static Object getSpringBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    private static <T> T getSpringBean(Class<T> beanType) {
        // 1. Error when spring used jdk-proxy(not cglib)
        // 2. Error when @ExtensionImpl extends @ExtensionBase and @ExtensionBase as a spring component.
        return applicationContext.getBean(beanType);
    }
}
