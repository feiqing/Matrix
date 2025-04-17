
package com.alibaba.matrix.extension.reducer;

import java.util.List;

/**
 * The Reducer interface defines a contract for reducing a list of elements of type T into a single result of type R.
 * Implementations of this interface are responsible for providing the logic to perform the reduction operation.
 *
 * @param <T> the type of elements to be reduced
 * @param <R> the type of the result produced by the reduction
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/05/19
 */
public interface Reducer<T, R> {

    /**
     * Reduces the given list of elements into a single result.
     *
     * @param list the list of elements to be reduced
     * @return the result of the reduction
     */
    R reduce(List<T> list);

    /**
     * Determines whether the execution of the remaining implementations should be interrupted.
     * Note: When concurrency is supported, it may not be possible to interrupt the execution of lower priority implementations
     * because it is not executed serially.
     *
     * @param t the current element being processed
     * @return true if the execution should be interrupted, false otherwise
     */
    boolean willBreak(T t);

    /**
     * Determines whether the reduction operation should be executed in parallel.
     * Three conditions must be met for concurrent execution:
     * 1. parallel() returns true
     * 2. ExtensionParallelConfig#enable() returns true
     * 3. The router hits multiple extension implementations.
     *
     * @return true if the reduction should be executed in parallel, false otherwise
     */
    boolean parallel();

    /**
     * Returns the name of the reducer implementation.
     * By default, it returns the simple class name of the implementation.
     *
     * @return the name of the reducer
     */
    default String name() {
        return this.getClass().getSimpleName();
    }
}