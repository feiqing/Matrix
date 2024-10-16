package com.alibaba.matrix.config.test.other;

import com.alibaba.matrix.base.json.provider.FastJsonMapper;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2024/10/29 14:00.
 */
public class JsonTester {

    @Test
    public void test() throws IOException {

        String string = CharStreams.toString(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("matrix-config-test.json"))));

        Map<String, String> map = new FastJsonMapper().fromJson(string, new TypeToken<Map<String, String>>() {
        });

        System.out.println(map);


    }
}
