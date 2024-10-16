package com.alibaba.matrix.testing.extension.config;

import com.alibaba.matrix.extension.router.LoggingExtensionRouter;
import com.alibaba.matrix.extension.spring.ExtensionFrameworkSpringRegister;
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
    public ExtensionFrameworkSpringRegister extensionFrameworkLoader() {
        ExtensionFrameworkSpringRegister extensionFrameworkSpringRegister = new ExtensionFrameworkSpringRegister();
        extensionFrameworkSpringRegister.setEnableAnnotationScan(true);
        extensionFrameworkSpringRegister.setScanPackages(Arrays.asList("com.alibaba.matrix.testing.extension"));

        extensionFrameworkSpringRegister.setEnableXmlConfig(true);
        extensionFrameworkSpringRegister.setConfigLocations(Arrays.asList("classpath*:matrix-extension-*.xml"));

        extensionFrameworkSpringRegister.setExtensionRouter(new LoggingExtensionRouter());

        return extensionFrameworkSpringRegister;
    }
}
