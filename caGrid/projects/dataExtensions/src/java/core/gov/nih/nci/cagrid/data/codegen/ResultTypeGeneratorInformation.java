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
package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;


/**
 * @author oster
 * @author dervin
 * 
 */
public class ResultTypeGeneratorInformation {
	private ServiceInformation serviceInfo;
	private ModelInformation modelInfo;


	public ResultTypeGeneratorInformation() {

	}


	public ResultTypeGeneratorInformation(ServiceInformation serviceInfo, ModelInformation modelInfo) {
		this.serviceInfo = serviceInfo;
		this.modelInfo = modelInfo;
	}


	public ModelInformation getModelInformation() {
		return this.modelInfo;
	}


	public void setModelInformation(ModelInformation modelInfo) {
		this.modelInfo = modelInfo;
	}


	public ServiceInformation getServiceInfo() {
		return this.serviceInfo;
	}


	public void setServiceInfo(ServiceInformation serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

}
