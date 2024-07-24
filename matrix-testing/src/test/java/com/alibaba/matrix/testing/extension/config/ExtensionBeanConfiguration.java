package com.alibaba.matrix.testing.extension.config;

import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.spring.ExtensionFrameworkLoader;
import com.alibaba.matrix.extension.spring.ExtensionSpringBean;
import com.google.common.base.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:17.
 */
@Configuration
public class ExtensionBeanConfiguration {

    @Bean
    public ExtensionFrameworkLoader extensionFrameworkLoader() {
        ExtensionFrameworkLoader extensionFrameworkLoader = new ExtensionFrameworkLoader();
        extensionFrameworkLoader.setEnableAnnotationScan(true);
        extensionFrameworkLoader.setScanPackages(Arrays.asList("com.alibaba.matrix.testing.extension"));

        extensionFrameworkLoader.setEnableXmlConfig(true);
        extensionFrameworkLoader.setConfigLocations(Arrays.asList("classpath*:matrix-extension-*.xml"));

        return extensionFrameworkLoader;
    }

    @Bean
    public ExtensionSpringBean<Function> guavaFunctionExt() {
        ExtensionSpringBean<Function> springBean = new ExtensionSpringBean<>();
        springBean.setExt(Function.class);
        springBean.setReducer(Reducers.firstOf());
        return springBean;
    }
}
