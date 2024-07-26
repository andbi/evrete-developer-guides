package org.evrete.guides.benchmarks.jmh;

import org.evrete.KnowledgeService;
import org.evrete.api.IntToValue;
import org.evrete.api.Knowledge;
import org.evrete.api.StatefulSession;
import org.evrete.api.ValuesPredicate;
import org.evrete.api.builders.RuleSetBuilder;
import org.evrete.dsl.annotation.FieldDeclaration;
import org.evrete.dsl.annotation.Rule;
import org.evrete.dsl.annotation.RuleSet;
import org.evrete.dsl.annotation.Where;
import org.evrete.guides.classes.Customer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionCreatePerformance {


    @Benchmark
    public void benchmark(BenchmarkData state) {
        try(StatefulSession session = state.knowledge.newStatefulSession()) {
            // Don't let the JIT optimize away the session
            int hash = System.identityHashCode(session);
            Blackhole.consumeCPU(hash > 0 ? 1 : 2);
        }
    }


    @State(Scope.Benchmark)
    public static class BenchmarkData {

        @Param({"2", "4", "8", "16", "32", "64", "128", "256", "512", "1024"})
        int ruleCount;

        KnowledgeService service;
        Knowledge knowledge;


        @Setup(Level.Iteration)
        public void initInvocationData() {
        }

        @Setup(Level.Trial)
        public void initServices() throws IOException {
            service = new KnowledgeService();
            knowledge = buildKnowledge(service, ruleCount);
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            service.shutdown();
        }
    }

    private static Knowledge buildKnowledge(KnowledgeService service, int ruleCount) {
        Knowledge knowledge = service.newKnowledge();

        RuleSetBuilder<Knowledge> builder =  knowledge.builder();

        for (int i = 0; i < ruleCount; i++) {
            // Creating a simple dummy rule with two alpha- and one beta-conditions
            builder.newRule("Rule " + i)
                    .forEach("$c1", Customer.class, "$c2", Customer.class)
                    .where(new DummyPredicate(), "$c1.status")
                    .where(new DummyPredicate(), "$c2.name")
                    .where(new DummyPredicate(), "$c1.id", "$c2.id")
            ;
        }

        return builder.build();
    }

    private static class DummyPredicate implements ValuesPredicate  {
        @Override
        public boolean test(IntToValue t) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
