package com.rtzan.drools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Product;

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
            CustomerEventListener customerEventListener = new CustomerEventListener();
            kSession.addEventListener(customerEventListener);
            //
            final List<Customer> customers = getCustomers();

            for (Customer customer : customers) {
                kSession.insert(customer);
            }

            List<String> agendaGroups = AgendaGroupHelper.getAgendaGroupLabels(kSession);

            int groupIndex = 0;
            for (String group : agendaGroups) {
                System.out.println("## " + (++groupIndex) + " ## Running with [" + group + "] group in focus");

                kSession.getAgenda().getAgendaGroup(group).setFocus();

                // remove facts that were flagged
                kSession.fireAllRules(match -> {
                    Customer customer = (Customer) match.getObjects().get(0);
                    return !customer.isFlagged();
                });

                // work on flagged facts, no need for them to wait for next run
                workOnFlaggedCustomers(customerEventListener.getCustomerList());
                customerEventListener.reset();
            }

            System.out.println("*************************");

            runQuery(kSession);

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    List<Customer> getCustomers() {
        Customer customer01 = new Customer("ana_01");
        customer01.addItem(new Product("book", 10), 1);

        Customer customer02 = new Customer("ana_02");

        Customer customer03 = new Customer("ana_03");
        customer03.addItem(new Product("book", 10), 1);

        return Arrays.asList(customer01, customer02, customer03);
    }

    private void runQuery(KieSession kSession) {
        //QueryResults results = kSession.getQueryResults("FindClassNameStartingWith", Customer.class);
        QueryResults results = kSession.getQueryResults("FindFlaggedCustomers");

        for (QueryResultsRow row : results) {
            Customer customer = (Customer) row.get("object");
            System.out.println("Found " + customer);
        }
    }

    private void workOnFlaggedCustomers(List<Customer> flaggedCustomers) {
        System.out.println("In workOnFlaggedCustomers(" + flaggedCustomers.size() + ")");

        for (Customer flaggedCustomer : flaggedCustomers) {
            System.out.println(">> workOnFlaggedCustomer = " + flaggedCustomer);

        }

        flaggedCustomers.clear();
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
