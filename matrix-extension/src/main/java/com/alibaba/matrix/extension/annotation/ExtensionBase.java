package com.alibaba.matrix.extension.annotation;

import com.alibaba.matrix.extension.model.ExtensionImplType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.matrix.extension.model.ExtensionImplType.OBJECT;

/**
 * 扩展Ext 基础实现
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionBase {

    /**
     * ExtensionBase实现类型(如: object、bean、guice)
     *
     * @return
     */
    ExtensionImplType type() default OBJECT;

    /**
     * ExtensionBase实现的Extension
     *
     * @return
     */
    Class<?> belong() default Object.class;

}