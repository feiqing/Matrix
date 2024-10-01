package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.ExtensionContext;
import com.alibaba.matrix.extension.test.impl.base.BaseTriFunctionImpl;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/18 20:11.
 */
public class ObjectTriFunctionExtImpl extends BaseTriFunctionImpl {

    private final String args;

    public ObjectTriFunctionExtImpl(String args) {
        this.args = args;
    }

    @Override
    public Object apply(Object o, Object o2, Object o3) {
        System.out.println("ObjectTriFunctionExtImpl objectArgs =  " + args + ", thread = " + Thread.currentThread().getName() + ", ctx = " + ExtensionContext.getExtCtx("ctx") + ", args: " + o + ", " + o2 + ", " + o3);
        return "ObjectTriFunctionExtImpl(" + args + ", " + o + ", " + o2 + ", " + o3 + ")";
    }
}
