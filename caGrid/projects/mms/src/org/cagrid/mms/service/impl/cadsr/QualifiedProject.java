/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
