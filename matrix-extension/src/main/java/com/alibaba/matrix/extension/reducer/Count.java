package com.alibaba.matrix.extension.reducer;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/18 16:21.
 */
public class Count<T> implements Reducer<T, Long> {

    private final Predicate<T> predicate;

    public Count() {
        this(null);
    }

    public Count(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Long reduce(List<T> list) {
        long count = 0;
        for (T t : list) {
            if (predicate == null || predicate.test(t)) {
                ++count;
            }
        }
        return count;
    }

    @Override
    public boolean willBreak(T t) {
        return false;
    }

    @Override
    public boolean parallel() {
        return true;
    }
}
