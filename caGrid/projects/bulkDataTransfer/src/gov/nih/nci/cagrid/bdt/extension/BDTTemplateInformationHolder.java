package gov.nih.nci.cagrid.bdt.extension;

import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;

public class BDTTemplateInformationHolder {
	
	private SpecificServiceInformation ssi;
	private MethodType method;
	
	public BDTTemplateInformationHolder(SpecificServiceInformation ssi, MethodType method){
		this.ssi = ssi;
		this.method = method;
	}

	public SpecificServiceInformation getSsi() {
		return ssi;
	}

	public void setSsi(SpecificServiceInformation ssi) {
		this.ssi = ssi;
	}

	public MethodType getMethod() {
		return method;
	}

	public void setMethod(MethodType method) {
		this.method = method;
	}

}
