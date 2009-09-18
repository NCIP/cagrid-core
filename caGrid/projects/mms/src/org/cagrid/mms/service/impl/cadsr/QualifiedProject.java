package org.cagrid.mms.service.impl.cadsr;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.system.applicationservice.ApplicationService;


public class QualifiedProject {
    private ApplicationService sourceAppServ;
    private Project projectPrototype;


    public ApplicationService getSourceAppServ() {
        return sourceAppServ;
    }


    public void setSourceAppServ(ApplicationService sourceAppServ) {
        this.sourceAppServ = sourceAppServ;
    }


    public Project getProjectPrototype() {
        return projectPrototype;
    }


    public void setProjectPrototype(Project projectPrototype) {
        this.projectPrototype = projectPrototype;
    }


    public QualifiedProject(ApplicationService sourceAppServ, Project projectPrototype) {
        this.sourceAppServ = sourceAppServ;
        this.projectPrototype = projectPrototype;
    }

}
