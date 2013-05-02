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
package gov.nih.nci.cagrid.sdkquery4.style.beanmap;

import java.util.EventListener;

/** 
 *  BeanTypeDiscoveryEventListener
 *  Listens for bean type discovery events
 * 
 * @author David Ervin
 * 
 * @created Jan 15, 2008 12:49:34 PM
 * @version $Id: BeanTypeDiscoveryEventListener.java,v 1.1 2008-01-16 17:05:31 dervin Exp $ 
 */
public interface BeanTypeDiscoveryEventListener extends EventListener {

    public void typeDiscoveryBegins(BeanTypeDiscoveryEvent e);
}
