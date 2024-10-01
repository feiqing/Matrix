package com.alibaba.matrix.extension.test.impl.base;

import lombok.NoArgsConstructor;
import org.apache.commons.collections4.Bag;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/25 17:46.
 */
@Component("hashBag")
@NoArgsConstructor
public class HashBag<T> implements Bag<T> {

    public final Bag<T> hashBag = new org.apache.commons.collections4.bag.HashBag<>();

    public HashBag(List<Integer> list) {
        hashBag.addAll((Collection<? extends T>) list);
    }

    @Override
    public int getCount(Object object) {
        return hashBag.getCount(object);
    }

    @Override
    public boolean add(T object) {
        return hashBag.add(object);
    }

    @Override
    public boolean add(T object, int nCopies) {
        return hashBag.add(object, nCopies);
    }

    @Override
    public boolean remove(Object object) {
        return hashBag.remove(object);
    }

    @Override
    public boolean remove(Object object, int nCopies) {
        return hashBag.remove(object, nCopies);
    }

    @Override
    public Set<T> uniqueSet() {
        return hashBag.uniqueSet();
    }

    @Override
    public int size() {
        return hashBag.size();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return hashBag.containsAll(coll);
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        return hashBag.removeAll(coll);
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        return hashBag.retainAll(coll);
    }

    @Override
    public Iterator<T> iterator() {
        return hashBag.iterator();
    }

    @Override
    public boolean isEmpty() {
        return hashBag.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return hashBag.contains(o);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return hashBag.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return hashBag.toArray(a);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return hashBag.addAll(c);
    }

    @Override
    public void clear() {
        hashBag.clear();
    }
}
