package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.Hsf;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/11 20:41.
 */
public class HsfServiceFactory {

    private static final ConcurrentMap<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static Object getHsfService(Hsf hsf) {
        // tips: 在这里就不管它是否为lazy了, 一定要加载了

        String serviceKey = String.format("%s#%s#%s#%s", hsf.service, hsf.group, hsf.version, hsf.timeout);
        Object serviceObj = serviceMap.get(serviceKey);
        if (serviceObj != null) {
            return serviceObj;
        }

        // 没有使用serviceMap.computeIfAbsent()是因为想要把hsf初始化过程中的Exception原封不动的抛出去
        return _getHsfService(serviceKey, hsf);
    }

    private static synchronized Object _getHsfService(String serviceKey, Hsf hsf) {
        Object serviceObj = serviceMap.get(serviceKey);
        if (serviceObj != null) {
            return serviceObj;
        }

        // tips:
        // 后面如果有需要分布式传递ExtensionContext的话
        // 就使用: RPCContext rpcContext = RPCContext.getClientContext();
        // 详见: http://mw.alibaba-inc.com/products/hsf/_book/mw-docs/hsf-manuel-book/chapter14.html
        // 要在此处生成一个Hsf服务的Proxy 还是 说使用hsf filter?

//        HSFApiConsumerBean hsfConsumerBean = new HSFApiConsumerBean();
//        hsfConsumerBean.setInterfaceName(hsf.service);
//        hsfConsumerBean.setVersion(hsf.version);
//        hsfConsumerBean.setGroup(hsf.group);
//        if (hsf.timeout != null) {
//            hsfConsumerBean.setClientTimeout(hsf.timeout);
//        }
//        hsfConsumerBean.init(true);
//
//        serviceObj = hsfConsumerBean.getObject();
//        if (serviceObj == null) {
//            throw new ExtensionException(String.format("HsfService:[%s:%s:%s] init failed.", hsf.service, hsf.group, hsf.version));
//        } else {
//            log.info("HsfService:[{}:{}:{}] init success.", hsf.service, hsf.group, hsf.version);
//        }
//
//        serviceMap.put(serviceKey, serviceObj);


        return serviceObj;
    }
}
