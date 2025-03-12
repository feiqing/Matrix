package com.alibaba.matrix.extension.test.o;

import com.alibaba.matrix.base.util.T;
import org.HdrHistogram.Histogram;
import org.LatencyUtils.LatencyStats;

/**
 * @author <a href="mailto:feiqing.zjf@gmail.com">feiqing.zjf</a>
 * @version 1.0
 * @since 2024/10/7 10:53.
 */
public class A {

    public static void main(String[] args) throws InterruptedException {
        LatencyStats myOpStats = new LatencyStats();
        long startTime = System.nanoTime();
        Thread.sleep(T.OneS);
        // Perform operation:
        // doMyOperation(...);
        // Record operation latency:
        myOpStats.recordLatency(System.nanoTime() - startTime);

        // Later, report on stats collected:
        Histogram intervalHistogram = myOpStats.getIntervalHistogram();

        intervalHistogram.outputPercentileDistribution(System.out, 1000000.0);
    }
}
