package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.service.Identifiable;
import gov.nih.nci.cagrid.introduce.beans.service.Lifetime;
import gov.nih.nci.cagrid.introduce.beans.service.Main;
import gov.nih.nci.cagrid.introduce.beans.service.Notification;
import gov.nih.nci.cagrid.introduce.beans.service.Persistent;
import gov.nih.nci.cagrid.introduce.beans.service.ResourceFrameworkOptions;
import gov.nih.nci.cagrid.introduce.beans.service.ResourcePropertyManagement;
import gov.nih.nci.cagrid.introduce.beans.service.Secure;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;
import gov.nih.nci.cagrid.introduce.beans.service.Singleton;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;
import java.util.StringTokenizer;


public class AddServiceContextStep extends BaseStep {
    private TestCaseInfo tci;


    public AddServiceContextStep(TestCaseInfo tci, boolean build) throws Exception {
        super(tci.getDir(), build);
        this.tci = tci;
    }


    public void runStep() throws Throwable {
        System.out.println("Adding a service context:" + tci.getName());

        ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
            + tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
        ServicesType servicesType = introService.getServices();

        ServiceType service = new ServiceType();
        service.setName(tci.getName());
        service.setNamespace(tci.getNamespace());
        service.setPackageName(tci.getPackageName());
        service.setResourceFrameworkOptions(new ResourceFrameworkOptions());
     // for each resource propertyOption set it on the service
        String resourceOptionsList = tci.getResourceFrameworkType();
        StringTokenizer strtok = new StringTokenizer(resourceOptionsList, ",", false);
        while (strtok.hasMoreElements()) {
            String option = strtok.nextToken();
            if (option.equals(IntroduceConstants.INTRODUCE_MAIN_RESOURCE)) {
                service.getResourceFrameworkOptions().setMain(new Main());
            } else if (option.equals(IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE)) {
                service.getResourceFrameworkOptions().setSingleton(new Singleton());
            } else if (option.equals(IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE)) {
                service.getResourceFrameworkOptions().setIdentifiable(new Identifiable());
            } else if (option.equals(IntroduceConstants.INTRODUCE_LIFETIME_RESOURCE)) {
                service.getResourceFrameworkOptions().setLifetime(new Lifetime());
            } else if (option.equals(IntroduceConstants.INTRODUCE_PERSISTENT_RESOURCE)) {
                service.getResourceFrameworkOptions().setPersistent(new Persistent());
            } else if (option.equals(IntroduceConstants.INTRODUCE_NOTIFICATION_RESOURCE)) {
                service.getResourceFrameworkOptions().setNotification(new Notification());
            } else if (option.equals(IntroduceConstants.INTRODUCE_SECURE_RESOURCE)) {
                service.getResourceFrameworkOptions().setSecure(new Secure());
            } else if (option.equals(IntroduceConstants.INTRODUCE_RESOURCEPROPETIES_RESOURCE)) {
                service.getResourceFrameworkOptions().setResourcePropertyManagement(
                    new ResourcePropertyManagement());
            }
        }

        // add new service to array in bean
        // this seems to be a wierd way be adding things....
        ServiceType[] newMethods;
        int newLength = 0;
        if (servicesType != null && servicesType.getService() != null) {
            newLength = servicesType.getService().length + 1;
            newMethods = new ServiceType[newLength];
            System.arraycopy(servicesType.getService(), 0, newMethods, 0, servicesType.getService().length);
        } else {
            newLength = 1;
            newMethods = new ServiceType[newLength];
        }
        ServicesType newservicesType = new ServicesType();
        newMethods[newLength - 1] = service;
        newservicesType.setService(newMethods);
        introService.setServices(newservicesType);

        Utils.serializeDocument(getBaseDir() + File.separator + tci.getDir() + File.separator + "introduce.xml",
            introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);

        try {
            SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + tci.getDir()));
            sync.sync();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        buildStep();
    }

}
