package org.evrete.guides.benchmarks;

import org.evrete.guides.benchmarks.jmh.SessionCreatePerformance;
import org.openjdk.jmh.annotations.Mode;

import java.util.concurrent.TimeUnit;

public class BenchmarkSessionCreateTime extends AbstractBenchmarkRunner {
    public static void main(String[] args) {
        runTest(SessionCreatePerformance.class, Mode.AverageTime, TimeUnit.MILLISECONDS);
    }
}
