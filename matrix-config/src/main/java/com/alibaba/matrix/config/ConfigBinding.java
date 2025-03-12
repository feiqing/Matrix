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
 * The ConfigBinding annotation is used to bind configuration properties to fields or methods in a class.
 * It specifies the key for the configuration property, default value, description, deserializer, and validator.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigBinding {

    /**
     * The default value for the defaultValue attribute.
     */
    String DEFAULT_VALUE = "";

    /**
     * The key of the configuration property.
     *
     * @return the key of the configuration property
     */
    String key();

    /**
     * The default value for the configuration property.
     * If not specified, it defaults to an empty string.
     *
     * @return the default value of the configuration property
     */
    String defaultValue() default DEFAULT_VALUE;

    /**
     * A description of the configuration property.
     * This is optional and can be used for documentation purposes.
     *
     * @return the description of the configuration property
     */
    String desc() default "";

    /**
     * The deserializer class to use for converting the configuration property value to the desired type.
     * If not specified, it defaults to {@link DefaultDeserializer}.
     *
     * @return the deserializer class
     */
    Class<? extends Deserializer> deserializer() default DefaultDeserializer.class;

    /**
     * The validator class to use for validating the configuration property value.
     * If not specified, it defaults to {@link DefaultValidator}.
     *
     * @return the validator class
     */
    Class<? extends Validator> validator() default DefaultValidator.class;
}
