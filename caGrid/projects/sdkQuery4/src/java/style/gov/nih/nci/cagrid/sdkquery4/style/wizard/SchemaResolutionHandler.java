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
package gov.nih.nci.cagrid.sdkquery4.style.wizard;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.util.EventListener;

/** 
 *  SchemaResolutionHandler
 *  Listens for schema resolution button clicks and handles the operation
 * 
 * @author David Ervin
 * 
 * @created Jan 23, 2008 11:00:40 AM
 * @version $Id: SchemaResolutionHandler.java,v 1.1 2008-01-23 19:59:19 dervin Exp $ 
 */
public interface SchemaResolutionHandler extends EventListener {
    
    public SchemaResolutionStatus resolveSchemaForPackage(ServiceInformation serviceInfo, String packageName);
}
