package com.rtzan.drools;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.util.StringUtil;

import org.drools.core.common.DefaultAgenda;
import org.drools.core.spi.AgendaGroup;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


public class AgendaGroupHelper {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static List<String> getAgendaGroupLabels(KieSession knowledgeSession) {
        List<String> agendaGroups = new LinkedList<>();

        AgendaGroup[] agendas = ((DefaultAgenda) knowledgeSession.getAgenda()).getAgendaGroups();

        for (AgendaGroup agenda : agendas) {
            agendaGroups.add(agenda.getName());
        }

        return agendaGroups.stream().filter(s -> !"MAIN".equals(s)).sorted().collect(Collectors.toList());
    }

    public static KieSession createKieSession(String kieSessionName, Map<String, String> drlFilePaths) {
        KieContainerFactory kieContainerFactory = new KieContainerFactory();

        KieContainer kieContainer = kieContainerFactory.createKieContainer(drlFilePaths);

        return (kieSessionName == null) ? kieContainer.newKieSession() : kieContainer.newKieSession(kieSessionName);
    }

}
