package com.rtzan.drools;

import java.util.List;

import com.rtzan.drools.model.Customer;

import org.junit.Before;
import org.junit.Test;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;


/**
 * This is a sample class to launch a rule.
 */
public class AgendaGroupTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() {

    }

    @Test
    public void testAgendaGroup() throws Exception {
        KieSession knowledgeSession;
        try {
            // load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            knowledgeSession = kContainer.newKieSession("ksession-rules");

            //
            Customer customer01 = new Customer("ana_01");
            Customer customer02 = new Customer("ana_02");
            Customer customer03 = new Customer("ana_03");

            knowledgeSession.insert(customer01);
            knowledgeSession.insert(customer02);
            knowledgeSession.insert(customer03);

            List<String> agendaGroups = AgendaGroupHelper.getAgendaGroupLabels(knowledgeSession);

            for (String group : agendaGroups) {
                System.out.println("Running with [" + group + "] group in focus");

                knowledgeSession.getAgenda().getAgendaGroup(group).setFocus();

                knowledgeSession.fireAllRules(match -> {
                    Customer customer = (Customer) match.getObjects().get(0);
                    return customer.isFlagged();
                });
            }

            System.out.println("*************************");
            QueryResults results = knowledgeSession.getQueryResults("FindClassNameStartingWith", Customer.class);

            for (QueryResultsRow row : results) {
                Customer customer = (Customer) row.get("object");
                System.out.println("Found " + customer);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
