package org.evrete.guides.benchmarks;

import org.evrete.guides.benchmarks.jmh.AnnotatedVsCoreBuildTime;
import org.openjdk.jmh.annotations.Mode;

public class BenchmarkAnnotatedVsCodeBuildTime extends AbstractBenchmarkRunner {
    public static void main(String[] args) {
        runTest(AnnotatedVsCoreBuildTime.class, Mode.Throughput);
    }
}
