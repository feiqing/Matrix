# Matrix-Config: 可配置框架

## 快速上手

- 配置

```java
import com.alibaba.matrix.config.ConfigCenter;
import com.alibaba.matrix.config.ConfigBinding;

@ConfigCenter(namespace = "DEFAULT_GROUP", desc = "ConfigCenterDemo")
public class ConfigCenterDemo {

    @ConfigBinding(key = "demo.config.value", desc = "${config.desc}")
    public static volatile String demoConfigValue;
}
```

- 引用

```java
@Test
public void test_config_reference() {
  System.out.println(ConfigCenterDemo.demoConfigValue);
}
```

与普通静态变量相同, 无需初始化, 可以直接引用.

- 启动

```java
@Before
public void setUp() {
  ConfigFrameworkRegister register = new ConfigFrameworkRegister(Collections.singletonList("com.alibaba.matrix.config.test"));
  register.init();
}
```

指定包扫描路径, 启动配置框架.

## 特性

- 默认类型支持
    - 8种Java基础类型: `int`, `long`, `float`, `double`, `boolean`, `char`, `byte`, `short`
    - 以及对应的包装类型: `Integer`, `Long`, `Float`, `Double`, `Boolean`, `Character`, `Byte`, `Short`
    - 以及对应的数组类型: `int[]`, `long[]`, `float[]`, `double[]`, `boolean[]`, `char[]`, `byte[]`, `short[]`
    - String: `String`, `String[]`
    - List: `List<String>`, `List<Integer>`
    - Map: `Map<String, String>`, `Map<String, Integer>`
    - Set: `Set<String>`, `Set<Integer>`
    - Array: `String[]`, `Integer[]`
    - Enum: `Enum`, `Enum[]`
    - 自定义类型: `CustomType`, `CustomType[]`, `List<CustomType>`, `Set<CustomType>`, `Map<String, CustomType>`
    -
    具体实现参考: `com.alibaba.matrix.config.deserializer.DefaultDeserializer`(`com.alibaba.matrix.base.util.MatrixUtils#str2obj`)
- 配置中心SPI
- 默认值
- Namespace
- Method Callback

## 扩展

- Serializer

- Validator
