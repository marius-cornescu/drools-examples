/**
 * Free
 */
package com.rtzan.drools;

import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Product;
import com.rtzan.drools.salience.CustomerGrouper;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
public class SalienceGroupingTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() {
    }

    @Test
    public void testSalienceGrouping() throws Exception {
        KieSession kSession = buildSessionFromFiles(buildRuleFiles());

        ProductEventListener eventListener = new ProductEventListener();
        kSession.addEventListener(eventListener);
        //
        Customer customer01 = new Customer("ana");
        Customer customer02 = new Customer("mihai");

        Product book1 = new Product("book", 10);
        Product book2 = new Product("big_book", 15);
        Product book3 = new Product("book", 10);
        Product book4 = new Product("big_book", 15);

        Product alcohol1 = new Product("wine", 50);

        Product milk1 = new Product("milk", 5);

        book1.setCustomer(customer01);
        book2.setCustomer(customer01);

        book3.setCustomer(customer02);
        milk1.setCustomer(customer02);
        alcohol1.setCustomer(customer02);

        final List<Product> products = Arrays.asList(book1, book2, book3, book4, milk1, alcohol1);

        process(kSession, eventListener, products);

        //runQuery(kSession);

        kSession.dispose();
    }

    private void process(KieSession kSession, ProductEventListener eventListener, final List<Product> products) {
        for (Product product : products) {
            kSession.insert(product);
        }

        int activesCount = products.size();

        logger.info("## " + " ## Running focus" + "| >>> activesCount = " + activesCount);

        kSession.fireAllRules();

        // work on flagged facts, no need for them to wait for next run
        int processed = workOnFlagged(eventListener.getProductList());
        eventListener.reset();
        activesCount -= processed;

        if (activesCount == 0) {
            logger.info("####    ALL PROCESSED    ####");
        } else {
            logger.info("####    REMAINING = " + activesCount + "    ####");
        }
    }

    private KieSession buildSessionFromFiles(Map<String, String> drlFilePaths) {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = new LoadFileToMemory().build(ks, drlFilePaths);
        return kContainer.newKieSession();
    }

    private void runQuery(KieSession kSession) {
        //QueryResults results = kSession.getQueryResults("FindClassNameStartingWith", Customer.class);
        QueryResults results = kSession.getQueryResults("FindFlaggedCustomers");

        for (QueryResultsRow row : results) {
            Customer customer = (Customer) row.get("object");
            logger.info("Found " + customer);
        }
    }

    private int workOnFlagged(List<Product> flaggeds) {
        logger.info("\t\tIn workOnFlagged(" + flaggeds.size() + ")");
        int workeds = 0;

        for (Product flagged : flaggeds) {
            logger.info("\t\t\t>> workOnFlagged = " + flagged);
            workeds++;
        }

        return workeds;
    }

    private Map<String, String> buildRuleFiles() {
        Map<String, String> fileToContent = new HashMap<>();

        String fileName = "salience_grouping.drl";

        String path = Utils.getResourceFilePath("grouping/" + fileName);

        logger.info("### file = " + path);

        String fileContent = Utils.fileToString(path);

        logger.info("### file size = " + fileContent.length());

        fileToContent.put(fileName, fileContent);

        return fileToContent;
    }

}
