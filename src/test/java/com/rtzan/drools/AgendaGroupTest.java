package com.rtzan.drools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Product;

import org.junit.Before;
import org.junit.Ignore;
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
    @Ignore
    public void testBasicAgendaGroup() throws Exception {
        try {
            KieSession kSession = AgendaGroupHelper.createKieSession(null, buildRuleFiles());
            //KieSession kSession = buildSessionFromFiles(buildRuleFiles());
            DefaultCustomerEventListener customerEventListener = new DefaultCustomerEventListener();
            kSession.addEventListener(customerEventListener);
            //
            Customer customer01 = new Customer("ana_01");
            customer01.addItem(new Product("book", 10), 1);

            Customer customer02 = new Customer("ana_02");

            Customer customer03 = new Customer("ana_03");
            customer03.addItem(new Product("book", 10), 1);

            final List<Customer> customers = Arrays.asList(customer01, customer02, customer03);

            processCustomers(kSession, customerEventListener, customers);

            //runQuery(kSession);

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void testAgendaGroupSomeMatch() throws Exception {
        try {
            KieSession kSession = buildSessionFromFiles(buildRuleFiles());
            DefaultCustomerEventListener customerEventListener = new DefaultCustomerEventListener();
            kSession.addEventListener(customerEventListener);
            //
            Customer customer01 = new Customer("ana_01");
            customer01.addItem(new Product("book", 10), 1);

            Customer customer02 = new Customer("ana_02");

            Customer customer03 = new Customer("ana_03");
            customer03.addItem(new Product("book", 10), 1);

            Customer customer04 = new Customer("eric_01");
            customer04.addItem(new Product("book", 10), 1);

            final List<Customer> customers = Arrays.asList(customer01, customer02, customer03, customer04);

            processCustomers(kSession, customerEventListener, customers);

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void testEarlyMatchNotTriggerAllGroups() throws Exception {
        try {
            KieSession kSession = buildSessionFromFiles(buildRuleFiles());
            DefaultCustomerEventListener customerEventListener = new DefaultCustomerEventListener();
            kSession.addEventListener(customerEventListener);
            //
            Customer customer01 = new Customer("ana_01");
            customer01.addItem(new Product("book", 10), 1);

            Customer customer02 = new Customer("ana_02");

            Customer customer03 = new Customer("ana_03");
            customer03.addItem(new Product("book", 10), 1);

            final List<Customer> customers = Arrays.asList(customer01, customer02, customer03);

            processCustomers(kSession, customerEventListener, customers);

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void processCustomers(KieSession kSession, CustomerEventListener eventListener, final List<Customer> customers) {
        for (Customer customer : customers) {
            kSession.insert(customer);
        }

        int activeCustomerCount = customers.size();

        //List<String> agendaGroups = Arrays.asList("Group_01", "Group_02", "Group_03", "Group_04", "Group_05", "Group_06", "Group_07");
        List<String> agendaGroups = AgendaGroupHelper.getAgendaGroupLabels(kSession);

        int groupIndex = 0;
        for (String group : agendaGroups) {
            System.out.println("\n## " + (++groupIndex) + " ## Running with [" + group + "] group in focus" + "| >>> activeCustomerCount = " + activeCustomerCount);

            kSession.getAgenda().getAgendaGroup(group).setFocus();

            // remove facts that were flagged
            kSession.fireAllRules(match -> {
                Customer customer = (Customer) match.getObjects().get(0);
                return !customer.isFlagged();
            });

            // work on flagged facts, no need for them to wait for next run
            int processedCustomers = workOnFlaggedCustomers(eventListener.getCustomerList());
            eventListener.reset();
            activeCustomerCount -= processedCustomers;

            if (activeCustomerCount == 0) {
                break;
            }
        }

        if (activeCustomerCount == 0) {
            System.out.println("####    ALL PROCESSED    ####");
        } else {
            System.out.println("####    REMAINING Customers = " + activeCustomerCount + "    ####");
        }
    }

    private int countCustomers(KieSession kSession) {
        QueryResults results = kSession.getQueryResults("getCustomers");
        return results.size();
    }

    private void runQuery(KieSession kSession) {
        //QueryResults results = kSession.getQueryResults("FindClassNameStartingWith", Customer.class);
        QueryResults results = kSession.getQueryResults("FindFlaggedCustomers");

        for (QueryResultsRow row : results) {
            Customer customer = (Customer) row.get("object");
            System.out.println("Found " + customer);
        }
    }

    private int workOnFlaggedCustomers(List<Customer> flaggedCustomers) {
        System.out.println("\t\tIn workOnFlaggedCustomers(" + flaggedCustomers.size() + ")");
        int workedCustomers = 0;

        for (Customer flaggedCustomer : flaggedCustomers) {
            System.out.println("\t\t\t>> workOnFlaggedCustomer = " + flaggedCustomer);
            workedCustomers++;
        }

        return workedCustomers;
    }

    private KieSession buildKieSession() {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        return kContainer.newKieSession("ksession-rules");
    }

    private KieSession buildSessionFromFiles(Map<String, String> drlFilePaths) {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = new LoadFileToMemory().build(ks, drlFilePaths);
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
