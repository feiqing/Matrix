package com.alibaba.matrix.extension.reducer;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @since 2022/05/19
 */
public class FirstOf<T> extends AbstractReducer<T, T> {

    private final Predicate<T> predicate;

    public FirstOf() {
        this(null);
    }

    public FirstOf(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean willBreak(T t) {
        return predicate == null || predicate.test(t);
    }

    @Override
    public T reduce(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (T t : list) {
            if (predicate == null || predicate.test(t)) {
                return t;
            }
        }

        return null;
    }
}
