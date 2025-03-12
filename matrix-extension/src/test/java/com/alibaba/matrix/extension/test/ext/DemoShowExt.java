package com.alibaba.matrix.extension.test.ext;

import com.alibaba.matrix.extension.annotation.Extension;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/7/21 21:05.
 */
@Extension(desc = "演示Demo扩展定义")
public interface DemoShowExt {

    String desc();

    Object show(Object... args);
}
