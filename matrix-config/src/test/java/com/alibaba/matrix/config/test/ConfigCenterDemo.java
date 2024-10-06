package com.alibaba.matrix.config.test;

import com.alibaba.matrix.config.ConfigBinding;
import com.alibaba.matrix.config.ConfigCenter;
import com.alibaba.matrix.config.deserializer.Deserializer;
import com.alibaba.matrix.config.validator.Validator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;

/**
 * @author feiqing.zjf@gmail.com (FeiQing)
 * @version 1.0
 * @since 2023/6/24 16:10.
 */
@ConfigCenter(namespace = "DEFAULT_GROUP")
public class ConfigCenterDemo {

    /**
     * 属性映射
     */
    // 基础类型
    @ConfigBinding(key = "bool.value", desc = "${config.key.desc}")
    public static boolean boolRawValue;

    @ConfigBinding(key = "bool.value", desc = "${config.key.desc}")
    public static Boolean boolValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static byte byteRawValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static Byte byteValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static short shortRawValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static Short shortValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static int intRawValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static Integer intValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static long longRawValue;

    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static Long longValue;

    @ConfigBinding(key = "double.value", desc = "${config.key.desc}")
    public static float floatRawValue;

    @ConfigBinding(key = "double.value", desc = "${config.key.desc}")
    public static Float floatValue;

    @ConfigBinding(key = "double.value", desc = "${config.key.desc}")
    public static double doubleRawValue;

    @ConfigBinding(key = "double.value", desc = "${config.key.desc}")
    public static Double doubleValue;

    @ConfigBinding(key = "char.value", desc = "${config.key.desc}")
    public static char charRawValue;

    @ConfigBinding(key = "char.value", desc = "${config.key.desc}")
    public static Character charValue;

    @ConfigBinding(key = "string.value", desc = "${config.key.desc}")
    public static String strValue;

    @ConfigBinding(key = "enum.value", desc = "${config.key.desc}")
    public static AppEnum enumValue;

    // 集合类型
    @ConfigBinding(key = "int.list", desc = "${config.key.desc}")
    public static List<Integer> intList;

    @ConfigBinding(key = "int.list", desc = "${config.key.desc}")
    public static List<Long> longList;

    @ConfigBinding(key = "string.list", desc = "${config.key.desc}")
    public static List<String> stringList;

    @ConfigBinding(key = "string.list", desc = "${config.key.desc}")
    public static ArrayList<String> stringArrayList;

    @ConfigBinding(key = "int.list", desc = "${config.key.desc}")
    public static Set<Integer> intSet;

    @ConfigBinding(key = "int.list", desc = "${config.key.desc}")
    public static Set<Long> longSet;

    @ConfigBinding(key = "string.list", desc = "${config.key.desc}")
    public static Set<String> stringSet;

    @ConfigBinding(key = "int2int.map", desc = "${config.key.desc}")
    public static Map<String, Integer> str2intMap;

    @ConfigBinding(key = "int2int.map", desc = "${config.key.desc}")
    public static Map<Integer, Integer> int2intMap;

    @ConfigBinding(key = "int2int.map", desc = "${config.key.desc}")
    public static ConcurrentHashMap<Integer, Long> int2longConcurrentHashMap;

    @ConfigBinding(key = "int.2.int.list.map", desc = "${config.key.desc}")
    public static Map<String, List<Integer>> str2intListMap;

    @ConfigBinding(key = "int.2.int.list.map", desc = "${config.key.desc}")
    public static Map<Integer, Set<Long>> int2longSetMap;

    // 自定义类型
    @ConfigBinding(key = "custom.obj", desc = "${config.key.desc}")
    public static CustomObj customObj;

    @ConfigBinding(key = "custom.obj.list", desc = "${config.key.desc}")
    public static List<CustomObj> customObjList;

    @ConfigBinding(key = "custom.obj.list", desc = "${config.key.desc}")
    public static Set<CustomObj> customObjSet;

    @ConfigBinding(key = "custom.obj.list", desc = "${config.key.desc}")
    public static Collection<CustomObj> customObjCollection;

    @ConfigBinding(key = "custom.obj.map", desc = "${config.key.desc}")
    public static Map<String, CustomObj> str2customObjMap;

    @ConfigBinding(key = "custom.obj.map", desc = "${config.key.desc}")
    public static Map<Integer, CustomObj> int2customeObjMap;

    @ConfigBinding(key = "int.2.custom.obj.list.map", desc = "${config.key.desc}")
    public static Map<String, List<CustomObj>> str2customObjListMap;

    @ConfigBinding(key = "int.2.custom.obj.list.map", desc = "${config.key.desc}")
    public static Map<Integer, Set<CustomObj>> int2customeObjSetMap;

    /**
     * 自定义反序列化
     */
    @ConfigBinding(key = "string.list", desc = "${config.key.desc}", deserializer = ImmutableListDeserializer.class)
    public static ImmutableList<String> strImmutableList;

    @ConfigBinding(key = "int2int.map", desc = "${config.key.desc}", deserializer = ImmutableMapDeserializer.class)
    public static ImmutableMap<String, Integer> str2intImmutableMap;

    /**
     * 自定义业务验证
     */
    @ConfigBinding(key = "str2int.custom.validator.map", desc = "${config.key.desc}", validator = CustomValidator.class)
    public static Map<String, Integer> customValidatorMap;

    /**
     * 方法回调: long类型演示, 其他类型与属性映射相同
     */
    @ConfigBinding(key = "number.value", desc = "${config.key.desc}")
    public static void onLongRawValueCallback(long longRawValue) {
        System.out.println("longRawValue:" + longRawValue);
    }

    @Data
    public static class CustomObj {

        private long id;

        private String value;
    }

    public static class ImmutableListDeserializer implements Deserializer {

        @Override
        public Object deserialize(Context context) {
            List<String> list = jsonMapper.fromJson(context.getValueStr(), new TypeToken<List<String>>() {
            });
            return ImmutableList.copyOf(list);
        }
    }

    public static class ImmutableMapDeserializer implements Deserializer {

        @Override
        public Object deserialize(Context context) {
            Map<String, Integer> map = jsonMapper.fromJson(context.getValueStr(), new TypeToken<Map<String, Integer>>() {
            });
            return ImmutableMap.copyOf(map);
        }
    }

    public static class CustomValidator implements Validator {

        @Override
        public boolean validate(Context context) {
            Map<String, Integer> map = context.getValueObj();
            if (!map.containsKey("matrix_is_good")) {
                return false;
            }

            return Objects.equals(map.get("matrix_is_good"), 18) && Objects.equals(map.get("matrix_is_bad"), -1);
        }
    }

    @Getter
    public enum AppEnum {
        MATRIX_BASE(0, "Base"),
        MATRIX_JOB(1, "Job"),
        MATRIX_FLOW(2, "Flow"),
        MATRIX_CONFIG(3, "Config"),
        MATRIX_EXTENSION(4, "Extension"),
        ;

        private final int code;

        private final String desc;

        AppEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
