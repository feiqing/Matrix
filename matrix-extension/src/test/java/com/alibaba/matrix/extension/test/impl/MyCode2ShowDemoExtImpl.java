package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.ext.ShowDemoExt;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_SCOPE;
import static com.alibaba.matrix.extension.test.constants.ExtensionConstants.SCOPE_1;
import static com.alibaba.matrix.extension.test.constants.ExtensionConstants.SCOPE_2;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:08.
 */
@ExtensionImpl(scope = {BASE_SCOPE, SCOPE_1, SCOPE_2}, code = {"my-code-2", "scope.1:my-code-2"}, extension = ShowDemoExt.class)
public class MyCode2ShowDemoExtImpl extends ShowDemoExtBaseImpl {

    @Override
    public Object showDemo() {
        return "my-code-2 show demo ext impl";
    }
}
