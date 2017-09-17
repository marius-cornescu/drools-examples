package com.rtzan.drools;

import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Product;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.runtime.StatelessKnowledgeSession;

/**
 * Created by ${USERNAME} on 9/17/17.
 */
public class LegacyDecisionTableTest {

    private LegacyDecisionTable decisionTable;

    @Before
    public void setUp() {
        decisionTable = new LegacyDecisionTable();
    }

    @Test
    public void testExcelDecisionTable() throws Exception {
        KnowledgeBase knowledgeBase = decisionTable.createKnowledgeBase("decision-table/shopping_cart_customer.xls", DecisionTableInputType.XLS);
        StatelessKnowledgeSession session = knowledgeBase.newStatelessKnowledgeSession();

        basicTestFlow(session);
    }

    @Test
    @Ignore("CSV file format is wrong")
    public void testCsvDecisionTable() throws Exception {
        KnowledgeBase knowledgeBase = decisionTable.createKnowledgeBase("decision-table/shopping_cart_customer.csv", DecisionTableInputType.CSV);
        StatelessKnowledgeSession session = knowledgeBase.newStatelessKnowledgeSession();

        basicTestFlow(session);
    }

    private void basicTestFlow(StatelessKnowledgeSession session) {
        Customer customer = new Customer("ana_01");
        Product p1 = new Product("Laptop", 15000);
        Product p2 = new Product("Mobile", 5000);
        Product p3 = new Product("Books", 2000);
        customer.addItem(p1, 1);
        customer.addItem(p2, 2);
        customer.addItem(p3, 5);
        customer.setCoupon("DISC01");

        session.execute(customer);

        System.out.println("First Customer\n" + customer);

        Customer newCustomer = new Customer("ana_02");
        newCustomer.addItem(p1, 1);
        newCustomer.addItem(p2, 2);

        session.execute(newCustomer);

        System.out.println("*********************************");
        System.out.println("Second Customer\n" + customer);
    }

}