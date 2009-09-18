package gov.nih.nci.cagrid.testing.system.deployment.story;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Story;

public abstract class ServiceStoryBase extends Story {
    
    private ServiceContainer container;

    public ServiceStoryBase(){
        this.container = null;
    }
    
    public ServiceStoryBase(ServiceContainer container){
        this.container = container;
    }

    public ServiceContainer getContainer() {
        return container;
    }

    public void setContainer(ServiceContainer container) {
        this.container = container;
    }

}
