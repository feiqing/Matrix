package com.alibaba.matrix.extension.reducer;

import java.util.List;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/05/19
 */
public interface Reducer<T, R> {

    R reduce(List<T> list);

    /**
     * Whether to interrupt the execution of the remaining implementations
     * Note: When concurrency is supported, it may not be possible to interrupt the execution of lower priority implementations because it is not executed serially.
     */
    boolean willBreak(T t);

    /**
     * Three Conditions For Concurrent Execution:
     * 1. parallel() = true
     * 2. ExtensionParallelConfig#enable() = true
     * 3. Router Hit Multiple Extension Implementations.
     */
    boolean parallel();

    default String name() {
        return this.getClass().getSimpleName();
    }
}
