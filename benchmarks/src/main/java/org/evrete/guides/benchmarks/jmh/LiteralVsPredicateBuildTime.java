package org.evrete.guides.benchmarks.jmh;

import org.evrete.KnowledgeService;
import org.evrete.dsl.annotation.FieldDeclaration;
import org.evrete.dsl.annotation.Rule;
import org.evrete.dsl.annotation.RuleSet;
import org.evrete.dsl.annotation.Where;
import org.evrete.guides.classes.Customer;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

public class LiteralVsPredicateBuildTime {


    @Benchmark
    public void longLiteral(BenchmarkData state) throws IOException {
        state.service.newKnowledge()
                .importRules("JAVA-CLASS", LongLiteral.class);
    }

    @Benchmark
    public void customField(BenchmarkData state) throws IOException {
        state.service.newKnowledge()
                .configureTypes(typeResolver -> typeResolver.getOrDeclare(Customer.class)
                        .declareBooleanField(
                                "validState",
                                c -> c.status() == 4 && c.properties().containsKey("VALID")
                        ))
                .importRules("JAVA-CLASS", CustomField.class);
    }

    @Benchmark
    public void customFieldAnnotated(BenchmarkData state) throws IOException {
        state.service.newKnowledge()
                .importRules("JAVA-CLASS", CustomFieldAnnotated.class);
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


    @RuleSet("Customer Ruleset Literal")
    public static class LongLiteral {

        @Rule("Rule 1")
        @Where("$customer.status == 4 && $customer.properties.containsKey('VALID')")
        public void rule1(Customer $customer) {

        }
    }

    @RuleSet("Customer Ruleset Custom Field 1")
    public static class CustomField {

        @Rule("Rule 1")
        @Where("$customer.validState")
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

