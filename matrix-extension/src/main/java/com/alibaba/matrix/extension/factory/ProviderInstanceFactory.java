package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.base.util.MatrixUtils;
import com.alibaba.matrix.extension.model.config.Provider;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Slf4j
public class ProviderInstanceFactory {

    public static Object getProviderInstance(Provider provider) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Method method = null;
        for (Method m : Class.forName(provider.clazz).getDeclaredMethods()) {
            if (m.getName().equals(provider.method)) {
                if (StringUtils.isBlank(provider.arg0)) {
                    method = m;
                    break;
                }

                if (StringUtils.isNotBlank(provider.arg0) && m.getParameterCount() == 1) {
                    method = m;
                    break;
                }
            }
        }

        Preconditions.checkState(method != null);

        Object instance;
        if (StringUtils.isBlank(provider.arg0)) {
            instance = method.invoke(null);
        } else {
            Object arg0 = MatrixUtils.str2obj(provider.arg0, method.getGenericParameterTypes()[0]);
            instance = method.invoke(null, arg0);
        }

        Preconditions.checkState(instance != null, String.format("Provider instance init failed, provider class:[%s] method:[%s] arg0:[%s].", provider.clazz, provider.method, provider.arg0));
        log.info("Provider instance init success, provider class:[{}] method:[{}] arg0:[{}].", provider.clazz, provider.method, provider.arg0);

        return instance;
    }
}
