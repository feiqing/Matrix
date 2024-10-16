package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.config.Guice;
import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Slf4j
public class GuiceInstanceFactory {

    private static Injector guiceInjector;

    public static void setGuiceInjector(Injector guiceInjector) {
        GuiceInstanceFactory.guiceInjector = guiceInjector;
    }

    public static Object getGuiceInstance(Guice guice) throws ClassNotFoundException {
        Preconditions.checkState(guiceInjector != null);
        Object instance;
        if (StringUtils.isBlank(guice.name)) {
            instance = guiceInjector.getInstance(Key.get(Class.forName(guice.clazz)));
        } else {
            instance = guiceInjector.getInstance(Key.get(Class.forName(guice.clazz), Names.named(guice.name)));
        }

        Preconditions.checkState(instance != null, String.format("Guice instance get failed, guice class:[%s] name:[%s].", guice.clazz, guice.name));
        log.info("Guice instance get success, guice class:[{}] name:[{}].", guice.clazz, guice.name);
        return instance;
    }
}
