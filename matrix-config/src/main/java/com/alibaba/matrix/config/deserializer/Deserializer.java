package com.alibaba.matrix.config.deserializer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 反序列化器
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018/6/23 23:09.
 */
public interface Deserializer {

    Object deserialize(Context context) throws Exception;

    @Data
    @AllArgsConstructor
    class Context {

        private final String key;

        private final String desc;

        private final String valueStr;

        private final Class<?> clazz;

        private final Type type;

        private final Field field;

        private final Method method;

        private final Class<?> belongs;
    }
}
