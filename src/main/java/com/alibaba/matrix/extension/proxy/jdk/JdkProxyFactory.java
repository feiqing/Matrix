package com.alibaba.matrix.extension.proxy.jdk;

import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:49.
 */
@SuppressWarnings("unchecked")
public class JdkProxyFactory<Ext> implements InvocationHandler {

    private final String group;

    private final Class<Ext> ext;

    private final Reducer<?, ?> reducer;

    public JdkProxyFactory(String group, Class<Ext> ext, Reducer<?, ?> reducer) {
        this.group = group;
        this.ext = ext;
        this.reducer = reducer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ReflectionUtils.isObjectMethod(method)) {
            return method.invoke(this, args);
        }
        return ExtensionExecutor.callback(group, ext, method, args, reducer);
    }

    public static <Ext> Ext newProxy(String group, Class<Ext> ext, Reducer<?, ?> reducer) {
        return (Ext) Proxy.newProxyInstance(ext.getClassLoader(), new Class[]{ext}, new JdkProxyFactory<>(group, ext, reducer));
    }
}
