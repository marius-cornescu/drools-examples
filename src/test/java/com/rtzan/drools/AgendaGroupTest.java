package com.rtzan.drools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rtzan.drools.model.Customer;

import org.junit.Before;
import org.junit.Test;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
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
        try {
            //KieSession kSession = AgendaGroupHelper.createKieSession(null, buildRuleFiles());
            KieSession kSession = buildKieSession2();
            //
            Customer customer01 = new Customer("ana_01");
            Customer customer02 = new Customer("ana_02");
            Customer customer03 = new Customer("ana_03");

            kSession.insert(customer01);
            kSession.insert(customer02);
            kSession.insert(customer03);

            List<String> agendaGroups = AgendaGroupHelper.getAgendaGroupLabels(kSession);

            for (String group : agendaGroups) {
                System.out.println("Running with [" + group + "] group in focus");

                kSession.getAgenda().getAgendaGroup(group).setFocus();

                kSession.fireAllRules(match -> {
                    Customer customer = (Customer) match.getObjects().get(0);
                    return customer.isFlagged();
                });
            }

            System.out.println("*************************");
            QueryResults results = kSession.getQueryResults("FindClassNameStartingWith", Customer.class);

            for (QueryResultsRow row : results) {
                Customer customer = (Customer) row.get("object");
                System.out.println("Found " + customer);
            }

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private KieSession buildKieSession() {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        return kContainer.newKieSession("ksession-rules");
    }

    private KieSession buildKieSession2() {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = new LoadFileToMemory().build(ks, buildRuleFiles());
        return kContainer.newKieSession();
    }

    private Map<String, String> buildRuleFiles() {
        Map<String, String> fileToContent = new HashMap<>();

        String fileName = "agenda_groups.drl";

        String path = Utils.getResourceFilePath("agenda-groups/" + fileName);

        System.out.println("### file = " + path);

        fileToContent.put(fileName, Utils.fileToString(path));

        return fileToContent;
    }

}
