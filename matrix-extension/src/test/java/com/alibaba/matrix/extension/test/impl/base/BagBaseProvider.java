package com.alibaba.matrix.extension.test.impl.base;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import java.util.List;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2024/10/10 14:29.
 */
public class BagBaseProvider {

    public static Bag<?> newInstance() {
        return new HashBag<>();
    }

    public static Bag<Integer> newInstance(List<Integer> ints) {
        return new HashBag<>(ints);
    }
}
