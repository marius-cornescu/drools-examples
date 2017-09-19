package com.rtzan.drools;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;


public class LoadFileToMemory {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public KieContainer build(KieServices kieServices, Map<String, String> drlFilePaths) {
        return build(kieServices, "com.rtzan.drools", "agenda-groups", drlFilePaths);
    }

    public KieContainer build(KieServices kieServices, String groupId, String artifactId, Map<String, String> drlFilePaths) {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        ReleaseId rid = kieServices.newReleaseId(groupId, artifactId, "1.0-SNAPSHOT");
        kieFileSystem.generateAndWritePomXML(rid);

        addRules(kieServices, kieFileSystem, drlFilePaths);

        //kieFileSystem.write("src/main/resources/rules.drl", getResource(kieServices, "agenda-groups/agenda_groups.drl"));
        //addRule(kieFileSystem);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" +
                kieBuilder.getResults().toString());
        }

        return kieServices.newKieContainer(rid);
    }

    private void addRule(KieFileSystem kieFileSystem) {
        PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage();
        //J-
        packageDescrBuilder.name("com.rtzan.drools").newRule()
                .name("Group_01_Rule_01").lhs()
                .pattern("Customer").constraint("flagged == true && name == \"ana_01\"").end()
                .pattern().id("$customer", false)
                .end()
                .end()
                .rhs("$customer.setFlagged( false );")
            .end();
        //J+

        String rules = new DrlDumper().dump(packageDescrBuilder.getDescr());
        kieFileSystem.write("src/main/resources/rule-1.drl", rules);
    }

    private void addRules(KieServices kieServices, KieFileSystem kieFileSystem, Map<String, String> drlFilePaths) {
        for (Map.Entry<String, String> entry : drlFilePaths.entrySet()) {
            addRule(kieServices, kieFileSystem, entry.getKey(), entry.getValue());
        }
    }

    private void addRule(KieServices kieServices, KieFileSystem kieFileSystem, String drlFilePath, String drlFileContent) {
        System.out.println("<< file name = [" + drlFilePath + "]; file content = [" + drlFileContent + "]");
        kieFileSystem.write("src/main/resources/" + drlFilePath, getResource(kieServices, drlFileContent));
    }

    private Resource getResource(KieServices kieServices, String drlFileContent) {
        InputStream is = new ByteArrayInputStream(drlFileContent.getBytes());
        return kieServices.getResources().newInputStreamResource(is).setResourceType(ResourceType.DRL);
    }

}
