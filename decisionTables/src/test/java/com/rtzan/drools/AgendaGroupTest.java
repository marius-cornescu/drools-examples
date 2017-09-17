package com.rtzan.drools;

import com.rtzan.drools.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 * This is a sample class to launch a rule.
 */
class AgendaGroupTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void testAgendaGroup() throws Exception {
        KieSession knowledgeSession = null;
        try {
            // load up the knowledge base
            // load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            knowledgeSession = kContainer.newKieSession("ksession-rules");

            // go !
            String[] agendaGroups = {"Group_01", "Group_02"};

            Customer customer01 = new Customer("ana_01");
            Customer customer02 = new Customer("ana_02");
            Customer customer03 = new Customer("ana_03");

            for (String group : agendaGroups) {
                System.out.println("Running with " + group + " group in focus");
                knowledgeSession.insert(customer01);
                knowledgeSession.insert(customer02);
                knowledgeSession.insert(customer03);

                knowledgeSession.getAgenda().getAgendaGroup(group).setFocus();

                knowledgeSession.fireAllRules();
            }

            QueryResults results = knowledgeSession.getQueryResults("FindClassNameStartingWith", new Object[]{Customer.class});

            for (QueryResultsRow row : results) {
                Customer customer = (Customer) row.get("object");
                System.out.println("Found " + customer);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}