package com.alibaba.matrix.extension.test.impl.base;


import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.extension.test.ext.DemoShowExt;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:06.
 */
@ExtensionBase
public class BaseDemoShowExtImpl implements DemoShowExt {

    @Override
    public String desc() {
        return "base show demo ext implementation.";
    }

    @Override
    public Object show(Object... args) {
        return null;
    }
}
