package com.alibaba.matrix.extension.proxy.cglib;

import com.alibaba.matrix.extension.core.ExtensionExecutor;
import com.alibaba.matrix.extension.reducer.Reducer;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:49.
 */
@SuppressWarnings("unchecked")
public class CglibProxyFactory<Ext> implements MethodInterceptor {

    private final String scope;

    private final Class<Ext> ext;

    private final Reducer<?, ?> reducer;

    public CglibProxyFactory(String scope, Class<Ext> ext, Reducer<?, ?> reducer) {
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
        enhancer.setCallback(new CglibProxyFactory<>(scope, ext, reducer));
        return (Ext) enhancer.create();
    }
}
