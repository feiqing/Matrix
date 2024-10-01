package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.test.impl.base.BaseFunctionExtImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/9 14:30.
 */
@Component
public class NormalFunctionExtImpl extends BaseFunctionExtImpl {

    @Override
    public Object apply(Object arg) {
        if (arg != null) {
            System.out.println("argType: " + arg.getClass());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", "normal");
        result.put("result", arg);

        return result;
    }
}
