package com.alibaba.matrix.extension.test.ext;

import com.alibaba.matrix.extension.annotation.Extension;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:05.
 */
@Extension(desc = "演示Demo扩展定义")
public interface DemoShowExt {

    String desc();

    Object show(Object... args);
}
