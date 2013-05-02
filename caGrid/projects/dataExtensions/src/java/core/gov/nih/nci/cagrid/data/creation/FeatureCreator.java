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
package gov.nih.nci.cagrid.data.creation;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;

/** 
 *  FeatureCreator
 *  Base class for feature additions to the data service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 22, 2006 
 * @version $Id: FeatureCreator.java,v 1.2 2007-07-18 14:01:47 dervin Exp $ 
 */
public abstract class FeatureCreator {
	
	private ServiceInformation serviceInformation;
	private ServiceType mainService;

	public FeatureCreator(ServiceInformation info, ServiceType mainService) {
		this.serviceInformation = info;
		this.mainService = mainService;
	}
	
	
	protected ServiceInformation getServiceInformation() {
		return serviceInformation;
	}
	
	
	protected ServiceType getMainService() {
		return mainService;
	}
    
	
	public abstract void addFeature() throws CreationExtensionException; 
}
