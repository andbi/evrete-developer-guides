package org.evrete.guides.benchmarks;

import org.evrete.guides.benchmarks.jmh.LiteralVsPredicateFirePerformance;
import org.openjdk.jmh.annotations.Mode;

import java.util.concurrent.TimeUnit;

public class BenchmarkLiteralVsPredicateBuildTime extends AbstractBenchmarkRunner {
    public static void main(String[] args) {
        runTest(LiteralVsPredicateFirePerformance.class, Mode.Throughput, TimeUnit.SECONDS);
    }
}
