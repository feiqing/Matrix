package com.alibaba.matrix.testing.extension.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.testing.extension.ext.ShowDemoExt;
import org.springframework.stereotype.Component;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:07.
 */
@Component
@ExtensionImpl(code = "my-code-1", desc = "MyCode1ShowDemoExtImpl")
public class MyCode1ShowDemoExtImpl implements ShowDemoExt {

    @Override
    public Object showDemo() {
        return "my-code-1 show demo ext impl";
    }
}
