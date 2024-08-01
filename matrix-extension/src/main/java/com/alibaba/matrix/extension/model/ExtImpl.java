package com.alibaba.matrix.extension.model;

import com.alibaba.matrix.extension.exception.ExtensionException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/7/23 10:42.
 */
public class ExtImpl implements Serializable {

    private static final long serialVersionUID = -8095928529159883845L;

    /**
     * {@link Type}
     */
    public final String type;

    public final Object instance;

    public ExtImpl(@Nonnull String type, @Nonnull Object instance) {
        this.type = type;
        this.instance = instance;
    }

    @Getter
    public enum Type implements Serializable {

        BASE("BASE"),

        BEAN("BEAN"),

        HSF("HSF"),

        DUBBO("DUBBO"),

        HTTP("HTTP"),

        GROOVY("GROOVY"),

        ;

        private final String desc;

        Type(String desc) {
            this.desc = desc;
        }

        public static Type fromStr(String name) {
            for (Type value : values()) {
                if (StringUtils.equalsIgnoreCase(value.name(), name)) {
                    return value;
                }
            }
            throw new ExtensionException("ExtImpl type:[" + name + "] not supported.");
        }
    }
}
