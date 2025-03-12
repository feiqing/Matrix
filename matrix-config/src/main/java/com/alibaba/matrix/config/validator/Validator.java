package com.alibaba.matrix.config.validator;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
public interface Validator {

    boolean validate(Context context) throws Exception;

    @Data
    @AllArgsConstructor
    class Context {

        private final String key;

        private final String desc;

        private final String valueStr;

        private final Object valueObj;

        private final Field field;

        private final Method method;

        private final Class<?> clazz;

        @SuppressWarnings("unchecked")
        public <T> T getValueObj() {
            return (T) valueObj;
        }
    }
}
