package com.alibaba.matrix.extension.core;

import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.util.Message;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author <a href="mailto:jifang.zjf@alibaba-inc.com">jifang.zjf(FeiQing)</a>
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
        throw new ExtensionException(Message.format("MATRIX-EXTENSION-0001-0005", name));
    }
}
