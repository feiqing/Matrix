package com.alibaba.matrix.extension.reducer;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/05/19
 */
public class Collect<T> implements Reducer<T, List<T>> {

    private final Predicate<T> predicate;

    public Collect() {
        this(null);
    }

    public Collect(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public List<T> reduce(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<T> results = new ArrayList<>(list.size());
        for (T t : list) {
            if (predicate == null || predicate.test(t)) {
                results.add(t);
            }
        }

        return results;
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
