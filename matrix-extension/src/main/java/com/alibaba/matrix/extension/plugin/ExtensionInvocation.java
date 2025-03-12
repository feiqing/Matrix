package com.alibaba.matrix.extension.plugin;

import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.alibaba.matrix.extension.reducer.Reducer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/8/12 11:20.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExtensionInvocation {

    @Getter
    private final String namespace;

    @Getter
    private final List<String> codes;

    @Getter
    private final Class<?> extension;

    @Getter
    private final Function action;

    @Getter
    private final Reducer reducer;

    @Getter
    private final String code;

    @Getter
    @Setter
    private String type;

    @Getter
    public int priority;

    @Getter
    @Setter
    public String desc;

    @Getter
    @Setter
    private Object instance;

    private final ExtensionPlugin[] plugins;

    public ExtensionInvocation(ExtensionExecuteContext ctx, ExtensionImplEntity impl, ExtensionPlugin[] plugins) {
        this.namespace = ctx.namespace;
        this.codes = ctx.codes;
        this.extension = ctx.extension;
        this.action = ctx.action;
        this.reducer = ctx.reducer;

        this.code = impl.code;
        this.type = impl.type;
        this.priority = impl.priority;
        this.desc = impl.desc;

        this.instance = impl.instance;
        this.plugins = plugins;
    }

    private int idx = -1;

    public Object proceed() {
        if (++idx < plugins.length) {
            return plugins[idx].invoke(this);
        } else {
            return action.apply(instance);
        }
    }
}
