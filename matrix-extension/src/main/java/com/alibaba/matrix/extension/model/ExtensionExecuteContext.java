package com.alibaba.matrix.extension.model;

import com.alibaba.matrix.extension.reducer.Reducer;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@AllArgsConstructor
public class ExtensionExecuteContext implements Serializable {

    private static final long serialVersionUID = 1374750581241420843L;

    public final String scope;

    public final String code;

    public final Class<?> extension;

    public final Function action;

    public final Reducer reducer;
}
