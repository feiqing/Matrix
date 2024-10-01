package com.alibaba.matrix.job;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2019/9/8 13:41.
 */
@FunctionalInterface
public interface Task<Input, Output> {

    default void setUp(Input input) {
    }

    Output execute(Input input);

    default void tearDown(Input input, Output output) {
    }

    /**
     * Lambda匿名类的情况需要注意: 由于不同机器生成的lambda匿名类id不同, 可能会导致Metrics信息过载, 所以最好还是覆盖该方法
     *
     * @return
     */
    default String name() {
        return this.getClass().getName();
    }

    default String key(Input input) {
        return this.toString();
    }
}
