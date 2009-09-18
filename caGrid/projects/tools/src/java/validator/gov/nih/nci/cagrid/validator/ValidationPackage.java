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
