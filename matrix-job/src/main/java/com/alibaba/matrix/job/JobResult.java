package com.alibaba.matrix.job;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2025/2/9 23:07.
 */
@Data
public class JobResult<Output> implements Serializable {

    private static final long serialVersionUID = -1036783111656601889L;

    /**
     * Pair<TaskKey, TaskOutput>
     */
    private Collection<Pair<String, Output>> outputs;

    /**
     * Job Execute Runtime Exception
     */
    private Collection<Throwable> exceptions;
}
