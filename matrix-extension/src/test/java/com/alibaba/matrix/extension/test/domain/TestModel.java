package com.alibaba.matrix.extension.test.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2024/7/25 22:37.
 */
@Data
public class TestModel {

    public String name;

    public List<Integer> list;

    public Map<String, Object> map;
}
