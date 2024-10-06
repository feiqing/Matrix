package com.alibaba.matrix.extension.reducer;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/05/19
 */
public class NoneMatch<T> implements Reducer<T, Boolean> {

    private final Predicate<T> predicate;

    public NoneMatch(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public Boolean reduce(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return true;
        }

        for (T t : list) {
            if (predicate.test(t)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean willBreak(T t) {
        return predicate.test(t);
    }

    @Override
    public boolean parallel() {
        return true;
    }
}
