package com.alibaba.matrix.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an extension with an optional description.
 * This annotation is used to mark interfaces or classes as extensions.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {

    /**
     * The description of the extension.
     * Default value is an empty string.
     *
     * @return The description of the extension
     */
    String desc() default "";

}