package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.config.Hsf;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/3/30 10:31.
 */
@Slf4j
public class HsfServiceFactory {

    public static Object getHsfService(Hsf hsf) {
        // 后面如果有需要分布式传递ExtensionContext的话
        // 就使用: RPCContext rpcContext = RPCContext.getClientContext();
        // 详见: http://mw.alibaba-inc.com/products/hsf/_book/mw-docs/hsf-manuel-book/chapter14.html
        // 要在此处生成一个Hsf服务的Proxy 还是 说使用hsf filter?

        HSFApiConsumerBean hsfConsumerBean = new HSFApiConsumerBean();
        hsfConsumerBean.setInterfaceName(hsf.service);
        hsfConsumerBean.setVersion(hsf.version);
        hsfConsumerBean.setGroup(hsf.group);
        if (hsf.timeout != null) {
            hsfConsumerBean.setClientTimeout(hsf.timeout);
        }
        hsfConsumerBean.init(true);

        Object serviceObj = hsfConsumerBean.getObject();

        Preconditions.checkState(serviceObj != null, String.format("Hsf service:[%s] version:[%s] group:[%s] init failed.", hsf.service, hsf.version, hsf.group));
        log.info("Hsf service:[{}] version:[{}] group:[{}] init success.", hsf.service, hsf.version, hsf.group);

        return serviceObj;
    }

    private static class HSFApiConsumerBean {

        public void setInterfaceName(String service) {

        }

        public void setVersion(String version) {

        }

        public void setGroup(String group) {

        }

        public void setClientTimeout(Integer timeout) {

        }

        public void init(boolean b) {

        }

        public Object getObject() {
            return null;
        }
    }
}
