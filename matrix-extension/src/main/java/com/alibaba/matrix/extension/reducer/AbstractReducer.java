package com.alibaba.matrix.extension.reducer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2022/7/21 21:51.
 */
public abstract class AbstractReducer<T, R> implements Reducer<T, R> {
    
    @Override
    public boolean sameType() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: Reducer constructed without actual type information");
        }
        /* 22-Dec-2008, tatu: Not sure if this case is safe -- I suspect
         *   it is possible to make it fail?
         *   But let's deal with specific
         *   case when we know an actual use case, and thereby suitable
         *   workarounds for valid case(s) and/or error to throw
         *   on invalid one(s).
         */
        Type[] typeArguments = ((ParameterizedType) superClass).getActualTypeArguments();
        return typeArguments != null && typeArguments.length == 2 && typeArguments[0] == typeArguments[1];
    }
}
