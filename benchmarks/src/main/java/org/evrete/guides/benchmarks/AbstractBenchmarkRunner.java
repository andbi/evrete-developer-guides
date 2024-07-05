package org.evrete.guides.benchmarks;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public abstract class AbstractBenchmarkRunner {


    protected static void runTest(Class<?> benchmark, Mode mode) {
        try {
            TimeValue duration = TimeValue.milliseconds(1000L);
            int iterations = 10;
            Options opt = new OptionsBuilder()
                    .include(benchmark.getSimpleName())
                    .warmupIterations(iterations / 2)
                    .warmupTime(duration)
                    .measurementIterations(iterations)
                    .measurementTime(duration)
                    .mode(mode)
                    //.timeUnit(TimeUnit.SECONDS)
                    .forks(1)
                    .build();

            new Runner(opt).run();
        } catch (RunnerException e) {
            throw new RuntimeException(e);
        }
    }
}
