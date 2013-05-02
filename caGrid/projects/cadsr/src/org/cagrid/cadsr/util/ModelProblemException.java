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
