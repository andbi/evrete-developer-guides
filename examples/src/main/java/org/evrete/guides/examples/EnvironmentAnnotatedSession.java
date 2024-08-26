package org.evrete.guides.examples;

import org.evrete.KnowledgeService;
import org.evrete.api.Knowledge;
import org.evrete.api.StatelessSession;
import org.evrete.api.events.EnvironmentChangeEvent;
import org.evrete.dsl.Constants;
import org.evrete.dsl.annotation.EventSubscription;
import org.evrete.dsl.annotation.Fact;
import org.evrete.dsl.annotation.Rule;

import java.io.IOException;

public class EnvironmentAnnotatedSession {

    public static void main(String[] args) throws IOException {
        KnowledgeService service = new KnowledgeService();
        Knowledge knowledge = service.newKnowledge()
                .importRules(Constants.PROVIDER_JAVA_CLASS, ExampleRuleset.class);

        // Overriding the value for a particular session
        StatelessSession session = knowledge.newStatelessSession();
        session.set("ENV_KEY", 1234);

        // This will print 'Env property value: 1234'
        session.insertAndFire("Hello World");
    }

    public static class ExampleRuleset {
        private int envValue;

        @EventSubscription
        public void envSessionListener(EnvironmentChangeEvent event) {
            if(event.getProperty().equals("ENV_KEY")) {
                this.envValue = (int) event.getValue();
            }
        }

        @Rule
        public void rule(@Fact("$s") String fact) {
            System.out.println("Env property value: " + envValue);
        }
    }
}




