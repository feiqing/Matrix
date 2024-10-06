package com.alibaba.matrix.config.validator;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
public class PassAllValidator implements Validator {

    @Override
    public boolean validate(Context context) {
        return true;
    }
}
