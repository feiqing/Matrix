package com.alibaba.matrix.extension.factory;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.matrix.extension.exception.ExtensionException;
import com.alibaba.matrix.extension.model.Dubbo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.alibaba.matrix.extension.utils.Logger.log;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/30 13:13.
 */
public class DubboServiceFactory {

    private static final ConcurrentMap<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static Object getDubboService(Class<?> ext, Dubbo dubbo) {
        // tips: 在这里就不管它是否为lazy了, 一定要加载了
        String serviceKey = String.format("%s#%s#%s#%s", ext.getName(), dubbo.group, dubbo.version, dubbo.timeout);
        Object serviceObj = serviceMap.get(serviceKey);
        if (serviceObj != null) {
            return serviceObj;
        }

        // 没有使用serviceMap.computeIfAbsent()是因为想要把dubbo初始化过程中的Exception原封不动的抛出去
        return _getDubboService(serviceKey, ext, dubbo);
    }

    private static synchronized Object _getDubboService(String serviceKey, Class<?> ext, Dubbo dubbo) {
        Object serviceObj = serviceMap.get(serviceKey);
        if (serviceObj != null) {
            return serviceObj;
        }

        ReferenceConfig<Object> reference = new ReferenceConfig<>();
        reference.setInterface(ext);
        if (dubbo.version != null) {
            reference.setVersion(dubbo.version);
        }
        if (dubbo.group != null) {
            reference.setGroup(dubbo.group);
        }

        if (dubbo.timeout != null) {
            reference.setTimeout(dubbo.timeout);
        }
        if (dubbo.check != null) {
            reference.setCheck(dubbo.check);
        }
        if (dubbo.filter != null) {
            reference.setFilter(dubbo.filter);
        }

        serviceObj = reference.get();
        if (serviceObj == null) {
            throw new ExtensionException(String.format("DubboService:[%s:%s:%s] init failed.", ext.getName(), dubbo.group, dubbo.version));
        } else {
            log.info("DubboService:[{}:{}:{}] init success.", ext, dubbo.group, dubbo.version);
        }

        serviceMap.put(serviceKey, serviceObj);

        return serviceObj;
    }


//    public static void main(String[] args) throws ClassNotFoundException {
//        // 当前应用配置
//        ApplicationConfig application = new ApplicationConfig();
//        application.setName("demo-consumer");
//
//        // 连接注册中心配置
//        RegistryConfig registry = new RegistryConfig();
//        registry.setAddress("zookeeper://10.20.130.230:2181");
//
//        // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
//        // 引用远程服务
//        ReferenceConfig<Object> reference = new ReferenceConfig<>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
//        reference.setApplication(application);
//
//        reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
//        reference.setInterface(Class.forName("xxx"));
//        reference.setVersion("1.0.0");
//
//        // 和本地bean一样使用demoService
//        // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
//        Object service = reference.get();
//
//    }
}
