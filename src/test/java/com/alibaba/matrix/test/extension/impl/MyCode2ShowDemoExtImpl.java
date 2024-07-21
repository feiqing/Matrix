package com.alibaba.matrix.test.extension.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_GROUP;
import static com.alibaba.matrix.test.extension.constants.ExtensionConstants.GROUP_1;
import static com.alibaba.matrix.test.extension.constants.ExtensionConstants.GROUP_2;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:08.
 */
@ExtensionImpl(group = {BASE_GROUP, GROUP_1, GROUP_2}, code = {"my-code-2", "group.1:my-code-2"})
public class MyCode2ShowDemoExtImpl extends ShowDemoExtBaseImpl {

    @Override
    public Object showDemo() {
        return "my-code-2 show demo ext impl";
    }
}
