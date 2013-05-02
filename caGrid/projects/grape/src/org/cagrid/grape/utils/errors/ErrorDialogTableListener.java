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
package org.cagrid.grape.utils.errors;

import java.util.EventListener;

/** 
 *  ErrorDialogTableListener
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Apr 13, 2007 9:43:11 AM
 * @version $Id: ErrorDialogTableListener.java,v 1.1 2007-04-13 18:11:01 dervin Exp $ 
 */
public interface ErrorDialogTableListener extends EventListener {

    public void showDetailsClicked(ErrorContainer container);
    
    
    public void showErrorClicked(ErrorContainer container);
}
