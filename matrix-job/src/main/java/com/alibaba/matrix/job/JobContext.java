package com.alibaba.matrix.job;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2025/2/10 16:41.
 */
class JobContext implements Serializable {

    private static final long serialVersionUID = 6392898597976639655L;

    final boolean throwsExecuteException;

    final Collection<Pair<String, Object>> outputs;

    final Collection<Throwable> exceptions = new ConcurrentLinkedQueue<>();

    final AtomicLong executed = new AtomicLong(0L);

    final AtomicLong excepted = new AtomicLong(0L);

    final AtomicLong skipped = new AtomicLong(0L);

    final long start = System.currentTimeMillis();

    JobContext(boolean throwsExecuteException, Collection<Pair<String, Object>> outputs) {
        this.throwsExecuteException = throwsExecuteException;
        this.outputs = outputs;
    }

    int size() {
        return CollectionUtils.size(outputs);
    }

    long cost() {
        return System.currentTimeMillis() - start;
    }

    void addOutput(String key, Object output) {
        if (outputs != null) {
            outputs.add(Pair.of(key, output));
        }
    }

    void addException(Throwable throwable) {
        if (throwable != null) {
            exceptions.add(throwable);
        }
    }
}