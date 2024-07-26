package org.evrete.guides.benchmarks;

import org.evrete.guides.benchmarks.jmh.AnnotatedVsCorePerformance;
import org.openjdk.jmh.annotations.Mode;

import java.util.concurrent.TimeUnit;

public class BenchmarkAnnotatedVsCodeBuildTime extends AbstractBenchmarkRunner {
    public static void main(String[] args) {
        runTest(AnnotatedVsCorePerformance.class, Mode.Throughput, TimeUnit.SECONDS);
    }
}
