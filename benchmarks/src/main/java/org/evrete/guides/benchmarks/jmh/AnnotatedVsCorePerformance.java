package org.evrete.guides.benchmarks.jmh;

import org.evrete.KnowledgeService;
import org.evrete.api.ValuesPredicate;
import org.evrete.dsl.annotation.*;
import org.evrete.guides.classes.Customer;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

public class AnnotatedVsCorePerformance {

    @Benchmark
    public void core(BenchmarkData state) {
        KnowledgeService service = state.service;

        service.newKnowledge()
                .builder()
                .newRule("Rule 1")
                .forEach("$customer", Customer.class)
                .where("$customer.status > 0")
                .execute(ctx->{})
                .build();
    }

    @Benchmark
    public void coreFunctional(BenchmarkData state) {
        KnowledgeService service = state.service;

        service.newKnowledge()
                .builder()
                .newRule("Rule 1")
                .forEach("$customer", Customer.class)
                .where((ValuesPredicate) t -> t.get(0, int.class) > 0, "$customer.status")
                .execute(ctx->{})
                .build();
    }

    @Benchmark
    public void annotated(BenchmarkData state) throws IOException {
        state.service.newKnowledge()
                .importRules("JAVA-CLASS", AJRRuleSet.class);
    }

    @Benchmark
    public void annotatedFunctional(BenchmarkData state) throws IOException {
        state.service.newKnowledge()
                .importRules("JAVA-CLASS", AJRRuleSetFunctional.class);
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


    @RuleSet("Annotated Ruleset")
    public static class AJRRuleSet {

        @Rule("Rule 1")
        @Where("$customer.status > 0")
        public void rule1(Customer $customer) {
            //
        }
    }

    @RuleSet("Annotated Ruleset Functional")
    public static class AJRRuleSetFunctional {

        @Rule("Rule 1")
        @Where(methods = {
                @MethodPredicate(method = "statusIsPositive", args = {"$customer.status"})
        })
        public void rule1(Customer $customer) {
            //
        }

        @PredicateImplementation
        public boolean statusIsPositive(int status) {
            return status > 0;
        }
    }
}

