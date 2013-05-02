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

import gov.nih.nci.cagrid.data.service.auditing.DataServiceAuditor;

import java.util.EventListener;

/** 
 *  AuditorAdditionListener
 *  Listens for additions to the auditors of a data service
 * 
 * @author David Ervin
 * 
 * @created May 21, 2007 11:57:48 AM
 * @version $Id: AuditorAdditionListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface AuditorAdditionListener extends EventListener {

    public void auditorAdded(DataServiceAuditor auditor, String auditorClass, String instanceName);
}
