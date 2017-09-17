package com.rtzan.drools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rtzan.drools.model.TestObject;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 * This is a sample class to launch a rule.
 */
class AgendaGroupTest {

	@BeforeEach
	void setUp() {

	}

	@Test
	void testAgendaGroup() throws Exception {
		KieSession knowledgeSession = null;
		try {
			// load up the knowledge base
	        // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
		    KieContainer kContainer = ks.getKieClasspathContainer();
		    knowledgeSession = kContainer.newKieSession("ksession-rules");
		    
			// go !
			String[] agendaGroups = {"Group_01", "Group_02" };

			TestObject testObjectA = new TestObject("A");
			TestObject testObjectB = new TestObject("B");

			for (String group : agendaGroups) {
				System.out.println("Running with " + group + " group in focus");
				knowledgeSession.insert(testObjectA);
				knowledgeSession.insert(testObjectB);
				knowledgeSession.getAgenda().getAgendaGroup(group).setFocus();
				knowledgeSession.fireAllRules();
			}
			
			QueryResults results = knowledgeSession.getQueryResults( "FindClassNameStartingWith", new Object[] { TestObject.class } );
			
			for ( QueryResultsRow row : results ) {
				TestObject testObject = (TestObject) row.get( "object" );
				System.out.println("Found " + testObject);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}