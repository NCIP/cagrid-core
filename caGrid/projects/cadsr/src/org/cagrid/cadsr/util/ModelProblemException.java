package org.cagrid.cadsr.util;

/** 
 *  ModelInspectionException
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Feb 8, 2008 10:32:39 AM
 * @version $Id: ModelProblemException.java,v 1.1 2009-01-07 04:45:41 oster Exp $ 
 */
public class ModelProblemException extends Exception {

    public ModelProblemException(String message) {
        super(message);
    }
    
    
    public ModelProblemException(Exception ex) {
        super(ex);
    }
    
    
    public ModelProblemException(String message, Exception ex) {
        super(message, ex);
    }
}
