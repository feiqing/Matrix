package com.alibaba.matrix.extension.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_GROUP;

/**
 * 扩展Ext 实现
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/30 11:12.
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionImpl {

    String[] group() default BASE_GROUP;

    String[] code();

    int priority() default 0;

    String desc() default "";
}