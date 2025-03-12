package com.alibaba.matrix.extension.test.impl;

import com.alibaba.matrix.extension.annotation.ExtensionImpl;
import com.alibaba.matrix.extension.test.impl.base.BaseDemoShowExtImpl;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/7/21 21:08.
 */
@Component
@ExtensionImpl(code = "code.b", desc = "Code B")
public class CodeBDemoShowExtImpl extends BaseDemoShowExtImpl {

    @Override
    public String desc() {
        return "code b show demo ext implementation.";
    }

    @Override
    public Object show(Object... args) {
        return "CodeBDemoShowExtImpl: " + Arrays.toString(args);
    }
}
