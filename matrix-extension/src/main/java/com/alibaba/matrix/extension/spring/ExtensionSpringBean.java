package com.alibaba.matrix.extension.spring;

import com.alibaba.matrix.extension.proxy.ProxyFactory;
import com.alibaba.matrix.extension.reducer.Reducer;
import com.google.common.base.Preconditions;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import static com.alibaba.matrix.extension.ExtensionInvoker.BASE_SCOPE;
import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/1/26 22:34.
 */
@Setter
public class ExtensionSpringBean<Ext> implements FactoryBean<Ext> {

    private Class<Ext> ext;

    private Reducer<?, ?> reducer;

    private boolean singleton = true;

    @Override
    public Ext getObject() {
        Preconditions.checkState(ext != null && reducer != null && reducer.sameType());
        Ext proxy = ProxyFactory.newProxy(BASE_SCOPE, ext, reducer);
        log.info("Register ext:[{}] reducer:[{}] bean:[{}] into Spring Context.", ext, reducer, proxy);
        return proxy;
    }

    @Override
    public Class<Ext> getObjectType() {
        return ext;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }
}
