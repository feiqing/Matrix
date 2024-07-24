package com.alibaba.matrix.extension.reducer;

import java.util.List;

/**
 * T是扩展点执行后的结果类型
 * R是reducer执行后的结果类型
 *
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @since 2022/05/19
 */
public interface Reducer<T, R> {

    /**
     * reduce方法
     *
     * @param list
     * @return
     */
    R reduce(List<T> list);

    /**
     * 一次扩展点执行完成后是否中断接下来的扩展点执行
     *
     * @param t
     * @return
     */
    boolean willBreak(T t);

    /**
     * T、R是否为相同类型
     *
     * @return
     */
    boolean sameType();
}
