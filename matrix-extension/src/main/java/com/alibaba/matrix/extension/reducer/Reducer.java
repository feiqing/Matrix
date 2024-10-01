package com.alibaba.matrix.extension.reducer;

import java.util.List;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 2.0
 * @since 2022/05/19
 */
public interface Reducer<T, R> {

    R reduce(List<T> list);

    /**
     * 是否中断剩下的扩展实现执行
     * 注意: 当支持并发时, 由于并非顺序执行, 可能会导致并不能中断认可扩展执行.
     */
    boolean willBreak(T t);

    /**
     * 是否开启并发执行.
     * 并发执行的三个条件:
     * 1. parallel() = true
     * 2. ExtensionParallelConfig#enable() = true
     * 3. Router命中多个扩展实现.
     */
    boolean parallel();

    default String name() {
        return this.getClass().getSimpleName();
    }
}
