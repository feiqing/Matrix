package com.alibaba.matrix.extension.router;

import com.alibaba.matrix.extension.core.ExtensionExecuteContext;
import com.alibaba.matrix.extension.core.ExtensionImplEntity;
import com.alibaba.matrix.extension.core.ExtensionManager;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.util.Message;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2023/11/13 10:48.
 */
public class BaseExtensionRouter implements ExtensionRouter {

    @Override
    public List<ExtensionImplEntity> route(ExtensionExecuteContext ctx) {
        String namespace = ctx.namespace;
        List<String> codes = ctx.codes;
        Class<?> extension = ctx.extension;
        Preconditions.checkArgument(StringUtils.isNotBlank(namespace));
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(codes) && codes.stream().allMatch(StringUtils::isNotBlank));
        Preconditions.checkArgument(extension != null);

        List<ExtensionImplEntity> impls = ExtensionManager.getExtensionImpls(namespace, codes, extension);
        if (CollectionUtils.isEmpty(impls)) {
            throw new ExtensionException(Message.format("MATRIX-EXTENSION-0000-0003", extension.getName(), namespace, codes));
        }
        return impls;
    }
}
