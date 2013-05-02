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
package gov.nih.nci.cagrid.data.ui.auditors;

import java.util.EventListener;

/** 
 *  AuditorPropertyChangeListener
 *  Listens for property edit events
 * 
 * @author David Ervin
 * 
 * @created May 22, 2007 10:33:46 AM
 * @version $Id: AuditorPropertyChangeListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface AuditorPropertyChangeListener extends EventListener {

    public void propertyValueEdited(String key, String newValue);
}
