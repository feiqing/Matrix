package com.alibaba.matrix.config.validator;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
public class PassAllValidator implements Validator {

    @Override
    public boolean validate(Context context) {
        return true;
    }
}
