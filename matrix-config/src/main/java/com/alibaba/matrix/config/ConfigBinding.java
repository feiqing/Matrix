package com.alibaba.matrix.config;

import com.alibaba.matrix.config.deserializer.DefaultDeserializer;
import com.alibaba.matrix.config.deserializer.Deserializer;
import com.alibaba.matrix.config.validator.DefaultValidator;
import com.alibaba.matrix.config.validator.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018/6/23 22:46.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigBinding {

    String key();

    String desc() default "";

    Class<? extends Deserializer> deserializer() default DefaultDeserializer.class;

    Class<? extends Validator> validator() default DefaultValidator.class;
}
