package com.alibaba.matrix.extension.factory;

import com.google.common.io.CharStreams;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author jimi.zhu@temu.com
 * @version 1.0
 * @since 2024/8/11 20:34.
 */
public class LuaServiceFactory {

    private static final Globals globals = JsePlatform.standardGlobals();

    public static void main(String[] args) throws IOException {
        String script = CharStreams.toString(new InputStreamReader(Files.newInputStream(Paths.get("/Users/feiqing.zjf/Downloads/b.lua"))));
        globals.load(script).call();
    }

    private static class LuaImplAdaptor implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
            LuaValue[] luaArgs = new LuaValue[params.length];
            for (int i = 0; i < params.length; i++) {
                luaArgs[i] = CoerceJavaToLua.coerce(params[i]);
            }
            Varargs luaResult = globals.get(method.getName()).checkfunction().invoke(luaArgs);

            Object result = null;
            if (luaResult instanceof LuaNil) {
                result = null;
            } else if (luaResult instanceof LuaBoolean) {
                result = ((LuaBoolean) luaResult).toboolean();
            } else if (luaResult instanceof LuaInteger) {
                result = ((LuaInteger) luaResult).toint();
            } else if (luaResult instanceof LuaDouble) {
                result = ((LuaDouble) luaResult).todouble();
            } else if (luaResult instanceof LuaString) {
                result = ((LuaString) luaResult).tostring();
            } else if (luaResult instanceof LuaTable) {
                LuaTable table = (LuaTable) luaResult;
                // luaTable还不知道该怎么处理 sad...
            }

            return result;
        }
    }
}
