package com.alibaba.matrix.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The ConfigCenter annotation is used to mark a class as a configuration center.
 * It specifies the namespace and a description for the configuration center.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigCenter {

    /**
     * The namespace of the configuration center.
     * If not specified, it defaults to an empty string.
     *
     * @return the namespace of the configuration center
     */
    String namespace() default "";

    /**
     * A description of the configuration center.
     * This is optional and can be used for documentation purposes.
     *
     * @return the description of the configuration center
     */
    String desc() default "";
}
