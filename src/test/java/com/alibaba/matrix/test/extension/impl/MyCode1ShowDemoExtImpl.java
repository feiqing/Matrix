package com.alibaba.matrix.test.extension.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.test.extension.ext.ShowDemoExt;

import static com.alibaba.matrix.test.extension.constants.ExtensionConstants.GROUP_2;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:07.
 */
@ExtensionImpl(group = GROUP_2, code = "my-code-1", desc = "MyCode1ShowDemoExtImpl")
public class MyCode1ShowDemoExtImpl implements ShowDemoExt {

    @Override
    public Object showDemo() {
        return "my-code-1 show demo ext impl";
    }
}
