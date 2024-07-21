package com.alibaba.matrix.extension.spring;

import com.alibaba.matrix.extension.core.ExtensionContainer;
import com.alibaba.matrix.extension.model.Extension;
import com.alibaba.matrix.extension.reducer.Reducers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.alibaba.matrix.extension.utils.Logger.log;


/**
 * 该方案暂时尚未测试完毕, 请务必不要应用到生产系统!!!
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/30 15:43.
 */
@Component
@Deprecated
public class ExtensionSpringBeanAutoRegister implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (Map.Entry<Class<?>, Extension> entry : ExtensionContainer.extensionMap.entrySet()) {
            Class<?> ext = entry.getKey();

            RootBeanDefinition definition = new RootBeanDefinition();
            definition.setBeanClass(ExtensionSpringBean.class);
            definition.getPropertyValues().addPropertyValue("ext", ext);
            definition.getPropertyValues().addPropertyValue("reducer", Reducers.firstOf());
            definition.setPrimary(true);

            String beanName = asBeanName(ext.getSimpleName());
            registry.registerBeanDefinition(beanName, definition);
            log.info("Register ext:[{}] reducer:[{}] bean:[{}] into Spring Context.", ext, Reducers.firstOf(), beanName);
        }
    }

    private String asBeanName(String extName) {
        return "matrix" + extName.substring(0, 1).toUpperCase() + extName.substring(1);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
}
