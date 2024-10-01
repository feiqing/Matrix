package com.alibaba.matrix.base.message;

import com.google.common.base.Joiner;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.PropertyKey;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rocky Yu
 * @since 2022/9/16
 */
@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 19894637804946296L;

    private static final MessageSource messageSource;

    static {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasenames("i18n/message");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        messageSource = resourceBundleMessageSource;
    }

    private static final Message defaultMessage = Message.of("MATRIX-BASE-0000-0000");

    public static Message defaultMessage() {
        return defaultMessage;
    }

    /**
     * @param code the code.
     * @param args the args
     * @return
     */
    public static Message of(@PropertyKey(resourceBundle = "i18n.message") String code, Object... args) {
        return new Message(code, messageSource.getMessage(code, args, LocaleContextHolder.getLocale()));
    }

    /**
     * the error code.
     */
    private final String code;

    /**
     * the error message.
     */
    private final String message;

    /**
     * the error group
     */
    private String group;

    /**
     * the error content create the error.
     */
    private Map<String, Object> attributes;

    public Message(String code, String message) {
        this(code, message, null, new HashMap<>());
    }

    public Message(String code, String message, String group, Map<String, Object> attributes) {
        this.code = code;
        this.message = message;
        this.group = group;
        this.attributes = attributes;
    }

    public void addAttribute(String key, Object value) {
        getAttributes().put(key, value);
    }

    public Object getAttribute(String key) {
        return getAttributes().get(key);
    }

    public Object removeAttribute(String key) {
        return getAttributes().remove(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("code='").append(code).append("', message='").append(message).append("'");
        if (StringUtils.isNotBlank(group)) {
            sb.append(", group='").append(group).append("'");
        }
        if (MapUtils.isNotEmpty(attributes)) {
            sb.append(", attributes={").append(Joiner.on(", ").withKeyValueSeparator(" => ").join(attributes)).append("}");
        }

        return sb.toString();
    }
}
