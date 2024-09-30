package org.evrete.guides.examples.testing;

import org.evrete.api.RhsContext;
import org.evrete.dsl.annotation.Fact;
import org.evrete.dsl.annotation.Rule;
import org.evrete.dsl.annotation.RuleSet;
import org.evrete.dsl.annotation.Where;

/**
 * <p>
 * This is a simple ruleset consisting of two rules which 'talk' to each other, exchanging numbers.
 * When the 'Answer Rule' sees a {@link Question} instance, it inserts a new {@link Answer}
 * instance with the same number into the working memory. When the 'New Question Rule' sees that answer,
 * it inserts a new question with the number increased by one. The process continues until the number
 * reaches <code>5</code>.
 * </p>
 */
@RuleSet
public class GuessingMachine {

    @Rule("Answer Rule")
    public void answerRule(@Fact("$q") Question question, RhsContext context) {
        // Inserting new Answer fact with the same number
        context.insert(new Answer(question.number()));
    }

    @Rule("New Question Rule")
    @Where("$a.number < 5")
    public void newQuestionRule(@Fact("$a") Answer answer, RhsContext context) {
        // Inserting new question with the number increased by one
        context.insert(new Question(answer.number() + 1));
    }

    public record Question(int number) {
    }

    public record Answer(int number) {
    }
}
