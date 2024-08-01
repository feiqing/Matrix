package com.alibaba.matrix.extension.proxy.spring;

import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:38.
 */
@SuppressWarnings("unchecked")
public class SpringProxyFactory<Ext> implements MethodInterceptor {

    private final String scope;

    private final Class<Ext> ext;

    private final Reducer<?, ?> reducer;

    public SpringProxyFactory(String scope, Class<Ext> ext, Reducer<?, ?> reducer) {
        this.scope = scope;
        this.ext = ext;
        this.reducer = reducer;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (ReflectionUtils.isObjectMethod(method)) {
            return method.invoke(this, objects);
        }
        return ExtensionExecutor.callback(scope, ext, method, objects, reducer);
    }

    public static <Ext> Ext newProxy(String scope, Class<Ext> ext, Reducer<?, ?> reducer) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(ext.getClassLoader());
        enhancer.setInterfaces(new Class[]{ext});
        enhancer.setCallback(new SpringProxyFactory<>(scope, ext, reducer));
        return (Ext) enhancer.create();
    }
}
