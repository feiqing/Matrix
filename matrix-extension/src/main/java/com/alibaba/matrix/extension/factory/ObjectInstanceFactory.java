package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.base.util.MatrixUtils;
import com.alibaba.matrix.extension.model.config.ObjectT;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Slf4j
public class ObjectInstanceFactory {

    public static Object getObjectInstance(ObjectT object) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = Class.forName(object.clazz);

        Object instance = null;
        if (StringUtils.isEmpty(object.arg0)) {
            instance = clazz.newInstance();
        } else {
            Optional<Constructor<?>> optional = Arrays.stream(clazz.getConstructors()).filter(constructor -> constructor.getParameterCount() == 1).findFirst();
            if (optional.isPresent()) {
                Object constructorArg = MatrixUtils.str2obj(object.arg0, optional.get().getGenericParameterTypes()[0]);
                instance = optional.get().newInstance(constructorArg);
            }
        }

        Preconditions.checkState(instance != null, String.format("Object instance init failed, object class:[%s] arg0:[%s].", object.clazz, object.arg0));
        log.info("Object instance init success, object class:[{}] arg0:[{}].", object.clazz, object.arg0);

        return instance;
    }
}
