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
package gov.nih.nci.cagrid.validator;

import java.util.List;

import gov.nih.nci.cagrid.testing.system.haste.Story;
import gov.nih.nci.cagrid.tests.core.beans.validation.Schedule;

/** 
 *  ValidationPackage
 *  Container class for a deployment validation
 * 
 * @author David Ervin
 * 
 * @created Aug 27, 2007 3:05:03 PM
 * @version $Id: ValidationPackage.java,v 1.2 2008-11-12 23:36:16 jpermar Exp $ 
 */
public class ValidationPackage {

    private List<Story> validationStories;
    private Schedule validationSchedule;
    
    public ValidationPackage(List<Story> validationStories, Schedule validationSchedule) {
        this.validationStories = validationStories;
        this.validationSchedule = validationSchedule;
    }
    
    
    public List<Story> getValidationStories() {
        return this.validationStories;
    }
    
    
    public Schedule getValidationSchedule() {
        return validationSchedule;
    }
}
