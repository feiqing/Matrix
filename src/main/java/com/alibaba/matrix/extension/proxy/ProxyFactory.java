package com.alibaba.matrix.extension.proxy;

import com.alibaba.matrix.extension.proxy.cglib.CglibProxyFactory;
import com.alibaba.matrix.extension.proxy.jdk.JdkProxyFactory;
import com.alibaba.matrix.extension.proxy.spring.SpringProxyFactory;
import com.alibaba.matrix.extension.reducer.Reducer;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
public class ProxyFactory {

    private static volatile String type = null;

    public static <Ext> Ext newProxy(String group, Class<Ext> ext) {
        return newProxy(group, ext, null);
    }

    public static <Ext> Ext newProxy(String group, Class<Ext> ext, Reducer<?, ?> reducer) {
        if (type != null) {
            switch (type) {
                case "spring":
                    return SpringProxyFactory.newProxy(group, ext, reducer);
                case "cglib":
                    return CglibProxyFactory.newProxy(group, ext, reducer);
                case "jdk":
                    return JdkProxyFactory.newProxy(group, ext, reducer);
            }
        }

        try {
            Class.forName("org.springframework.cglib.proxy.Enhancer");
            type = "spring";
            return SpringProxyFactory.newProxy(group, ext, reducer);
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("net.sf.cglib.proxy.Enhancer");
            type = "cglib";
            return CglibProxyFactory.newProxy(group, ext, reducer);
        } catch (ClassNotFoundException ignored) {
        }

        type = "jdk";
        return JdkProxyFactory.newProxy(group, ext, reducer);
    }
}
