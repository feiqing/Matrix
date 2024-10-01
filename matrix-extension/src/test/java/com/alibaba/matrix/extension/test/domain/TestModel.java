package com.alibaba.matrix.extension.test.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author jifang.zjf@alibaba-inc.com
 * @version 1.0
 * @since 2023/7/25 22:37.
 */
@Data
public class TestModel implements Serializable {

    private static final long serialVersionUID = 370071340368632352L;
    private String id;
    
    public String name;

    public List<Integer> list;

    public Map<String, Object> map;
}
