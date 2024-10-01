package com.alibaba.matrix.config.validator;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2023/9/26 16:08.
 */
public class PassAllValidator implements Validator {

    @Override
    public boolean validate(Context context) {
        return true;
    }
}
