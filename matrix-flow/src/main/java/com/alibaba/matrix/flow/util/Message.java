package com.alibaba.matrix.flow.util;

import org.jetbrains.annotations.PropertyKey;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author Rocky Yu
 * @since 2022/9/16
 */
public class Message {

    private static final MessageSource ms;

    static {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasenames("i18n/flow");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        ms = resourceBundleMessageSource;
    }

    public static String format(@PropertyKey(resourceBundle = "i18n.flow") String code, Object... args) {
        return ms.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
