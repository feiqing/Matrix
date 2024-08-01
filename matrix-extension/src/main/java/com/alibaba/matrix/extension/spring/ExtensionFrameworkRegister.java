package com.alibaba.matrix.extension.spring;

import com.alibaba.matrix.extension.exception.ExtensionException;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 23:13.
 */
@Deprecated
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ExtensionFrameworkRegister implements ImportBeanDefinitionRegistrar {

    private static boolean enableAnnotationScan = false;

    private static List<String> scanPackages = Collections.emptyList();

    private static boolean enableXmlConfig = false;

    private static List<String> configLocations = Collections.emptyList();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttrs = annotationMetadata.getAnnotationAttributes(EnableExtension.class.getName());
        Preconditions.checkState(!CollectionUtils.isEmpty(annotationAttrs));

        if (BooleanUtils.isNotFalse((Boolean) annotationAttrs.get("enableAnnotationScan"))) {
            enableAnnotationScan = true;
            log.info("@EnableExtension enableAnnotationScan = true.");

            String[] scanPackagesAttr = (String[]) annotationAttrs.get("scanPackages");
            if (scanPackagesAttr == null || scanPackagesAttr.length == 0) {
                throw new ExtensionException("ScanPackages must not be empty.");
            }
            scanPackages = Arrays.stream(scanPackagesAttr).collect(Collectors.toList());
            log.info("@EnableExtension scanPackages = {}.", scanPackages);
        }

        if (BooleanUtils.isNotFalse((Boolean) annotationAttrs.get("enableXmlConfig"))) {
            enableXmlConfig = true;
            log.info("@EnableExtension enableXmlConfig = true.");

            String[] configLocationsAttr = (String[]) annotationAttrs.get("configLocations");
            if (configLocationsAttr == null || configLocationsAttr.length == 0) {
                throw new ExtensionException("ConfigLocations must not be empty.");
            }
            configLocations = Arrays.stream(configLocationsAttr).collect(Collectors.toList());
            log.info("@EnableExtension configLocations = {}.", configLocations);
        }
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ExtensionFrameworkLoader extensionFrameworkLoader() {
        ExtensionFrameworkLoader extensionFrameworkLoader = new ExtensionFrameworkLoader();
        extensionFrameworkLoader.setEnableAnnotationScan(enableAnnotationScan);
        extensionFrameworkLoader.setScanPackages(scanPackages);
        extensionFrameworkLoader.setEnableXmlConfig(enableXmlConfig);
        extensionFrameworkLoader.setConfigLocations(configLocations);
        return extensionFrameworkLoader;
    }
}
