package com.alibaba.matrix.extension.test.impl.base;

import com.alibaba.matrix.extension.test.ext.DemoRemoteExt;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/8 22:05.
 */
@Component
public class BlankDemoRemoteExtImpl implements DemoRemoteExt {

    @Override
    public Map<String, Object> apply(Object param0, Object param1, Object param2) {
        return null;
    }
}
