package com.alibaba.matrix.test.extension.impl;


import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.test.extension.ext.ShowDemoExt;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:06.
 */
@ExtensionBase
public class ShowDemoExtBaseImpl implements ShowDemoExt {

    @Override
    public Object showDemo() {
        return "base show demo impl";
    }
}
