package com.alibaba.matrix.testing.other;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.alibaba.matrix.base.json.JsonMapperProvider.jsonMapper;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2023/9/22 22:35.
 */
// @RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:spring-*.xml")
public class Tester {

    @Test
    public void tt() throws CannotCompileException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ClassPool pool = ClassPool.getDefault();

        CtClass cc = pool.makeClass("com.example.DynamicGeneratedClass");

        cc.addField(CtField.make("public int hh = 18;", cc));
        cc.addField(CtField.make("public int xx = 29;", cc));
        CtMethod method = CtNewMethod.make("public void sayHello() { System.out.println(\"Hello from DynamicGeneratedClass!\"); }", cc);
        cc.addMethod(method);

        // 将生成的类写入文件（可选）
        // cc.writeFile("/path/to/output/directory");

        // 加载并实例化生成的类（注意：这里的类加载器必须是能够访问生成类的类加载器）
        Class<?> generatedClass = cc.toClass(); // 使用默认类加载器加载类（可能不适合所有情况）
        Object instance = generatedClass.newInstance();

        // 调用生成类的方法（需要反射）
        java.lang.reflect.Method sayHelloMethod = generatedClass.getDeclaredMethod("sayHello");
        sayHelloMethod.invoke(instance); // 输出 "Hello from DynamicGeneratedClass!"

        System.out.println(jsonMapper.toJson(instance));
    }

    @Test
    public void tt2() throws NotFoundException, CannotCompileException {
//        new CustomObj().sayHi();
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        CtClass ctClass = classPool.get("com.alibaba.matrix.testing.other.CustomObj");
        CtMethod ctMethod = ctClass.getDeclaredMethod("sayHi");
        ctMethod.setBody("System.out.println(\"hello nishi hi\");");
        ctClass.toClass();
        ctClass.detach();
        new CustomObj().sayHi();
    }
}
