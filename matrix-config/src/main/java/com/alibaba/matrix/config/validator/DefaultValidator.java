package com.alibaba.matrix.config.validator;

import java.util.Objects;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
public class DefaultValidator implements Validator {

    @Override
    public boolean validate(Context context) {
        return Objects.nonNull(context.getValueObj());
    }
}
