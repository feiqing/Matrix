package com.alibaba.matrix.extension.reducer;

import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/05/19
 */
public class AllMatch<T> implements Reducer<T, Boolean> {

    private final Predicate<T> predicate;

    public AllMatch(Predicate<T> predicate) {
        Preconditions.checkArgument(predicate != null);
        this.predicate = predicate;
    }

    @Override
    public Boolean reduce(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        for (T t : list) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean willBreak(T t) {
        return !predicate.test(t);
    }

    @Override
    public boolean parallel() {
        return true;
    }
}
