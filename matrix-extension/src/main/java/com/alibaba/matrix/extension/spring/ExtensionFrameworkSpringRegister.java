package com.alibaba.matrix.extension.spring;

import com.alibaba.matrix.extension.ExtensionFrameworkRegister;
import com.alibaba.matrix.extension.factory.SpringBeanFactory;
import com.alibaba.matrix.extension.plugin.ExtensionPlugin;
import com.alibaba.matrix.extension.router.BaseExtensionRouter;
import com.alibaba.matrix.extension.router.ExtensionRouter;
import lombok.Data;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Data
public class ExtensionFrameworkSpringRegister implements ApplicationListener<ContextRefreshedEvent> {

    private static final AtomicBoolean started = new AtomicBoolean(false);

    private boolean enableAnnotationScan = false;

    private List<String> scanPackages = Collections.emptyList();

    private boolean enableXmlConfig = false;

    private List<String> configLocations = Collections.emptyList();

    private ExtensionRouter extensionRouter = new BaseExtensionRouter();

    private List<ExtensionPlugin> extensionPlugins = Collections.emptyList();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SpringBeanFactory.setApplicationContext(event.getApplicationContext());

        ExtensionFrameworkRegister register = new ExtensionFrameworkRegister();
        register.setEnableAnnotationScan(enableAnnotationScan);
        register.setScanPackages(scanPackages);
        register.setEnableXmlConfig(enableXmlConfig);
        register.setConfigLocations(configLocations);
        register.setExtensionRouter(extensionRouter);
        register.setExtensionPlugins(extensionPlugins);
        register.init();
    }
}