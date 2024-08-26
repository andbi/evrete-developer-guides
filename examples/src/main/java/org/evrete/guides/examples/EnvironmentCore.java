package org.evrete.guides.examples;

import org.evrete.KnowledgeService;
import org.evrete.api.Knowledge;
import org.evrete.api.StatelessSession;

@SuppressWarnings("unused")
public class EnvironmentCore {

    public static void main(String[] args) {
        KnowledgeService service = new KnowledgeService();
        Knowledge knowledge = service.newKnowledge()
                .builder()
                .newRule()
                .forEach("$c", String.class)
                .execute(context -> {
                    // Reading the value from the runtime context
                    int keyValue = context.getRuntime().get("ENV_KEY");
                    System.out.println("Env property value: " + keyValue);
                }).build();

        // Setting the environment value for Knowledge
        knowledge.set("ENV_KEY", 1234);

        // This will print 'Env property value: 1234'
        knowledge.newStatelessSession().insertAndFire("Hello World");

        // Overriding the value for a particular session
        StatelessSession session = knowledge.newStatelessSession();
        session.set("ENV_KEY", 5678);

        // This will print 'Env property value: 5678'
        session.insertAndFire("Hello World");
    }
}




