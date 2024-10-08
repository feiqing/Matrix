package com.alibaba.matrix.extension.test.config;

import com.alibaba.matrix.extension.plugin.ExtensionIndependentLoggingPlugin;
import com.alibaba.matrix.extension.plugin.ExtensionLoggingPlugin;
import com.alibaba.matrix.extension.spring.ExtensionFrameworkLoader;
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
    public ExtensionFrameworkLoader extensionFrameworkLoader() {
        ExtensionFrameworkLoader extensionFrameworkLoader = new ExtensionFrameworkLoader();
        extensionFrameworkLoader.setEnableAnnotationScan(true);
        extensionFrameworkLoader.setScanPackages(Collections.singletonList("com.alibaba.matrix.extension.test"));
        extensionFrameworkLoader.setEnableXmlConfig(true);
        extensionFrameworkLoader.setConfigLocations(Collections.singletonList("classpath*:/extension/matrix-extension-*.xml"));
        extensionFrameworkLoader.setExtensionPlugins(Arrays.asList(new ExtensionLoggingPlugin(), new ExtensionIndependentLoggingPlugin()));

        return extensionFrameworkLoader;
    }
}
