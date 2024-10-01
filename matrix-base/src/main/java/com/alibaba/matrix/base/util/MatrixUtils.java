package com.alibaba.matrix.base.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;

/**
 * @author feiqing.zjf@gmail.com
 * @version 3.0
 * @since 2016/8/18 下午6:09.
 */
@Slf4j
@SuppressWarnings("unchecked")
public class MatrixUtils {

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static String localIp = "unknown";

    private static String localMac = "unknown";

    private static Map<Type, Method> primitiveMethods;

    static {
        initPrimitiveMethods();
        initLocalIp();
        initLocalMac();
    }

    public static <T> T str2obj(String str, TypeToken<T> typeToken) {
        return str2obj(str, typeToken.getType());
    }

    public static <T> T str2obj(String str, Class<T> clazz) {
        return str2obj(str, (Type) clazz);
    }

    @SneakyThrows
    public static <T> T str2obj(String str, Type type) {
        Object valueObj;
        Method primitive = primitiveMethods.get(type);
        if (primitive != null) {
            valueObj = StringUtils.isEmpty(str) ? null : primitive.invoke(null, str);
        } else if (type == Character.class || type == char.class) {
            valueObj = StringUtils.isEmpty(str) ? null : str.charAt(0);
        } else if (type == String.class) {
            valueObj = str;
        } else {
            valueObj = StringUtils.isEmpty(str) ? null : jsonMapper.fromJson(str, type);
        }
        return (T) valueObj;
    }

    public static String getLocalIp() {
        return localIp;
    }

    public static String getLocalMac() {
        return localMac;
    }

    private static void initPrimitiveMethods() {
        try {
            Map<Class<?>, Method> methods = new HashMap<>();
            methods.put(byte.class, Byte.class.getMethod("parseByte", String.class));
            methods.put(Byte.class, Byte.class.getMethod("valueOf", String.class));
            methods.put(short.class, Short.class.getMethod("parseShort", String.class));
            methods.put(Short.class, Short.class.getMethod("valueOf", String.class));
            methods.put(int.class, Integer.class.getMethod("parseInt", String.class));
            methods.put(Integer.class, Integer.class.getMethod("valueOf", String.class));
            methods.put(long.class, Long.class.getMethod("parseLong", String.class));
            methods.put(Long.class, Long.class.getMethod("valueOf", String.class));
            methods.put(float.class, Float.class.getMethod("parseFloat", String.class));
            methods.put(Float.class, Float.class.getMethod("valueOf", String.class));
            methods.put(double.class, Double.class.getMethod("parseDouble", String.class));
            methods.put(Double.class, Double.class.getMethod("valueOf", String.class));
            methods.put(boolean.class, Boolean.class.getMethod("parseBoolean", String.class));
            methods.put(Boolean.class, Boolean.class.getMethod("valueOf", String.class));
            primitiveMethods = ImmutableMap.copyOf(methods);
        } catch (NoSuchMethodException ignored) {
        }
    }

    private static void initLocalIp() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                localIp = localAddress.getHostAddress();
            } else {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                if (interfaces != null) {
                    while (interfaces.hasMoreElements()) {
                        try {
                            Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        localIp = address.getHostAddress();
                                    }
                                } catch (Throwable e) {
                                    log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
                                }
                            }
                        } catch (Throwable e) {
                            log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.error("get local ip addr error.", t);
        }
    }

    private static void initLocalMac() {
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = netInterfaces.nextElement();

                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress == null) {
                    continue;
                }

                if (!isValidIp(networkInterface)) {
                    continue;
                }

                localMac = getMacFromBytes(hardwareAddress);
            }
        } catch (Throwable e) {
            log.error("get local mac addr error.", e);
        }
    }


    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && IP_PATTERN.matcher(name).matches());
    }

    private static String getMacFromBytes(byte[] macBytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < macBytes.length; i++) {
            if (i != 0) {
                sb.append(":");
            }

            String str = Integer.toHexString(macBytes[i] & 0xff);
            if (str.length() == 1) {
                sb.append("0");
            }

            sb.append(str);
        }

        return sb.toString();
    }

    private static boolean isValidIp(NetworkInterface networkInterface) {
        Enumeration<InetAddress> ips = networkInterface.getInetAddresses();
        while (ips.hasMoreElements()) {
            InetAddress ip = ips.nextElement();
            if (/*ip instanceof Inet4Address && */!ip.isLoopbackAddress()) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println("ip: " + getLocalIp());
        System.out.println("mac: " + getLocalMac());
    }
}
