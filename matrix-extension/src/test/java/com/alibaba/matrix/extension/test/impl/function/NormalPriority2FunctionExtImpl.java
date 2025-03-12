package com.alibaba.matrix.extension.test.impl.function;

import com.alibaba.matrix.extension.test.impl.base.BaseFunctionExtImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/9 14:30.
 */
@Component
public class NormalPriority2FunctionExtImpl extends BaseFunctionExtImpl {

    @Override
    public Object apply(Object arg) {
        if (arg != null) {
            System.out.println("argType: " + arg.getClass());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", "normal-priority-2");
        result.put("result", arg);

        return result;
    }
}
