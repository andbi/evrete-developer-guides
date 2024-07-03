package org.evrete.guides.benchmarks;

import org.evrete.guides.benchmarks.jmh.LiteralVsPredicateBuildTime;
import org.openjdk.jmh.annotations.Mode;

public class BenchmarkLiteralVsPredicateBuildTime extends AbstractBenchmarkRunner {
    public static void main(String[] args) {
        runTest(LiteralVsPredicateBuildTime.class, Mode.Throughput);
    }
}
