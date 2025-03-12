package com.alibaba.matrix.extension.test.config;

import com.alibaba.matrix.extension.test.impl.base.BaseDemoBaseExtImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.CollectionBag;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.bag.SynchronizedBag;
import org.apache.commons.collections4.bag.TreeBag;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2024/10/8 16:50.
 */
public class TestGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
//        Multibinder<Bag> bagBinder = Multibinder.newSetBinder(binder(), Bag.class);
//        bagBinder.addBinding().toInstance(PredicatedBag.predicatedBag(new HashBag<>(), object -> true));
//        bagBinder.addBinding().toInstance(PredicatedSortedBag.predicatedSortedBag(new TreeBag<>(), object -> true));
//        bagBinder.addBinding().toInstance(SynchronizedBag.synchronizedBag(new HashBag<>()));
//        bagBinder.addBinding().toInstance(SynchronizedSortedBag.synchronizedSortedBag(new TreeBag<>()));
//        bagBinder.addBinding().toInstance(TransformedBag.transformedBag(new HashBag<>(), NOPTransformer.nopTransformer()));
//        bagBinder.addBinding().toInstance(TransformedSortedBag.transformedSortedBag(new TreeBag<>(), NOPTransformer.nopTransformer()));
//        bagBinder.addBinding().toInstance(CollectionBag.collectionBag(new HashBag<>()));
//        bagBinder.addBinding().toInstance(UnmodifiableBag.unmodifiableBag(new HashBag<>()));

//        MapBinder.newMapbinder
//        MapBinder.newMapBinder(binder(), Bag.class)

//        bind(Bag.class).toConstructor()
//        bind(Bag.class).toInstance(SynchronizedBag.synchronizedBag(new HashBag<>()));
//        bind(Bag.class).toInstance(CollectionBag.collectionBag(new HashBag<>()));

//        Multibinder<Bag> setBinder = Multibinder.newSetBinder(binder(), Bag.class);
//        setBinder.addBinding().toInstance(SynchronizedBag.synchronizedBag(new HashBag<>()));
//        setBinder.addBinding().toInstance(CollectionBag.collectionBag(new HashBag<>()));
//        setBinder.permitDuplicates();

//        MapBinder<String, Bag> mapBinder = MapBinder.newMapBinder(binder(), String.class, Bag.class);
//        mapBinder.addBinding("synchronizedBag").toInstance(SynchronizedBag.synchronizedBag(new HashBag<>()));
//        mapBinder.addBinding("collectionBag").toInstance(CollectionBag.collectionBag(new HashBag<>()));
        // bagBinder.permitDuplicates();

        bind(Key.get(Bag.class, Names.named("synchronizedBag"))).toInstance(SynchronizedBag.synchronizedBag(new TreeBag<>()));
        bind(Key.get(Bag.class, Names.named("collectionBag"))).toInstance(CollectionBag.collectionBag(new TreeBag<>()));
        bind(Key.get(HashBag.class, Names.named("hashBag"))).toInstance(new HashBag<>());
        bind(BaseDemoBaseExtImpl.class).toInstance(new BaseDemoBaseExtImpl());
    }
}
