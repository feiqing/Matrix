package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.extension.core.ExtensionManager;
import com.alibaba.matrix.extension.exception.ExtensionRuntimeException;
import com.alibaba.matrix.extension.model.ExtensionExecuteContext;
import com.alibaba.matrix.extension.model.ExtensionImplEntity;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
        Class<?> ext = context.ext;
        Preconditions.checkArgument(!Strings.isNullOrEmpty(scope));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(code));

        List<ExtensionImplEntity> impls = ExtensionManager.getExtensionImpls(scope, code, ext);
        if (CollectionUtils.isEmpty(impls)) {
            throw new ExtensionRuntimeException(Message.of("MATRIX-EXTENSION-0000-0003", ext.getName(), scope, code).getMessage());
        }
        return impls;
    }
}
