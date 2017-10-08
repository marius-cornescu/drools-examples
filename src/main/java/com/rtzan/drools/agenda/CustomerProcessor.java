/** Free */
package com.rtzan.drools.agenda;

import java.util.Arrays;
import java.util.List;

import com.rtzan.drools.AgendaGroupHelper;
import com.rtzan.drools.CustomerEventListener;
import com.rtzan.drools.model.Customer;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomerProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void process(KieSession kSession, CustomerEventListener eventListener, final List<Customer> customers) {
        for (Customer customer : customers) {
            kSession.insert(customer);
        }

        int activeCustomerCount = customers.size();

        List<String> agendaGroups = Arrays.asList("Group_01", "Group_02", "Group_03", "Group_04", "Group_05", "Group_06", "Group_07");
        //List<String> agendaGroups = AgendaGroupHelper.getAgendaGroupLabels(kSession);

        int groupIndex = 0;
        for (String group : agendaGroups) {
            logger.info("\n## " + (++groupIndex) + " ## Running with [" + group + "] group in focus" + "| >>> activeCustomerCount = " + activeCustomerCount);

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
            logger.info("####    ALL PROCESSED    ####");
        } else {
            logger.info("####    REMAINING Customers = " + activeCustomerCount + "    ####");
        }
    }

    private int workOnFlaggedCustomers(List<Customer> flaggedCustomers) {
        logger.info("\t\tIn workOnFlaggedCustomers(" + flaggedCustomers.size() + ")");
        int workedCustomers = 0;

        for (Customer flaggedCustomer : flaggedCustomers) {
            logger.info("\t\t\t>> workOnFlaggedCustomer = " + flaggedCustomer);
            workedCustomers++;
        }

        return workedCustomers;
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
            logger.info("Found " + customer);
        }
    }
}
