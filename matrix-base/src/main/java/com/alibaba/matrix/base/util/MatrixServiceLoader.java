package com.alibaba.matrix.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/8/28 11:01.
 */
@Slf4j
public class MatrixServiceLoader {

    public static <T> T loadService(Class<T> serviceType, T defaultService) {
        try {
            for (T service : ServiceLoader.load(serviceType, serviceType.getClassLoader())) {
                log.info("loaded [Service:{}] -> {}", serviceType.getName(), service);
                return service;
            }
            log.info("use [Service:{}] -> DEFAULT:{}.", serviceType.getName(), defaultService);
            return defaultService;
        } catch (Throwable t) {
            log.error("loading [Service:{}] error.", serviceType.getName(), t);
            return ExceptionUtils.rethrow(t);
        }
    }

    public static <T> List<T> loadServices(Class<T> serviceType, T defaultService) {
        try {
            List<T> services = new LinkedList<>();
            for (T service : ServiceLoader.load(serviceType, serviceType.getClassLoader())) {
                log.info("loaded [Service:{}] -> {}", serviceType.getName(), service);
                services.add(service);
            }

            if (CollectionUtils.isNotEmpty(services)) {
                return services;
            }

            log.info("use [Service:{}] -> DEFAULT:{}.", serviceType.getName(), defaultService);
            return Collections.singletonList(defaultService);
        } catch (Throwable t) {
            log.error("loading [Service:{}] error.", serviceType.getName(), t);
            return ExceptionUtils.rethrow(t);
        }
    }
}
