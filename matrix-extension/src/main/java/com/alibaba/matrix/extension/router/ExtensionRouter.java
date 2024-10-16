package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;

import java.util.List;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:42.
 */
public interface ExtensionRouter {

    /**
     * 获取扩展实现
     *
     * @param context
     * @return
     */
    List<ExtensionImplEntity> route(ExtensionExecuteContext context);
}
