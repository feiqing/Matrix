package com.alibaba.matrix.extension.model;

import com.alibaba.matrix.base.message.Message;
import com.alibaba.matrix.extension.exception.ExtensionException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Getter
public enum ExtensionImplType implements Serializable {

    BASE("BASE"),

    OBJECT("OBJECT"),

    BEAN("BEAN"),

    GUICE("GUICE"),

    PROVIDER("PROVIDER"),

    HSF("HSF"),

    DUBBO("DUBBO"),

    HTTP("HTTP"),

    GROOVY("GROOVY"),

    SPEL("SPEL"),

    ;

    private final String desc;

    ExtensionImplType(String desc) {
        this.desc = desc;
    }

    public static ExtensionImplType fromStr(String name) {
        for (ExtensionImplType value : values()) {
            if (StringUtils.equalsIgnoreCase(value.name(), name)) {
                return value;
            }
        }
        throw new ExtensionException(Message.of("MATRIX-EXTENSION-0001-0005", name).getMessage());
    }
}