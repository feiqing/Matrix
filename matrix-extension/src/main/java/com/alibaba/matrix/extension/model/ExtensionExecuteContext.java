package com.alibaba.matrix.extension.model;

import com.alibaba.matrix.extension.reducer.Reducer;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@AllArgsConstructor
@SuppressWarnings("all")
public class ExtensionExecuteContext implements Serializable {

    private static final long serialVersionUID = 1374750581241420843L;

    public final String namespace;

    public final List<String> codes;

    public final Class<?> extension;

    public final Function action;

    public final Reducer reducer;
}
