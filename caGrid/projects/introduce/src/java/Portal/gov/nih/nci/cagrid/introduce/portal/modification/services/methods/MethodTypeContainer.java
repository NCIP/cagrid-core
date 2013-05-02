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
package gov.nih.nci.cagrid.introduce.portal.modification.services.methods;

import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

public class MethodTypeContainer implements Comparable {
	private MethodType method;


	public MethodType getMethod() {
		return method;
	}


	public void setMethod(MethodType method) {
		this.method = method;
	}


	public MethodTypeContainer(MethodType method) {
		this.method = method;
	}


	public String toString() {
		return CommonTools.methodTypeToString(this.method);
	}


	public int compareTo(Object arg0) {
		MethodTypeContainer mtc = (MethodTypeContainer) (arg0);
		if (getMethod().isIsImported() && mtc.getMethod().isIsImported()) {
			return 100 + getMethod().getName().compareTo(mtc.getMethod().getName());
		} else if (!getMethod().isIsImported() && !mtc.getMethod().isIsImported()) {
			return getMethod().getName().compareTo(mtc.getMethod().getName());
		} else if (this.getMethod().isIsImported()) {
			return 100;
		} else if (!this.getMethod().isIsImported()) {
			return 0;
		}
		return 0;
	}
}
