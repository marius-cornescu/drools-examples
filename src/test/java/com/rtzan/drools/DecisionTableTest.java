package com.rtzan.drools;

import java.util.Arrays;

import com.rtzan.drools.model.Driver;
import com.rtzan.drools.model.Policy;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;


public class DecisionTableTest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() {
    }

    @Test
    @Ignore
    public void testDecisionTable() throws Exception {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        System.out.println(kc.verify().getMessages().toString());
        StatelessKieSession ksession = kc.newStatelessKieSession("DecisionTableKS");

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        ksession.execute(Arrays.asList(new Object[] { driver, policy }));

        System.out.println("BASE PRICE IS: " + policy.getBasePrice());
        System.out.println("DISCOUNT IS: " + policy.getDiscountPercent());

        int price = policy.getBasePrice();
    }

}
