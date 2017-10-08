/** Free */
package com.rtzan.drools.salience;

import com.rtzan.drools.ProductEventListener;
import com.rtzan.drools.model.Product;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SalienceGrouper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void process(KieSession kSession, ProductEventListener eventListener, final List<Product> products) {
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

    private int workOnFlagged(List<Product> flaggeds) {
        logger.info("\t\tIn workOnFlagged(" + flaggeds.size() + ")");
        int workeds = 0;

        for (Product flagged : flaggeds) {
            logger.info("\t\t\t>> workOnFlagged = " + flagged);
            workeds++;
        }

        return workeds;
    }

}
