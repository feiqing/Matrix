package com.alibaba.matrix.extension.annotation;

import com.alibaba.matrix.extension.core.ExtensionImplType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.matrix.extension.ExtensionInvoker.DEFAULT_NAMESPACE;
import static com.alibaba.matrix.extension.core.ExtensionImplType.OBJECT;

/**
 * This annotation is used to mark a class as an extension implementation within the Matrix framework.
 * It provides metadata about the extension such as namespace, code identifiers, type, priority, lazy loading behavior,
 * description, and the class it belongs to.
 *
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionImpl {

    /**
     * Specifies the namespace(s) under which this extension implementation is registered.
     * Defaults to {@link com.alibaba.matrix.extension.ExtensionInvoker#DEFAULT_NAMESPACE}.
     *
     * @return an array of namespace strings
     */
    String[] namespace() default DEFAULT_NAMESPACE;

    /**
     * Identifies the code or identifier(s) associated with this extension implementation.
     *
     * @return an array of code strings
     */
    String[] code();

    /**
     * Defines the type of this extension implementation.
     * Defaults to {@link ExtensionImplType#OBJECT}.
     *
     * @return the type of the extension implementation
     */
    ExtensionImplType type() default OBJECT;

    /**
     * Specifies the priority of this extension implementation.
     * Higher values indicate higher priority.
     * Defaults to 0.
     *
     * @return the priority of the extension implementation
     */
    int priority() default 0;

    /**
     * Indicates whether this extension implementation should be lazily loaded.
     * Defaults to false.
     *
     * @return true if the extension should be lazily loaded, false otherwise
     */
    boolean lazy() default false;

    /**
     * Provides a description of this extension implementation.
     * Defaults to an empty string.
     *
     * @return a description of the extension implementation
     */
    String desc() default "";

    /**
     * Specifies the class to which this extension implementation belongs.
     * Defaults to {@link Object}.
     *
     * @return the class to which the extension implementation belongs
     */
    Class<?> belong() default Object.class;

}