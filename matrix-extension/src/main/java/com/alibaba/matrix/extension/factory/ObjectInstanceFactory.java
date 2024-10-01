package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.base.util.MatrixUtils;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ObjectT;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/25 17:29.
 */
public class ObjectInstanceFactory {

    public static Object newInstance(ObjectT object) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = Class.forName(object.clazz);
        if (StringUtils.isEmpty(object.args)) {
            return clazz.newInstance();
        }

        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 1) {
                Object constructorArg = MatrixUtils.str2obj(object.args, constructor.getGenericParameterTypes()[0]);
                return constructor.newInstance(constructorArg);
            }
        }

        throw new ExtensionRuntimeException("cold not found one arg constructor in type: " + clazz.getName() + ".");
    }
}
