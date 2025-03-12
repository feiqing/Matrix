package com.alibaba.matrix.extension.reducer;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/05/19
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
