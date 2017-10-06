/** Free */
package com.rtzan.drools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Product;
import com.rtzan.drools.salience.CustomerGrouper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;


/**
 */
public class SalienceGroupingTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private CustomerGrouper customerGrouper;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() {
        customerGrouper = new CustomerGrouper();
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

    public void processCustomers(KieSession kSession, CustomerEventListener eventListener, final List<Customer> customers) {
        for (Customer customer : customers) {
            kSession.insert(customer);
        }

        int activeCustomerCount = customers.size();

        System.out.println("\n## " + " ## Running focus" + "| >>> activeCustomerCount = " + activeCustomerCount);

        kSession.fireAllRules();

        // work on flagged facts, no need for them to wait for next run
        int processedCustomers = workOnFlaggedCustomers(eventListener.getCustomerList());
        eventListener.reset();
        activeCustomerCount -= processedCustomers;

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

        String fileName = "salience_grouping.drl";

        String path = Utils.getResourceFilePath("grouping/" + fileName);

        System.out.println("### file = " + path);

        fileToContent.put(fileName, Utils.fileToString(path));

        return fileToContent;
    }

}
