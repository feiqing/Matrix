package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.core.ExtensionManager;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.alibaba.matrix.extension.util.Message;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/11/13 10:48.
 */
public class BaseExtensionRouter implements ExtensionRouter {

    @Override
    public List<ExtensionImplEntity> route(ExtensionExecuteContext context) {
        String scope = context.scope;
        String code = context.code;
        Class<?> extension = context.extension;
        Preconditions.checkArgument(scope != null);
        Preconditions.checkArgument(code != null);

        List<ExtensionImplEntity> impls = ExtensionManager.getExtensionImpls(scope, code, extension);
        if (CollectionUtils.isEmpty(impls)) {
            throw new ExtensionRuntimeException(Message.format("MATRIX-EXTENSION-0000-0003", extension.getName(), scope, code));
        }
        return impls;
    }
}
