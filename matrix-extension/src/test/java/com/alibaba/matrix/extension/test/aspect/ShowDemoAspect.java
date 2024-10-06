package com.alibaba.matrix.extension.test.aspect;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2023/8/24 16:49.
 */
public class ShowDemoAspect {

    public Object around(ProceedingJoinPoint pjp) {
        try {
            // Logger.log.info("前置通知: {}.", ((MethodSignature) pjp.getSignature()).getMethod().getName());
            Object returnValue = pjp.proceed();
            // Logger.log.info("后置通知: {}.", ((MethodSignature) pjp.getSignature()).getMethod().getName());
            return returnValue;
        } catch (Throwable t) {
            // Logger.log.info("异常通知: {}", ((MethodSignature) pjp.getSignature()).getMethod().getName(), t);
            return ExceptionUtils.rethrow(t);
        } finally {
            // Logger.log.info("最终通知: {}.", ((MethodSignature) pjp.getSignature()).getMethod().getName());
        }
    }
}
