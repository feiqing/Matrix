package com.alibaba.matrix.extension.reducer;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/05/19
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
     * Return First Result
     *
     * @param <T>
     * @return
     */
    public static <T> First<T> first() {
        return (First<T>) FIRST;
    }

    /**
     * Return First NonNull Result
     *
     * @param <T>
     * @return
     */
    public static <T> First<T> firstNotNull() {
        return (First<T>) FIRST_NOT_NULL;
    }

    /**
     * Return The First Result Meets The Condition
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> First<T> first(Predicate<T> predicate) {
        return new First<>(predicate);
    }

    /**
     * Return Any Result
     *
     * @param <T>
     * @return
     */
    public static <T> Any<T> any() {
        return (Any<T>) ANY;
    }

    /**
     * Return Any NonNull Result
     *
     * @param <T>
     * @return
     */
    public static <T> Any<T> anyNotNull() {
        return (Any<T>) ANY_NOT_NULL;
    }

    /**
     * Return Any Result That Meets The Condition
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Any<T> any(Predicate<T> predicate) {
        return new Any<>(predicate);
    }


    /**
     * Return All Results
     *
     * @param <T>
     * @return
     */
    public static <T> Collect<T> collect() {
        return (Collect<T>) COLLECT;
    }

    /**
     * Return All NonNull Results
     *
     * @param <T>
     * @return
     */
    public static <T> Collect<T> collectNotNull() {
        return (Collect<T>) COLLECT_NOT_NULL;
    }

    /**
     * Return All Results That Meet The Condition
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Collect<T> collect(Predicate<T> predicate) {
        return new Collect<>(predicate);
    }

    /**
     * Return Number Of Results
     *
     * @param <T>
     * @return
     */
    public static <T> Count<T> count() {
        return (Count<T>) COUNT;
    }

    /**
     * Return Number Of NonNull Results
     *
     * @param <T>
     * @return
     */
    public static <T> Count<T> countNotNull() {
        return (Count<T>) COUNT_NOT_NULL;
    }

    /**
     * Return Number of Results that meet the condition
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Count<T> count(Predicate<T> predicate) {
        return new Count<>(predicate);
    }

    /**
     * Return Whether Any Of The Results Meet The Criteria
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> AnyMatch<T> anyMatch(Predicate<T> predicate) {
        return new AnyMatch<>(predicate);
    }

    /**
     * Return Whether All Results Meet The Criteria
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> AllMatch<T> allMatch(Predicate<T> predicate) {
        return new AllMatch<>(predicate);
    }

    /**
     * Return Whether All Results Do Not Meet The Criteria
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> NoneMatch<T> noneMatch(Predicate<T> predicate) {
        return new NoneMatch<>(predicate);
    }

}
