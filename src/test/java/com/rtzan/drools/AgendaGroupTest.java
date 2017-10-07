/** Free */
package com.rtzan.drools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rtzan.drools.agenda.CustomerProcessor;
import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Product;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


/**
 * This is a sample class to launch a rule.
 */
public class AgendaGroupTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private CustomerProcessor customerProcessor;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() {
        customerProcessor = new CustomerProcessor();
    }

    @Test
    @Ignore
    public void testBasicAgendaGroup() throws Exception {
        try {
            KieSession kSession = AgendaGroupHelper.createKieSession(null, buildRuleFiles());
            //KieSession kSession = buildSessionFromFiles(buildRuleFiles());
            CustomerEventListener customerEventListener = new CustomerEventListener();
            kSession.addEventListener(customerEventListener);
            //
            Customer customer01 = new Customer("ana_01");
            customer01.addItem(new Product("book", 10), 1);

            Customer customer02 = new Customer("ana_02");

            Customer customer03 = new Customer("ana_03");
            customer03.addItem(new Product("book", 10), 1);

            final List<Customer> customers = Arrays.asList(customer01, customer02, customer03);

            customerProcessor.process(kSession, customerEventListener, customers);

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
            CustomerEventListener customerEventListener = new CustomerEventListener();
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

            customerProcessor.process(kSession, customerEventListener, customers);

            kSession.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void testEarlyMatchNotTriggerAllGroups() throws Exception {
        try {
            KieSession kSession = buildSessionFromFiles(buildRuleFiles());
            CustomerEventListener customerEventListener = new CustomerEventListener();
            kSession.addEventListener(customerEventListener);
            //
            Customer customer01 = new Customer("ana_01");
            customer01.addItem(new Product("book", 10), 1);

            Customer customer02 = new Customer("ana_02");

            Customer customer03 = new Customer("ana_03");
            customer03.addItem(new Product("book", 10), 1);

            final List<Customer> customers = Arrays.asList(customer01, customer02, customer03);

            customerProcessor.process(kSession, customerEventListener, customers);

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
