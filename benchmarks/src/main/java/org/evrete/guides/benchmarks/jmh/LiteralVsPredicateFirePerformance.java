package org.evrete.guides.benchmarks.jmh;

import org.evrete.KnowledgeService;
import org.evrete.api.Knowledge;
import org.evrete.api.ValuesPredicate;
import org.evrete.dsl.annotation.FieldDeclaration;
import org.evrete.dsl.annotation.Rule;
import org.evrete.dsl.annotation.RuleSet;
import org.evrete.dsl.annotation.Where;
import org.evrete.guides.classes.Customer;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiteralVsPredicateFirePerformance {


    @Benchmark
    public void literal(BenchmarkData state) {
        state.longLiteralKnowledge.newStatelessSession().insert(state.facts).fire();
    }

    @Benchmark
    public void customField(BenchmarkData state) {
        state.customFieldLiteralKnowledge.newStatelessSession().insert(state.facts).fire();
    }

    @Benchmark
    public void fastest(BenchmarkData state) {
        state.fastestKnowledge.newStatelessSession().insert(state.facts).fire();
    }

    @State(Scope.Benchmark)
    public static class BenchmarkData {

        @Param({"2", "4", "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192"})
        int factCount;

        KnowledgeService service;
        Knowledge longLiteralKnowledge;
        Knowledge customFieldLiteralKnowledge;
        Knowledge fastestKnowledge;

        List<Object> facts = new ArrayList<>();

        @Setup(Level.Iteration)
        public void initInvocationData() {
        }

        @Setup(Level.Trial)
        public void initServices() throws IOException {
            service = new KnowledgeService();
            longLiteralKnowledge = service.newKnowledge().importRules("JAVA-CLASS", LongLiteral.class);
            customFieldLiteralKnowledge = service.newKnowledge().importRules("JAVA-CLASS", CustomFieldAnnotated.class);

            fastestKnowledge = service.newKnowledge()
                    .configureTypes(
                            typeResolver -> typeResolver.getOrDeclare(Customer.class)
                                    .declareBooleanField(
                                            "validState",
                                            c -> c.status() == 4 && c.properties().containsKey("VALID")
                                    )
                    )
                    .builder()
                    .newRule()
                    .forEach("$c", Customer.class)
                    .where(
                            (ValuesPredicate) t -> t.get(0, boolean.class), "$c.validState"
                    )
                    .execute(ctx -> {})
                    .build();

            facts.clear();

            for (int i = 0; i < factCount; i++) {
                Customer customer = new Customer(0, 4, "Test name", new HashMap<>());
                facts.add(customer);
            }
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            service.shutdown();
        }

    }


    @RuleSet("Customer Ruleset Literal")
    public static class LongLiteral {

        @Rule("Rule 1")
        @Where("$customer.status == 4 && $customer.properties.containsKey('VALID')")
        public void rule1(Customer $customer) {

        }
    }

    @RuleSet("Customer Ruleset Custom Field Annotated")
    public static class CustomFieldAnnotated {

        @Rule("Rule 1")
        @Where("$customer.validState")
        public void rule1(Customer $customer) {

        }

        @FieldDeclaration
        public boolean validState(Customer c) {
            return c.status() == 4 && c.properties().containsKey("VALID");
        }
    }
}
