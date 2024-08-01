package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.model.ExtImpl;

import java.io.Serializable;
import java.util.List;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:42.
 */
public interface ExtensionRouter extends Serializable {


    /**
     * 获取扩展实现, by ext、scope、code(from context)、args
     *
     * @param ext
     * @param scope
     * @param args
     * @return
     */
    List<ExtImpl> route(Class<?> ext, String scope, Object[] args);
}
