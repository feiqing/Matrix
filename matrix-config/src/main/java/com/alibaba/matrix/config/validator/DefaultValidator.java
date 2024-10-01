package com.alibaba.matrix.config.validator;

import java.util.Objects;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2018/6/26 20:36.
 */
public class DefaultValidator implements Validator {

    @Override
    public boolean validate(Context context) {
        return Objects.nonNull(context.getValueObj());
    }
}
