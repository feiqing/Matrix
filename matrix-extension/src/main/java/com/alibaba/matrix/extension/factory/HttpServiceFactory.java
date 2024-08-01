package com.alibaba.matrix.extension.factory;

import com.alibaba.matrix.extension.model.Http;

import static com.alibaba.matrix.extension.utils.Logger.log;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * warning: 仅作为Demo演示http类型扩展实现支持, 没做任何的优化和异常处理, 千万不要应用到实际业务当中
 *
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2024/7/25 10:39.
 */
public class HttpServiceFactory {

    public static Object getHttpService(Class<?> ext, Http http) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(ext.getClassLoader());
        enhancer.setInterfaces(new Class[]{ext});
        enhancer.setCallback(new HttpImplAdaptor(http));
        return enhancer.create();
    }

    private static class HttpImplAdaptor implements MethodInterceptor {

        private final RestTemplate restTemplate;

        private final Http http;

        public HttpImplAdaptor(Http http) {
            this.http = http;
            this.restTemplate = new RestTemplate(); // 扩展参数
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            if (ReflectionUtils.isObjectMethod(method)) {
                return method.invoke(this, objects);
            }

            Class<?> resultType = method.getReturnType();
            Parameter[] parameters = method.getParameters();

            List<String> argParam = new LinkedList<>();
            Map<String, Object> argObj = new LinkedHashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                String name = parameters[i].getName();
                argParam.add(String.format("%s={%s}", name, name));
                argObj.put(name, objects[i]);
            }

            String url;
            ResponseEntity<?> responseEntity;
            if (StringUtils.equalsIgnoreCase(http.method, "GET")) {
                url = http.url + getMethodPath(method.getName(), http.camelToUnderline) + "?" + StringUtils.join(argParam, "&");
                responseEntity = restTemplate.getForEntity(url, resultType, argObj);
            } else {
                url = http.url + getMethodPath(method.getName(), http.camelToUnderline);
                responseEntity = restTemplate.postForEntity(url, argObj, resultType);
            }

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                String headers = responseEntity.getHeaders().entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(", "));
                log.error("request url:{} error, method:{}, param:{}, error:{}, headers:{}", url, http.method, argObj, responseEntity.getStatusCodeValue(), headers);
                return null;
            }
        }

        private String getMethodPath(String methodName, boolean camelToUnderline) {
            return camelToUnderline ? camelToUnderline(methodName) : methodName;
        }

        private String camelToUnderline(String str) {
            if (StringUtils.isEmpty(str)) {
                return str;
            }

            str = String.valueOf(str.charAt(0)).toUpperCase().concat(str.substring(1));
            Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
            Matcher matcher = pattern.matcher(str);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String word = matcher.group();
                sb.append(word.toLowerCase());
                sb.append(matcher.end() == str.length() ? "" : "_");
            }

            return sb.toString();
        }
    }
}
