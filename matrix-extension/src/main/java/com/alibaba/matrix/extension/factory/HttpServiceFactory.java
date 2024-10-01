package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.Http;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.matrix.extension.util.Logger.log;


/**
 * warning: 仅作为Demo演示http类型扩展实现支持, 没做任何的优化和异常处理, 千万不要应用到实际业务当中
 *
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2023/7/25 10:39.
 */
public class HttpServiceFactory {

    public static Object getHttpService(Class<?> ext, Http http) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(ext.getClassLoader());
        enhancer.setInterfaces(new Class[]{ext});
        enhancer.setCallback(new HttpServiceAdaptor(http));
        return enhancer.create();
    }

    private static class HttpServiceAdaptor implements MethodInterceptor {

        private final RestTemplate restTemplate;

        private final Http http;

        public HttpServiceAdaptor(Http http) {
            this.http = http;
            this.restTemplate = new RestTemplate(); // 扩展参数
        }

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (ReflectionUtils.isObjectMethod(method)) {
                return method.invoke(this, args);
            }

            Class<?> resultType = method.getReturnType();
            Parameter[] parameters = method.getParameters();

            String path = StringUtils.endsWith(http.path, "/") ? http.path + method.getName() : http.path + "/" + method.getName();

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().scheme(http.schema).host(http.host).port(http.port).path(path);
            Map<String, Object> requestBody = new LinkedHashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                String argName = parameters[i].getName();
                Object argValue = args[i];
                if (HttpMethod.GET == HttpMethod.resolve(http.method)) {
                    uriBuilder.queryParam(argName, argValue);
                } else if (HttpMethod.POST == HttpMethod.resolve(http.method)) {
                    requestBody.put(argName, argValue);
                }
            }

            String uri = uriBuilder.encode().toUriString();
            log.info("query uri = {}", uri);

            ResponseEntity<?> responseEntity;
            if (HttpMethod.GET == HttpMethod.resolve(http.method)) {
                responseEntity = restTemplate.getForEntity(uri, resultType);
            } else if (HttpMethod.POST == HttpMethod.resolve(http.method)) {
                responseEntity = restTemplate.postForEntity(uri, requestBody, resultType);
            } else {
                throw new RuntimeException("un support http method");
            }

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                String headers = responseEntity.getHeaders().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(", "));
                log.error("request uri:{} error, method:{}, param:{}, error:{}, headers:{}", uri, http.method, requestBody, responseEntity.getStatusCodeValue(), headers);
                return null;
            }
        }
    }
}
