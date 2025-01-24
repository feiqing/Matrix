package com.alibaba.matrix.extension.annotation;

import com.alibaba.matrix.extension.model.ExtensionImplType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_SCOPE;
import static com.alibaba.matrix.extension.model.ExtensionImplType.OBJECT;

/**
 * Defines an implementation of an extension with various attributes.
 * This annotation is used to mark classes as specific implementations of an extension.
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionImpl {

    /**
     * The scope(s) of the extension implementation.
     * Default value is the base scope defined in {@link com.alibaba.matrix.extension.ExtensionInvoker#BASE_SCOPE}.
     *
     * @return The scope(s) of the extension implementation
     */
    String[] scope() default BASE_SCOPE;

    /**
     * The code(s) that identify the extension implementation.
     * This attribute is required.
     *
     * @return The code(s) of the extension implementation
     */
    String[] code();

    /**
     * The type of the extension implementation.
     * Default value is {@link ExtensionImplType#OBJECT}.
     *
     * @return The type of the extension implementation
     */
    ExtensionImplType type() default OBJECT;

    /**
     * The priority of the extension implementation.
     * Lower values indicate higher priority.
     * Default value is 0.
     *
     * @return The priority of the extension implementation
     */
    int priority() default 0;

    /**
     * Indicates whether the extension implementation should be lazily loaded.
     * Default value is false.
     *
     * @return Whether the extension implementation should be lazily loaded
     */
    boolean lazy() default false;

    /**
     * The description of the extension implementation.
     * Default value is an empty string.
     *
     * @return The description of the extension implementation
     */
    String desc() default "";

    /**
     * The belonging type of the extension implementation.
     * Default value is {@link Object\.class}, indicating no specific belonging type.
     *
     * @return The belonging type of the extension implementation
     */
    Class<?> belong() default Object.class;

}