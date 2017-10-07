/** Free */
package com.rtzan.drools;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.rule.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class DefaultEventListener extends DefaultAgendaEventListener {

    private static final boolean DEBUG = true;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private List<String> activationList = new ArrayList<>();

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

    public void reset() {
        activationList.clear();
    }

    List<String> getActivationList() {
        return new ArrayList<>(activationList);
    }

    protected abstract void afterActivationFired(String ruleName, List<Object> objects);

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

        activationList.add(ruleName);
        afterActivationFired(ruleName, match.getObjects());

        if (DEBUG) {
            printActivationList(rule);
        }
    }

    private void printActivationList(Rule rule) {
        String ruleName = rule.getName();
        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

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
