package com.alibaba.matrix.flow.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2022/5/31 16:23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateContext implements Serializable {

    private static final long serialVersionUID = -3284338197634811827L;

    private long itemId;

}
