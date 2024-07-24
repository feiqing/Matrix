package com.alibaba.matrix.extension.annotation;

import java.lang.annotation.*;

/**
 * 扩展Ext 定义
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/30 14:45.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {

    String desc() default "";

}