package com.alibaba.matrix.extension.model;

import com.alibaba.matrix.extension.exception.ExtensionException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 10:42.
 */
@Getter
public enum ExtImplType {

    BASE("BASE"),

    BEAN("BEAN"),

    HSF("HSF"),

    DUBBO("DUBBO"),

    HTTP("HTTP"),

    GROOVY("GROOVY"),

    ;

    private final String desc;

    ExtImplType(String desc) {
        this.desc = desc;
    }

    public static ExtImplType fromStr(String name) {
        for (ExtImplType value : values()) {
            if (StringUtils.equalsIgnoreCase(value.name(), name)) {
                return value;
            }
        }
        // todo msg
        throw new ExtensionException("ExtensionImpl type:[" + name + "] not supported.");
    }
}
