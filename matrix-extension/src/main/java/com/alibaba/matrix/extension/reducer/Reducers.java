package com.alibaba.matrix.extension.reducer;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 22:31.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Reducers {

    private static final First FIRST = new First();

    private static final First FIRST_NOT_NULL = new First(Objects::nonNull);

    private static final Any ANY = new Any();

    private static final Any ANY_NOT_NULL = new Any(Objects::nonNull);

    private static final Count COUNT = new Count();

    private static final Count COUNT_NOT_NULL = new Count(Objects::nonNull);

    private static final Collect COLLECT = new Collect();

    private static final Collect COLLECT_NOT_NULL = new Collect(Objects::nonNull);

    /**
     * 返回: 第一个结果
     *
     * @param <T>
     * @return
     */
    public static <T> First<T> first() {
        return (First<T>) FIRST;
    }

    /**
     * 返回: 第一个非null结果
     *
     * @param <T>
     * @return
     */
    public static <T> First<T> firstNotNull() {
        return (First<T>) FIRST_NOT_NULL;
    }

    /**
     * 返回: 满足条件的第一个结果
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> First<T> first(Predicate<T> predicate) {
        return new First<>(predicate);
    }

    /**
     * 返回: 任一个结果
     *
     * @param <T>
     * @return
     */
    public static <T> Any<T> any() {
        return (Any<T>) ANY;
    }

    /**
     * 返回: 任一个非null结果
     *
     * @param <T>
     * @return
     */
    public static <T> Any<T> anyNotNull() {
        return (Any<T>) ANY_NOT_NULL;
    }

    /**
     * 返回: 满足条件的任一个结果
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Any<T> any(Predicate<T> predicate) {
        return new Any<>(predicate);
    }


    /**
     * 返回: 所有结果
     *
     * @param <T>
     * @return
     */
    public static <T> Collect<T> collect() {
        return (Collect<T>) COLLECT;
    }

    /**
     * 返回: 所有非null结果
     *
     * @param <T>
     * @return
     */
    public static <T> Collect<T> collectNotNull() {
        return (Collect<T>) COLLECT_NOT_NULL;
    }

    /**
     * 返回: 满足条件的所有结果
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Collect<T> collect(Predicate<T> predicate) {
        return new Collect<>(predicate);
    }

    /**
     * 返回: 结果数量
     *
     * @param <T>
     * @return
     */
    public static <T> Count<T> count() {
        return (Count<T>) COUNT;
    }

    /**
     * 返回: 非null结果数量
     *
     * @param <T>
     * @return
     */
    public static <T> Count<T> countNotNull() {
        return (Count<T>) COUNT_NOT_NULL;
    }

    /**
     * 返回: 满足条件的结果数量
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Count<T> count(Predicate<T> predicate) {
        return new Count<>(predicate);
    }

    /**
     * 返回: 是否有任意一个结果满足条件
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> AnyMatch<T> anyMatch(Predicate<T> predicate) {
        return new AnyMatch<>(predicate);
    }

    /**
     * 返回: 是否所有结果都满足条件
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> AllMatch<T> allMatch(Predicate<T> predicate) {
        return new AllMatch<>(predicate);
    }

    /**
     * 返回: 是否所有结果都不满足条件
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> NoneMatch<T> noneMatch(Predicate<T> predicate) {
        return new NoneMatch<>(predicate);
    }

}
