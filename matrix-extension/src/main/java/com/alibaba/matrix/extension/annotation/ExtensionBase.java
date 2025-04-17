package com.alibaba.matrix.extension.annotation;

import com.alibaba.matrix.extension.core.ExtensionImplType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.matrix.extension.core.ExtensionImplType.OBJECT;

/**
 * Defines the default implementation type and the belonging type of an extension.
 * This annotation is used to mark the default implementation of an extension interface.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionBase {

    /**
     * The default implementation type of the extension.
     * Default value is {@link ExtensionImplType#OBJECT}.
     *
     * @return The default implementation type
     */
    ExtensionImplType type() default OBJECT;

    /**
     * The belonging type of the extension.
     * Default value is {@link Object}, indicating no specific belonging type.
     *
     * @return The belonging type
     */
    Class<?> belong() default Object.class;

}