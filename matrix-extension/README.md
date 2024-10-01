# Extension: 可扩展框架

### Quick Start
#### Step 1. 扩展(Ext)定义
```java
@Extension(desc = "演示Demo扩展定义")
public interface ShowDemoExt {

    Long extPoint1(Long... args);

    Object extPoint2(Object... args);
}
```

#### Step 2. 默认实现
```java
@ExtensionBase
public class ShowDemoExtBaseImpl implements ShowDemoExt {

    @Override
    public Long extPoint1(Long... args) {
        System.out.println("ShowDemoExtBaseImpl#extPoint1, args: " + Arrays.toString(args));
        return 0L;
    }

    @Override
    public Object extPoint2(Object... args) {
        System.out.println("ShowDemoExtBaseImpl#extPoint2, args: " + Arrays.toString(args));
        return null;
    }
}
```

#### Step 3. 具体实现
- 实现A:
```java
@ExtensionImpl(code = "code.a", desc = "code A")
public class ShowDemoExtCodeAImpl implements ShowDemoExt {

    @Override
    public Long extPoint1(Long... args) {
        System.out.println("ShowDemoExtCodeAImpl#extPoint1, args: " + Arrays.toString(args));
        return ThreadLocalRandom.current().nextLong(100L);
    }

    @Override
    public Object extPoint2(Object... args) {
        System.out.println("ShowDemoExtCodeAImpl#extPoint2, args: " + Arrays.toString(args));
        return "A: " + Arrays.toString(args);
    }
}
```
- 实现B:
```java
@ExtensionImpl(code = "code.b", desc = "code B")
public class ShowDemoExtCodeBImpl implements ShowDemoExt {

    @Override
    public Long extPoint1(Long... args) {
        System.out.println("ShowDemoExtCodeBImpl#extPoint1, args: " + Arrays.toString(args));
        return ThreadLocalRandom.current().nextLong(100L, 1000L);
    }

    @Override
    public Object extPoint2(Object... args) {
        System.out.println("ShowDemoExtCodeBImpl#extPoint2, args: " + Arrays.toString(args));
        return "B: " + Arrays.toString(args);
    }
}
```

#### Step 4. 扩展调用
```java
public class Main {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static void main(String[] args) {
        Long result1 = ExtensionInvoker.invoke("code.a", ShowDemoExt.class, ext -> ext.extPoint1(random.nextLong()));
        System.out.println("result1 = " + result1);

        Object result2 = ExtensionInvoker.invoke("code.b", ShowDemoExt.class, ext -> ext.extPoint2(random.nextLong()));
        System.out.println("result2 = " + result2);

        Long result3 = ExtensionInvoker.invoke("code.noneMatch", ShowDemoExt.class, ext -> ext.extPoint1(random.nextLong()));
        System.out.println("result3 = " + result3);
    }
}
```


For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.13/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.13/maven-plugin/reference/html/#build-image)

