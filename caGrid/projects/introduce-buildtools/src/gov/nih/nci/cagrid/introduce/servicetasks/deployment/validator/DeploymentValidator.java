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
package gov.nih.nci.cagrid.introduce.servicetasks.deployment.validator;

public abstract class DeploymentValidator {
    String baseDir = null;


    public DeploymentValidator(String baseDir) {
        this.baseDir = baseDir;
    }


    public abstract void validate() throws Exception;


    public String getBaseDir() {
        return this.baseDir;
    }

}
