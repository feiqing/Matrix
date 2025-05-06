package com.alibaba.matrix.job;

import java.util.function.Function;

/**
 * Task interface definition, representing an executable task.
 * This is a functional interface that allows creating task instances using lambda expressions.
 *
 * @param <Input>  The type of input parameter
 * @param <Output> The type of output parameter
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2020/9/18 13:41.
 */
@FunctionalInterface
public interface Task<Input, Output> extends Function<Input, Output> {

    /**
     * Perform initialization before task execution.
     * Subclasses or implementing classes can override this method to perform necessary setup work.
     *
     * @param input The input parameter for the task
     */
    default void setUp(Input input) {
    }

    /**
     * Execute the core logic of the task.
     * Must be implemented by subclasses or implementing classes.
     *
     * @param input The input parameter for the task
     * @return The result of the task execution
     */
    Output execute(Input input);

    /**
     * Perform cleanup after task execution.
     * Subclasses or implementing classes can override this method to perform necessary cleanup work.
     *
     * @param input  The input parameter for the task
     * @param output The result of the task execution
     */
    default void tearDown(Input input, Output output) {
    }

    /**
     * Get the name of the task, defaulting to the class name.
     * Subclasses or implementing classes can override this method to provide a more descriptive name.
     *
     * @return The name of the task
     */
    default String name() {
        return this.getClass().getName();
    }

    /**
     * Generate a unique key for the task based on the input parameter, defaulting to the object's string representation.
     * Subclasses or implementing classes can override this method to generate a more precise key.
     *
     * @param input The input parameter for the task
     * @return The key for the task
     */
    default String key(Input input) {
        return this.toString();
    }

    @Override
    default Output apply(Input input) {
        return execute(input);
    }
}
