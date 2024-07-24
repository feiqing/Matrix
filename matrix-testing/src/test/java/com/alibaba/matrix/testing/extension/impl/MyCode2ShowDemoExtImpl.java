package com.alibaba.matrix.testing.extension.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:08.
 */
@ExtensionImpl(code = {"my-code-2", "group.1:my-code-2"})
public class MyCode2ShowDemoExtImpl extends ShowDemoExtBaseImpl {

    @Override
    public Object showDemo() {
        return "my-code-2 show demo ext impl";
    }
}
