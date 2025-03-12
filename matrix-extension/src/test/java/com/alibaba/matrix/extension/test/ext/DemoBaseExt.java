package com.alibaba.matrix.extension.test.ext;

import com.alibaba.matrix.extension.annotation.Extension;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/19 16:12.
 */
@Extension(desc = "测试扩展DemoBaseExt定义")
public interface DemoBaseExt {

    Object test(Object... args);

}
