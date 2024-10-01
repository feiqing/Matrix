package com.alibaba.matrix.config.validator;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 校验器
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018/6/26 20:33.
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
