package com.alibaba.matrix.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

/**
 * @author feiqing.zjf@gmail.com
 * @version 1.0
 * @since 2025/2/10 19:48.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobConfig implements Serializable {

    private static final long serialVersionUID = -6287184397947482903L;

    ExecutorService executor;

    boolean enableFailFast;
}
