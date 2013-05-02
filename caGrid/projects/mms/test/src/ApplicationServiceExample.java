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
import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.util.Iterator;
import java.util.List;


public class ApplicationServiceExample {

    public static void main(String[] args) {
        try {
            ApplicationService appService = ApplicationServiceProvider
                .getApplicationServiceFromUrl("http://cadsrapi.nci.nih.gov/cadsrapi40/");

            List rList = appService.search(Project.class, new Project());
            for (Iterator resultsIterator = rList.iterator(); resultsIterator.hasNext();) {
                Project project = (Project) resultsIterator.next();
                System.out.println(project.getShortName());
                System.out.println(project.getVersion());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
