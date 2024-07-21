package com.alibaba.matrix.extension.reducer;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @since 2022/05/19
 */
public class AnyMatch<T> extends AbstractReducer<T, Boolean> {

    private final Predicate<T> predicate;

    public AnyMatch(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public Boolean reduce(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        for (T t : list) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean willBreak(T t) {
        return predicate.test(t);
    }
}
