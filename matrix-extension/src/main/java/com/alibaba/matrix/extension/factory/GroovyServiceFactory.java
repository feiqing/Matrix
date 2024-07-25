package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.model.Groovy;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * fixme: 仅作为一个Demo演示groovy类型扩展实现的支持, 没做任何的优化和异常处理, 千万不要应用到任何实际业务当中
 *
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2024/7/25 22:00.
 */
public class GroovyServiceFactory {

    private static final ConcurrentMap<String, ConfigService> nacosConfigServices = new ConcurrentHashMap<>();

    private static final GroovyShell groovyShell = new GroovyShell();

    public static Object getGroovyService(Class<?> ext, Groovy groovy) {
        AtomicReference<Script> scriptRef;
        if (StringUtils.equalsIgnoreCase(groovy.protocol, "nacos")) {
            scriptRef = getNacosScript(groovy);
        } else if (StringUtils.equalsIgnoreCase(groovy.protocol, "file")) {
            scriptRef = getFileScript(groovy);
        } else if (StringUtils.equalsIgnoreCase(groovy.protocol, "http")) {
            scriptRef = getHttpScript(groovy);
        } else {
            throw new ExtensionException("Unsupported protocol: " + groovy.protocol);
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(ext.getClassLoader());
        enhancer.setInterfaces(new Class[]{ext});
        enhancer.setCallback(new GroovyExtensionImplProxy(scriptRef));
        return enhancer.create();
    }

    private static class GroovyExtensionImplProxy implements MethodInterceptor {

        private final AtomicReference<Script> scriptRef;

        public GroovyExtensionImplProxy(AtomicReference<Script> scriptRef) {
            this.scriptRef = scriptRef;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (ReflectionUtils.isObjectMethod(method)) {
                return method.invoke(this, args);
            }
            return scriptRef.get().invokeMethod(method.getName(), args);
        }
    }

    /**
     * http的方式不支持动态加载
     *
     * @param groovy
     * @return
     */
    private static AtomicReference<Script> getHttpScript(Groovy groovy) {
        try {
            return new AtomicReference<>(groovyShell.parse(URI.create(groovy.path)));
        } catch (IOException e) {
            throw new ExtensionException(e);
        }
    }

    /**
     * file的方式不支持动态加载
     *
     * @param groovy
     * @return
     */
    private static AtomicReference<Script> getFileScript(Groovy groovy) {
        try {
            File file = new File(groovy.path);
            Preconditions.checkArgument(file.exists());
            return new AtomicReference<>(groovyShell.parse(file));
        } catch (IOException e) {
            throw new ExtensionException(e);
        }
    }

    private static AtomicReference<Script> getNacosScript(Groovy groovy) {
        try {
            Map<String, String> config = Splitter.on(";").trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(groovy.path);
            String dataId = config.get("dataId");
            String group = config.get("group");
            Preconditions.checkArgument(!StringUtils.isAnyEmpty(dataId, group));

            ConfigService nacosConfigService = getNacosConfigService(config);

            AtomicReference<Script> scriptRef = new AtomicReference<>();
            String script = nacosConfigService.getConfig(dataId, group, Optional.ofNullable(config.get("timeout")).map(Long::parseLong).orElse(1000L));
            scriptRef.set(groovyShell.parse(Objects.requireNonNull(script)));
            nacosConfigService.addListener(dataId, group, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String scriptText) {
                    scriptRef.set(groovyShell.parse(Objects.requireNonNull(scriptText)));
                }
            });
            return scriptRef;
        } catch (NacosException e) {
            throw new ExtensionException(e);
        }
    }

    private static ConfigService getNacosConfigService(Map<String, String> config) {
        String serverAddr = config.get(PropertyKeyConst.SERVER_ADDR);
        String namespace = config.get(PropertyKeyConst.NAMESPACE);
        return nacosConfigServices.computeIfAbsent(String.format("%s#%s", serverAddr, namespace), _K -> {
            Properties properties = new Properties();
            // @菲青 目前serverAddr还是必填的
            if (!Strings.isNullOrEmpty(serverAddr)) {
                properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
            }
            if (!Strings.isNullOrEmpty(namespace)) {
                properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
            }
            try {
                return NacosFactory.createConfigService(properties);
            } catch (NacosException e) {
                throw new ExtensionException(e);
            }
        });
    }
}
