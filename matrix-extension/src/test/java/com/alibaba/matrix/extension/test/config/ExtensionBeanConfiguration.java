package com.alibaba.matrix.extension.test.config;

import com.alibaba.matrix.extension.factory.GuiceInstanceFactory;
import com.alibaba.matrix.extension.plugin.ExtensionLoggingPlugin;
import com.alibaba.matrix.extension.router.BaseLoggingExtensionRouter;
import com.alibaba.matrix.extension.spring.ExtensionFrameworkSpringRegister;
import com.google.inject.Guice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:17.
 */
@Configuration
public class ExtensionBeanConfiguration {

    @Bean
    public ExtensionFrameworkSpringRegister extensionFrameworkLoader() {
        GuiceInstanceFactory.setGuiceInjector(Guice.createInjector(new TestGuiceModule()));

        ExtensionFrameworkSpringRegister extensionFrameworkSpringRegister = new ExtensionFrameworkSpringRegister();
        extensionFrameworkSpringRegister.setEnableAnnotationScan(true);
        extensionFrameworkSpringRegister.setScanPackages(Collections.singletonList("com.alibaba.matrix.extension.test"));
        extensionFrameworkSpringRegister.setEnableXmlConfig(true);
        extensionFrameworkSpringRegister.setConfigLocations(Collections.singletonList("classpath*:/extension/matrix-extension-*.xml"));
        extensionFrameworkSpringRegister.setExtensionPlugins(Arrays.asList(new ExtensionLoggingPlugin()));

        extensionFrameworkSpringRegister.setExtensionRouter(new BaseLoggingExtensionRouter());

        return extensionFrameworkSpringRegister;
    }
}
