package com.rtzan.drools;

import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;


public class KieContainerFactory {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private KieServices kieServices;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public KieContainer createKieContainer(Map<String, String> drlFilePaths) {

        this.kieServices = KieServices.Factory.get();

        return createKieContainerFromFile(drlFilePaths);
    }

    private KieContainer createKieContainerFromFile(Map<String, String> drlFilePaths) {
        KieFileSystem kfs = kieServices.newKieFileSystem();

        for (Map.Entry<String, String> drlFileEntry : drlFilePaths.entrySet()) {
            String filePath = "agenda-groups/" + drlFileEntry.getKey();
            kfs.write(filePath, drlFileEntry.getValue());
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        KieContainer kieContainerFromFile = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());

        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build errors:\n" + kieBuilder.getResults().toString());
        }

        return kieContainerFromFile;
    }

}
