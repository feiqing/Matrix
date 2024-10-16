package com.alibaba.matrix.extension.test.entrace;

import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.test.ext.DemoRemoteExt;

import java.util.function.BiFunction;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2024/10/12 17:50.
 */
public class WrapExtensionInvoke {

    public static <Ext, R> R invoke(String code, Class<Ext> ext, BiFunction<Ext, Object[], R> action, Object... args) {
        return ExtensionInvoker.invoke(code, ext, ext1 -> action.apply(ext1, args));
    }

    public static void main(String[] x) {
        invoke("", DemoRemoteExt.class, (demoRemoteExt, args) -> demoRemoteExt.apply(args[0], args[1], args[2]), "nihao", "wohao", "tayehao");
    }
}
