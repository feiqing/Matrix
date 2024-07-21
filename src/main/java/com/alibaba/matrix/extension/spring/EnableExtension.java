package com.alibaba.matrix.extension.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/1 17:03.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ExtensionFrameworkRegister.class)
public @interface EnableExtension {

    boolean enableAnnotationScan() default false;

    String[] scanPackages() default {};

    boolean enableXmlConfig() default false;

    String[] configLocations() default {};
}
