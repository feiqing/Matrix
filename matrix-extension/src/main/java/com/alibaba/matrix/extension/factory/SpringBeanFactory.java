package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.Bean;
import com.google.common.base.Preconditions;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @since 2022/05/19
 */
public class SpringBeanFactory {

    private static final ConcurrentMap<Object, Object> cache = new ConcurrentHashMap<>();

    public static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanFactory.applicationContext = applicationContext;
    }

    public static Object getSpringBean(Bean bean) {
        Preconditions.checkState(!(bean.name == null && bean.clazz == null));
        if (bean.name != null) {
            return getSpringBean(bean.name);
        } else {
            return getSpringBean(bean.clazz);
        }
    }

    public static Object getSpringBean(String beanName) {
        return cache.computeIfAbsent(beanName, _beanName -> applicationContext.getBean(beanName));
    }

    public static <T> T getSpringBean(Class<T> beanType) {
        return (T) cache.computeIfAbsent(beanType, _beanType -> applicationContext.getBean(beanType));
    }
}
