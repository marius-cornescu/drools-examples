package com.rtzan.drools;

import com.rtzan.drools.model.Customer;
import com.rtzan.drools.model.Driver;
import com.rtzan.drools.model.Policy;
import com.rtzan.drools.model.Product;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.util.Arrays;

/**
 * Created by ${USERNAME} on 9/17/17.
 */
public class DecisionTableTest {

    @Before
    public void setUp() {
    }

    @Test
    public void testDecisionTable() throws Exception {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        System.out.println(kc.verify().getMessages().toString());
        StatelessKieSession ksession = kc.newStatelessKieSession( "DecisionTableKS");

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        ksession.execute( Arrays.asList( new Object[]{driver, policy} ) );

        System.out.println( "BASE PRICE IS: " + policy.getBasePrice() );
        System.out.println( "DISCOUNT IS: " + policy.getDiscountPercent() );

        int price = policy.getBasePrice();
    }

}
