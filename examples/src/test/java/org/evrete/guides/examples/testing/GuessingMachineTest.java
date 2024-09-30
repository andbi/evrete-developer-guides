package org.evrete.guides.examples.testing;

import org.evrete.KnowledgeService;
import org.evrete.api.ActivationManager;
import org.evrete.api.Knowledge;
import org.evrete.api.RuntimeRule;
import org.evrete.api.StatelessSession;
import org.evrete.dsl.Constants;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GuessingMachineTest {
    static KnowledgeService service = new KnowledgeService();
    private StatelessSession session;

    @BeforeAll
    static void initService() {
        service = new KnowledgeService();
    }

    @AfterAll
    static void shutdownService() {
        service.shutdown();
    }

    @BeforeEach
    void setUp() throws Exception {
        Knowledge knowledge = service.newKnowledge()
                .importRules(Constants.PROVIDER_JAVA_CLASS, GuessingMachine.class);

        this.session = knowledge.newStatelessSession();
    }


    @Test
    void testOutcome() {

        // Insert first question
        session.insert(new GuessingMachine.Question(1));

        // Prepare fact counters
        AtomicInteger questions = new AtomicInteger();
        AtomicInteger answers = new AtomicInteger();

        // Fire the rules
        session.fire(memoryFact -> {
            if (memoryFact instanceof GuessingMachine.Question) {
                questions.incrementAndGet();
            } else if (memoryFact instanceof GuessingMachine.Answer) {
                answers.incrementAndGet();
            } else {
                throw new IllegalStateException("Unexpected object: " + memoryFact);
            }
        });

        Assertions.assertEquals(5, questions.get());
        Assertions.assertEquals(5, answers.get());
    }

    @Test
    void testRuleActivationOrder() {

        /*
         * Preparing a list of rule activations.
         * While the Activation Manager is executed on the main thread by default,
         * it's better to use a synchronized collection as the behavior may change in the future.
         */
        List<String> activationHistory = Collections.synchronizedList(new ArrayList<>());

        session.setActivationManager(new ActivationManager() {
            @Override
            public void onActivation(RuntimeRule rule, long count) {
                activationHistory.add(rule.getName());
            }
        });

        // Insert first question
        session.insert(new GuessingMachine.Question(1));

        // Fire the rules
        session.fire();

        // Correct execution order (given the logic of the ruleset and the initial fact)
        List<String> expectedOrder = List.of(
                "Answer Rule", // Question = 1, new Answer = 1
                "New Question Rule", // Answer = 1, new Question = 2
                "Answer Rule", // Question = 2, new Answer = 2
                "New Question Rule", // Answer = 2, new Question = 3
                "Answer Rule", // Question = 3, new Answer = 3
                "New Question Rule", // Answer = 3, new Question = 4
                "Answer Rule", // Question = 4, new Answer = 4
                "New Question Rule", // Answer = 4, new Question = 5
                "Answer Rule" // Question = 5, new Answer = 5
                // This is where the `New Question Rule` stops
                // as it expects value to be less than 5
        );

        Assertions.assertIterableEquals(expectedOrder, activationHistory);
    }

    @Test
    void testAnswerFactHistory() {

        List<GuessingMachine.Question> questionHistory = Collections.synchronizedList(new ArrayList<>());

        // Adding a testing hook method for the `Answer Rule`
        session.getRule("Answer Rule")
                .chainRhs(rhsContext -> {
                    GuessingMachine.Question question = rhsContext.get("$q");
                    questionHistory.add(question);
                });

        // Insert first question and fire the session
        session.insertAndFire(new GuessingMachine.Question(1));

        // Expected question history:
        List<GuessingMachine.Question> expectedHistory = List.of(
                new GuessingMachine.Question(1),
                new GuessingMachine.Question(2),
                new GuessingMachine.Question(3),
                new GuessingMachine.Question(4),
                new GuessingMachine.Question(5)
        );

        Assertions.assertIterableEquals(expectedHistory, questionHistory);
    }
}
