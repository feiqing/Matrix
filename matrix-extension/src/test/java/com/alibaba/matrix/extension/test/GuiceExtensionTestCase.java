package com.alibaba.matrix.extension.test;

import com.alibaba.matrix.extension.ExtensionInvoker;
import com.alibaba.matrix.extension.factory.GuiceInstanceFactory;
import com.alibaba.matrix.extension.reducer.Reducers;
import com.alibaba.matrix.extension.test.config.TestGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.collections4.Bag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.function.Function;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/8/16 14:10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-*.xml")
public class GuiceExtensionTestCase {

    @Before
    public void setUp() {

    }

    static {
        Injector injector = Guice.createInjector(new TestGuiceModule());
        GuiceInstanceFactory.setGuiceInjector(injector);
    }


    @Test
    public void tt() {
        Boolean hah = ExtensionInvoker.invoke("code.tree", Bag.class, bag -> bag.add("hah", 4));
        System.out.println(hah);
        Integer hah1 = ExtensionInvoker.invoke("code.hash2", Bag.class, bag -> bag.getCount("hah"));
        System.out.println(hah1);
    }

    @Test
    public void test_dubbo() {
        Object hah = ExtensionInvoker.invoke("code.dubbo", Function.class, function -> function.apply("hah"));
        System.out.println(hah);
    }

    @Test
    public void tt2() {
        Object hah = ExtensionInvoker.invoke("code.guice.hash", Bag.class, bag -> bag.add("hah", 4), Reducers.collect());
        System.out.println(hah);
//        Integer hah1 = ExtensionInvoker.invoke("code.hash2", Bag.class, bag -> bag.getCount("hah"));
//        System.out.println(hah1);
    }

}
