package com.alibaba.matrix.config;

import com.alibaba.matrix.base.telemetry.trace.ISpan;
import com.alibaba.matrix.base.util.MatrixUtils;
import com.alibaba.matrix.config.deserializer.Deserializer;
import com.alibaba.matrix.config.exception.ConfigCenterException;
import com.alibaba.matrix.config.service.ConfigService;
import com.alibaba.matrix.config.util.Message;
import com.alibaba.matrix.config.validator.Validator;
import com.google.common.base.Preconditions;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.alibaba.matrix.base.telemetry.TelemetryProvider.metrics;
import static com.alibaba.matrix.base.telemetry.TelemetryProvider.tracer;
import static com.alibaba.matrix.config.service.ConfigServiceProvider.configServices;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/23 22:51.
 */
@Slf4j(topic = "Config")
public class ConfigFrameworkRegister {

    private static final ConcurrentMap<Class<?>, Object> clazz2instance = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, ConcurrentMap<String, Optional<String>>> namespace2key2data = new ConcurrentHashMap<>();

    private final List<String> scanPackages;

    public ConfigFrameworkRegister(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    public void init() {
        log.info("{}", Message.format("MATRIX-CONFIG-0001-0000", MatrixUtils.resolveProjectVersion(ConfigFrameworkRegister.class, "matrix-config")));
        if (scanPackages == null || scanPackages.isEmpty()) {
            throw new ConfigCenterException(Message.format("MATRIX-CONFIG-0000-0000"));
        }
        log.info("scanPackages: {}", scanPackages);

        Map<Pair<String, String>, List<Handler>> key2handlers = new ConcurrentHashMap<>();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(scanPackages.toArray(new String[0])).scan()) {
            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(ConfigCenter.class)) {
                Class<?> belong = classInfo.loadClass();
                ConfigCenter center = belong.getAnnotation(ConfigCenter.class);
                log.info("loaded @ConfigCenter:[{}] => namespace:[{}] desc:[{}]", belong.getName(), center.namespace(), center.desc());

                classInfo.getDeclaredFieldInfo().stream().filter(fieldInfo -> fieldInfo.hasAnnotation(ConfigBinding.class)).map(FieldInfo::loadClassAndGetField).forEach(field -> {
                    Handler handler = initFieldHandler(belong, center.namespace(), field);
                    key2handlers.computeIfAbsent(Pair.of(handler.namespace, handler.key), _K -> new LinkedList<>()).add(handler);
                    log.info("loaded @ConfigBinding field:[{}.{}] => namespace:[{}] key:[{}] desc:[{}].", handler.belongs.getName(), handler.field.getName(), handler.namespace, handler.key, handler.desc);
                });

                classInfo.getDeclaredMethodInfo().stream().filter(methodInfo -> methodInfo.hasAnnotation(ConfigBinding.class)).map(MethodInfo::loadClassAndGetMethod).forEach(method -> {
                    Handler handler = initMethodHandler(belong, center.namespace(), method);
                    key2handlers.computeIfAbsent(Pair.of(handler.namespace, handler.key), _K -> new LinkedList<>()).add(handler);
                    log.info("loaded @ConfigBinding method:[{}.{}] => namespace:[{}] key:[{}] desc:[{}].", handler.belongs.getName(), handler.method.getName(), handler.namespace, handler.key, handler.desc);
                });
            }
        }

        for (Map.Entry<Pair<String, String>, List<Handler>> entry : key2handlers.entrySet()) {
            String namespace = entry.getKey().getLeft();
            String key = entry.getKey().getRight();
            List<Handler> handlers = entry.getValue();

            String configData = getConfigData(namespace, key);
            handleConfigDataChanged(true, namespace, key, handlers, configData);
            addConfigListener(namespace, key, handlers);
        }

        log.info("{}", Message.format("MATRIX-CONFIG-0001-0001"));
    }

    private Handler initFieldHandler(Class<?> belongs, String namespace, Field field) {
        Preconditions.checkState(Modifier.isPublic(field.getModifiers()), "field:[%s.%s] must be public.", belongs.getName(), field.getName());
        Preconditions.checkState(Modifier.isStatic(field.getModifiers()), "field:[%s.%s] must be static.", belongs.getName(), field.getName());
        Preconditions.checkState(!Modifier.isFinal(field.getModifiers()), "field:[%s.%s] can not be final.", belongs.getName(), field.getName());
        if (!Modifier.isVolatile(field.getModifiers())) {
            log.warn("field:[{}.{}] recommend use [volatile].", belongs.getName(), field.getName());
        }

        ConfigBinding binding = field.getAnnotation(ConfigBinding.class);

        Handler handler = new Handler();
        handler.namespace = namespace;
        handler.key = binding.key();
        handler.defaultValue = binding.defaultValue();
        handler.desc = binding.desc();
        handler.belongs = belongs;
        handler.field = field;
        handler.type1 = field.getType();
        handler.type2 = field.getGenericType();
        handler.deserializer = newInstance("deserializer", binding.deserializer());
        handler.validator = newInstance("validator", binding.validator());

        return handler;
    }

    private Handler initMethodHandler(Class<?> clazz, String namespace, Method method) {
        Preconditions.checkState(Modifier.isPublic(method.getModifiers()), "method:[%s.%s] must be public.", clazz.getName(), method.getName());
        Preconditions.checkState(Modifier.isStatic(method.getModifiers()), "method:[%s.%s] must be static.", clazz.getName(), method.getName());

        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Preconditions.checkState(parameterTypes.length == 1 && genericParameterTypes.length == 1, "method:[%s.%s] must have only one param.", clazz.getName(), method.getName());

        ConfigBinding binding = method.getAnnotation(ConfigBinding.class);

        Handler handler = new Handler();
        handler.namespace = namespace;
        handler.key = binding.key();
        handler.defaultValue = binding.defaultValue();
        handler.desc = binding.desc();
        handler.belongs = clazz;
        handler.method = method;
        handler.type1 = parameterTypes[0];
        handler.type2 = genericParameterTypes[0];
        handler.deserializer = newInstance("deserializer", binding.deserializer());
        handler.validator = newInstance("validator", binding.validator());

        return handler;
    }

    private String getConfigData(String namespace, String key) {
        try {
            for (ConfigService configService : configServices) {
                String config = configService.getConfig(namespace, key);
                if (config != null) {
                    return config;
                }
            }

            return null;
        } catch (Throwable t) {
            throw new ConfigCenterException(Message.format("MATRIX-CONFIG-0000-0001", namespace, key), t);
        }
    }

    private void addConfigListener(String namespace, String key, List<Handler> handlers) {
        try {
            for (ConfigService configService : configServices) {
                configService.addConfigListener(namespace, key, newData -> handleConfigDataChanged(false, namespace, key, handlers, newData));
            }
        } catch (Throwable t) {
            throw new ConfigCenterException(Message.format("MATRIX-CONFIG-0000-0002", namespace, key), t);
        }
    }

    private void handleConfigDataChanged(boolean starting, String namespace, String key, List<Handler> handlers, String newData) {
        Map<String, Optional<String>> key2data = namespace2key2data.computeIfAbsent(namespace, _K -> new ConcurrentHashMap<>());
        Optional<String> oldData = key2data.get(key);
        if (oldData != null && oldData.isPresent()) {
            log.info("namespace:[{}] key:[{}] data changing from:[{}] to:[{}].", namespace, key, oldData.get(), newData);
        } else {
            log.info("namespace:[{}] key:[{}] data changing to:[{}].", namespace, key, newData);
        }

        if (handlers.isEmpty()) {
            log.warn("namespace:[{}] key:[{}] bounded handlers empty.", namespace, key);
            return;
        }

        for (Handler handler : handlers) {
            handleConfigDataChanged(starting, handler, newData);
        }

        key2data.put(key, Optional.ofNullable(newData));
    }

    private void handleConfigDataChanged(boolean starting, Handler handler, String newConfig) {
        if (newConfig == null && !Objects.equals(ConfigBinding.DEFAULT_VALUE, handler.defaultValue)) {
            newConfig = handler.defaultValue;
        }

        String name = handler.name();
        metrics.incCounter("handle_config_changed", name);
        ISpan span = tracer.newSpan("ConfigChanged", name);
        try {
            Object[] deserialize = handler.deserialize(starting, newConfig);
            if (!(boolean) deserialize[0]) {
                span.event("DeserializeConfigFailed", name);
                metrics.incCounter("deserialize_config_failed", name);
                span.setStatus(ISpan.STATUS_FAILED);
                return;
            }
            if (!handler.validate(starting, newConfig, deserialize[1])) {
                span.event("ValidateConfigFailed", name);
                metrics.incCounter("validate_config_failed", name);
                span.setStatus(ISpan.STATUS_FAILED);
                return;
            }
            handler.handle(starting, deserialize[1]);
            span.setStatus(ISpan.STATUS_SUCCESS);
        } catch (ConfigCenterException e) {
            span.event("HandleConfigChangeError", name);
            metrics.incCounter("handle_config_change_error", name);
            span.setStatus(e);
            throw e;
        } finally {
            span.finish();
        }
    }

    private static class Handler {

        private String namespace;

        private String key;

        private String defaultValue;

        private String desc;

        private Class<?> belongs;

        private Field field;

        private Method method;

        private Class<?> type1;

        private Type type2;

        private Deserializer deserializer;

        private Validator validator;

        public Object[] deserialize(boolean starting, String valueStr) {
            try {
                return new Object[]{true, deserializer.deserialize(new Deserializer.Context(key, desc, valueStr, type1, type2, field, method, belongs))};
            } catch (Throwable t) {
                String message = Message.format("MATRIX-CONFIG-0000-0006", namespace, key, valueStr, type1, type2);
                log.error("{}", message, t);
                if (starting) {
                    throw new ConfigCenterException(message, t);
                }
                return new Object[]{false, null};
            }
        }

        public boolean validate(boolean starting, String valueStr, Object valueObj) {
            boolean success;
            try {
                success = validator.validate(new Validator.Context(key, desc, valueStr, valueObj, field, method, belongs));
            } catch (Throwable t) {
                String message = Message.format("MATRIX-CONFIG-0000-0008", namespace, key, valueObj);
                log.error("{}", message, t);
                if (starting) {
                    throw new ConfigCenterException(message, t);
                }
                return false;
            }

            if (!success) {
                String message = Message.format("MATRIX-CONFIG-0000-0007", namespace, key, valueObj);
                log.error("{}", message);
                if (starting) {
                    throw new ConfigCenterException(message);
                }
            }
            return success;
        }

        public void handle(boolean starting, Object valueObj) {
            try {
                if (field != null) {
                    field.set(null, valueObj);
                    log.info("field:[{}.{}] => namespace:[{}] key:[{}] changed to:[{}] success.", belongs.getName(), field.getName(), namespace, key, valueObj);
                }
            } catch (Throwable t) {
                String message = Message.format("MATRIX-CONFIG-0000-0009", belongs.getName(), field.getName(), namespace, key, valueObj);
                log.error("{}", message, t);
                if (starting) {
                    throw new ConfigCenterException(message, t);
                }
            }

            try {
                if (method != null) {
                    method.invoke(null, valueObj);
                    log.info("method:[{}.{}] => namespace:[{}] key:[{}] invoked by:[{}] success.", belongs.getName(), method.getName(), namespace, key, valueObj);
                }
            } catch (Throwable t) {
                String message = Message.format("MATRIX-CONFIG-0000-0010", belongs.getName(), method.getName(), namespace, key, valueObj);
                log.error("{}", message, t);
                if (starting) {
                    throw new ConfigCenterException(message, t);
                }
            }
        }

        public String name() {
            if (field != null) {
                return String.format("field:[%s.%s](%s/%s)", belongs.getName(), field.getName(), namespace, key);
            } else {
                return String.format("method:[%s.%s](%s/%s)", belongs.getName(), method.getName(), namespace, key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T newInstance(String type, Class<T> clazz) {
        return (T) clazz2instance.computeIfAbsent(clazz, _K -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                String message = Message.format("MATRIX-CONFIG-0000-0003", type, clazz.getName());
                log.error("{}", message);
                throw new ConfigCenterException(message, e);
            } catch (IllegalAccessException e) {
                String message = Message.format("MATRIX-CONFIG-0000-0004", type, clazz.getName());
                log.error("{}", message);
                throw new ConfigCenterException(message, e);
            } catch (InvocationTargetException | InstantiationException e) {
                String message = Message.format("MATRIX-CONFIG-0000-0005", type, clazz.getName());
                log.error("{}", message);
                throw new ConfigCenterException(message, e);
            }
        });
    }
}
