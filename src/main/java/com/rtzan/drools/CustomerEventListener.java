/**
 *  Copyright Murex S.A.S., 2003-2017. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package com.rtzan.drools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rtzan.drools.model.Customer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.rule.Match;


public class CustomerEventListener extends DefaultAgendaEventListener {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private List<String> activationList = new ArrayList<>();
    private List<Customer> customerList = new ArrayList<>();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        System.out.println("1. agendaGroupPushed"); // after "kSession.getAgenda().getAgendaGroup(group).setFocus()"
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        System.out.println("agendaGroupPopped"); //
    }

    public void matchCreated(MatchCreatedEvent event) {
        System.out.println("2*. matchCreated");
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        System.out.println("3*. beforeMatchFired");
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
        System.out.println("4*. afterMatchFired"); //
        afterActivationFired(event.getMatch());
    }

    void reset() {
        activationList.clear();
        customerList.clear();
    }

    final List<String> getActivationList() {
        return new ArrayList<>(activationList);
    }

    final List<Customer> getCustomerList() {
        return new ArrayList<>(customerList);
    }

//    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
//        System.out.println("beforeRuleFlowGroupActivated");
//    }
//
//    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
//        System.out.println("afterRuleFlowGroupActivated");
//    }
//
//    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
//        System.out.println("beforeRuleFlowGroupDeactivated");
//    }
//
//    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
//        System.out.println("afterRuleFlowGroupDeactivated");
//    }

    private void afterActivationFired(Match match) {
        Rule rule = match.getRule();
        String ruleName = rule.getName();
        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

        activationList.add(ruleName);
        customerList.addAll(match.getObjects().stream().map(o -> (Customer) o).collect(Collectors.toList()));

        StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

        if (ruleMetaDataMap.size() > 0) {
            sb.append("\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
            for (String key : ruleMetaDataMap.keySet()) {
                sb.append("\n    key=" + key + ", value=" + ruleMetaDataMap.get(key));
            }
        }

        System.out.println(sb.toString());
    }
}
