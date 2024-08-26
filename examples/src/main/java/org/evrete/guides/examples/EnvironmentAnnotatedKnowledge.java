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

public class EnvironmentAnnotatedKnowledge {

    public static void main(String[] args) throws IOException {
        KnowledgeService service = new KnowledgeService();
        Knowledge knowledge = service.newKnowledge()
                .importRules(Constants.PROVIDER_JAVA_CLASS, ExampleRuleset.class);

        // Overriding the value for a particular session
        knowledge.set("ENV_KEY", 1234);

        // This will print 'Env property value: 1234'
        knowledge.newStatelessSession().insertAndFire("Hello World");
    }

    public static class ExampleRuleset {
        private static int envValue;

        @EventSubscription
        public static void envSessionListener(EnvironmentChangeEvent e) {
            if(e.getProperty().equals("ENV_KEY")) {
                envValue = (int) e.getValue();
            }
        }

        @Rule
        public void rule(@Fact("$s") String fact) {
            System.out.println("Env property value: " + envValue);
        }
    }
}




