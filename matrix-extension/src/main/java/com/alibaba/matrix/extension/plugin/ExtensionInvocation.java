package com.alibaba.matrix.extension.plugin;

import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.alibaba.matrix.extension.reducer.Reducer;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/12 11:20.
 */
public class ExtensionInvocation {

    @Getter
    private final String scope;

    @Getter
    private final String code;

    @Getter
    private final Class<?> ext;

    @Getter
    private final Function action;

    @Getter
    private final Reducer reducer;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private Object instance;

    @Getter
    public int priority;

    @Getter
    @Setter
    public String desc;

    private final ExtensionPlugin[] plugins;

    public ExtensionInvocation(ExtensionExecuteContext ctx, ExtensionImplEntity impl, ExtensionPlugin[] plugins) {
        this.scope = ctx.scope;
        this.code = ctx.code;
        this.ext = ctx.ext;
        this.action = ctx.action;
        this.reducer = ctx.reducer;

        this.type = impl.type;
        this.instance = impl.instance;
        this.priority = impl.priority;
        this.desc = impl.desc;
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
