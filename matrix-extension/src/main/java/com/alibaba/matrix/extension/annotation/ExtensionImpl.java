package com.alibaba.matrix.extension.annotation;

import com.alibaba.matrix.extension.model.ExtensionImplType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_SCOPE;
import static com.alibaba.matrix.extension.model.ExtensionImplType.OBJECT;

/**
 * 扩展Extension 实现
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2023/11/20 18:00.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionImpl {

    /**
     * Extension(所属)领域定义
     *
     * @return
     */
    String[] scope() default BASE_SCOPE;

    /**
     * Extension身份定义
     *
     * @return
     */
    String[] code();

    /**
     * ExtensionImpl实现类型(如: object、bean、guice)
     *
     * @return
     */
    ExtensionImplType type() default OBJECT;

    /**
     * Extension优先级定义: 越小优先级越高
     *
     * @return
     */
    int priority() default 0;

    /**
     * 启用懒加载: 运行时加载实现
     *
     * @return
     */
    boolean lazy() default false;

    /**
     * Extension实现描述
     *
     * @return
     */
    String desc() default "";

    /**
     * ExtensionImpl实现的Extension
     *
     * @return
     */
    Class<?> belong() default Object.class;

}