package com.alibaba.matrix.base.test;

import com.alibaba.matrix.base.serializer.provider.JsonSerializer;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * @author jimi.zhu@temu.com
 * @version 1.0
 * @since 2024/10/6 23:10.
 */
public class Main {

    public static void main(String[] args) {
        //创建Objenesis，内部包含一个实例化策略对象StdInstantiatorStrategy
        Objenesis objenesis = new ObjenesisStd();
        //获取对象实例化器
        ObjectInstantiator<JsonSerializer> objectInstantiator = objenesis.getInstantiatorOf(JsonSerializer.class);
        JsonSerializer o = objectInstantiator.newInstance();
        //实例化对象
        System.out.println(o);
    }
}
