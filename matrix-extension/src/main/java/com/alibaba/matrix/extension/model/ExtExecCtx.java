package com.alibaba.matrix.extension.model;

import com.alibaba.matrix.extension.reducer.Reducer;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/6 11:36.
 */
@AllArgsConstructor
public class ExtExecCtx implements Serializable {

    private static final long serialVersionUID = 1374750581241420843L;

    public final String scope;

    public final String code;

    public final Class<?> ext;

    public final Function action;

    public final Reducer reducer;
}
