package com.alibaba.matrix.extension.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_SCOPE;

/**
 * 扩展Ext 实现
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionImpl {

    String[] scope() default BASE_SCOPE;

    String[] code();

    int priority() default 0;

    String desc() default "";

    Class<?> belong() default Object.class;
}