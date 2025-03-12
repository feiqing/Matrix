package com.alibaba.matrix.testing.extension.impl;


import com.alibaba.matrix.extension.annotation.ExtensionBase;
import com.alibaba.matrix.testing.extension.ext.ShowDemoExt;

import static com.alibaba.matrix.extension.model.ExtensionImplType.OBJECT;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/7/21 21:06.
 */
@ExtensionBase(type = OBJECT)
public class ShowDemoExtBaseImpl implements ShowDemoExt {

    @Override
    public Object showDemo() {
        return "base show demo impl";
    }
}
