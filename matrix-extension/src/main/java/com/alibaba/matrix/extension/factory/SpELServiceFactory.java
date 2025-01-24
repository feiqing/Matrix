package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.config.SpEL;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * warning: Do not use in Product Environment!!!
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Slf4j
public class SpELServiceFactory {

    private static final ConcurrentMap<String, AtomicReference<String>> scriptRefs = new ConcurrentHashMap<>();

    public static Object getSpELService(Class<?> ext, SpEL spel) {
        Preconditions.checkArgument(StringUtils.equals("nacos", spel.protocol));

        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(ext.getClassLoader());
        enhancer.setInterfaces(new Class[]{ext});
        enhancer.setCallback(new SpELImplAdaptor(spel));
        Object service = enhancer.create();

        log.info("SpEL service init success, spel protocol:[{}] path:[{}].", spel.protocol, spel.path);
        return service;
    }

    private static class SpELImplAdaptor implements MethodInterceptor {

        private final SpEL spel;

        public SpELImplAdaptor(SpEL spel) {
            this.spel = spel;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (ReflectionUtils.isObjectMethod(method)) {
                return method.invoke(this, args);
            }

            Class<?> resultType = method.getReturnType();
            Parameter[] parameters = method.getParameters();

            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < parameters.length; i++) {
                context.setVariable(parameters[i].getName(), args[i]);
            }

            return new SpelExpressionParser().parseExpression(getNacosScript(spel, method.getName())).getValue(context, resultType);
        }
    }

    private static String getNacosScript(SpEL spel, String method) {
        Map<String, String> config = Splitter.on(";").trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(spel.path);
        String dataId = config.get("dataId");
        String group = config.get("group");
        Preconditions.checkArgument(!StringUtils.isAnyEmpty(dataId, group));

        String destDataId = dataId + "." + method + ".spel";
        return scriptRefs.computeIfAbsent(String.format("%s#%s", group, destDataId), _K -> {
            try {
                ConfigService configService = getConfigService(config);

                AtomicReference<String> scriptRef = new AtomicReference<>();
                String script = configService.getConfig(destDataId, group, Optional.ofNullable(config.get("timeout")).map(Long::parseLong).orElse(1000L));
                scriptRef.set(script);
                configService.addListener(destDataId, group, new AbstractListener() {
                    @Override
                    public void receiveConfigInfo(String scriptText) {
                        scriptRef.set(scriptText);
                    }
                });
                return scriptRef;
            } catch (NacosException e) {
                return ExceptionUtils.rethrow(e);
            }
        }).get();
    }

    private static final ConcurrentMap<String, ConfigService> configServiceMap = new ConcurrentHashMap<>();

    private static ConfigService getConfigService(Map<String, String> config) {
        String serverAddr = config.get(PropertyKeyConst.SERVER_ADDR);
        Preconditions.checkArgument(StringUtils.isNotBlank(serverAddr));
        String namespace = config.get(PropertyKeyConst.NAMESPACE);
        return configServiceMap.computeIfAbsent(String.format("%s#%s", serverAddr, namespace), _K -> {
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.setProperty(PropertyKeyConst.NAMESPACE, StringUtils.isBlank(namespace) ? "" : namespace);
            try {
                return NacosFactory.createConfigService(properties);
            } catch (NacosException e) {
                return ExceptionUtils.rethrow(e);
            }
        });
    }
}
