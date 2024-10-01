package com.alibaba.matrix.extension.reducer;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author feiqing.zjf@gmail.com
 * @version 2.0: 为并发而生!
 * @since 2023/9/13 11:26.
 */
public class Any<T> implements Reducer<T, T> {

    private final Predicate<T> predicate;

    public Any() {
        this(null);
    }

    public Any(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public T reduce(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.stream().filter(t -> predicate == null || predicate.test(t)).findAny().orElse(null);
    }

    @Override
    public boolean willBreak(T t) {
        return predicate == null || predicate.test(t);
    }

    @Override
    public boolean parallel() {
        return true;
    }
}
