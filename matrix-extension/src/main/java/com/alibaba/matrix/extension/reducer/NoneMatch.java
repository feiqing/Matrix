package com.alibaba.matrix.extension.reducer;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/19 08:19.
 */
public class NoneMatch<T> extends AbstractReducer<T, Boolean> {

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
}
