package org.evrete.guides.benchmarks.jmh;

import org.evrete.KnowledgeService;
import org.evrete.dsl.annotation.Rule;
import org.evrete.dsl.annotation.RuleSet;
import org.evrete.dsl.annotation.Where;
import org.evrete.guides.classes.Customer;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

public class AnnotatedVsCoreBuildTime {

    @Benchmark
    public void core(BenchmarkData state) {
        state.service.newKnowledge()
                .builder()
                .newRule("Rule 1")
                .forEach("$customer", Customer.class)
                .where("$customer.status > 0")
                .execute(ctx->{})
                .newRule("Rule 2")
                .forEach("$customer", Customer.class)
                .where("$customer.id > 0")
                .execute(ctx->{})
                .build();
    }

    @Benchmark
    public void ajr(BenchmarkData state) throws IOException {
        state.service.newKnowledge()
                .importRules("JAVA-CLASS", AJRRuleSet1.class);
    }

    @State(Scope.Benchmark)
    public static class BenchmarkData {

        KnowledgeService service;

        @Setup(Level.Iteration)
        public void initInvocationData() {
        }

        @Setup(Level.Trial)
        public void initServices() {
            service = new KnowledgeService();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            service.shutdown();
        }
    }


    @RuleSet("Customer Ruleset")
    public static class AJRRuleSet1 {

        @Rule("Rule 1")
        @Where("$customer.status > 0")
        public void rule1(Customer $customer) {

        }
        @Rule("Rule 2")
        @Where("$customer.id > 0")
        public void rule2(Customer $customer) {

        }
    }
}

