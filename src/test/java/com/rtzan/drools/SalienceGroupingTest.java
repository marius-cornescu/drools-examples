/**
 * Free
 */
package com.rtzan.drools;

import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Product;
import com.rtzan.drools.model.Receipt;
import com.rtzan.drools.salience.SalienceGrouper;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 */
public class SalienceGroupingTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SalienceGrouper grouper;


    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() {
        grouper = new SalienceGrouper();
    }

    @Test
    public void testSalienceGrouping() throws Exception {
        KieSession kSession = buildSessionFromFiles(buildRuleFiles());

        ProductEventListener eventListener = new ProductEventListener();
        kSession.addEventListener(eventListener);
        //
        Customer customer01 = new Customer("ana");
        Customer customer02 = new Customer("mihai");
        Customer customer03 = new Customer("jeean");
        Customer customer04 = new Customer("bob");

        Product book1 = new Product("book", 10);
        Product book2 = new Product("big_book", 15);
        Product book3 = new Product("book", 10);
        Product book4 = new Product("big_book", 15);

        Product alcohol1 = new Product("wine", 50);
        Product alcohol2 = new Product("vodka", 50);

        Product milk1 = new Product("milk", 5);

        book1.setCustomer(customer01);
        book2.setCustomer(customer01);

        book3.setCustomer(customer02);
        milk1.setCustomer(customer02);
        alcohol1.setCustomer(customer02);

        book4.setCustomer(customer03);

        alcohol2.setCustomer(customer04);

        final List<Product> products = Arrays.asList(book1, book2, book3, book4, milk1, alcohol1, alcohol2);

        grouper.process(kSession, eventListener, products);

        List<Receipt> receipts = runQuery(kSession, "FindReceipts");

        for (Receipt receipt : receipts) {
            logger.info("### Receipt: [{}]", receipt);
        }

        kSession.dispose();
    }

    private KieSession buildSessionFromFiles(Map<String, String> drlFilePaths) {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = new LoadFileToMemory().build(ks, drlFilePaths);
        return kContainer.newKieSession();
    }

    private <T> List<T> runQuery(KieSession kSession, String queryName) {
        QueryResults results = kSession.getQueryResults(queryName);

        for (QueryResultsRow row : results) {
            T fact = (T) row.get("object");
            logger.debug("Found: " + fact);
        }

        return (List<T>) StreamSupport.stream(results.spliterator(), false).map(r -> r.get("object")).collect(Collectors.toList());
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
