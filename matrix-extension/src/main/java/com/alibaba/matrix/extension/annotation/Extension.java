package com.alibaba.matrix.extension.annotation;

import java.lang.annotation.*;

/**
 * 扩展Ext 定义
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {

    /**
     * Extension描述
     *
     * @return
     */
    String desc() default "";

}