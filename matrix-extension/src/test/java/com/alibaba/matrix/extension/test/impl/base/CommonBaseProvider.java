package com.alibaba.matrix.extension.test.impl.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.matrix.base.json.JsonMapperProvider;
import com.alibaba.matrix.extension.test.ext.DemoRemoteExt;

/**
 * @author jimi.zhu@temu.com
 * @version 1.0
 * @since 2024/10/11 21:12.
 */
public class CommonBaseProvider {

    public static DemoRemoteExt newDemoRemoteExtBaseImpl() {
        return (param0, param1, param2) -> {
            System.out.println("Base:");
            System.out.println("\targ0 = " + param0);
            System.out.println("\targ1 = " + JSON.toJSONString(param1));
            System.out.println("\targ2 = " + param2);
            return "Base: " + JsonMapperProvider.jsonMapper.toJson(param1);
        };
    }
}
