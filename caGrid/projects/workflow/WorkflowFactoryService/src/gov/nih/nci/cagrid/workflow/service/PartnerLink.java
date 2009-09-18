package gov.nih.nci.cagrid.workflow.service;

import javax.xml.namespace.QName;

public class PartnerLink {
	public String name;
    public QName linkType;
    public String myRole;
    public String partnerRole;
    
    public String toString() {
    	String returnString = name + " " + linkType.toString() + " " + myRole + " " + partnerRole;
    	return returnString;
    }

	public QName getLinkType() {
		return linkType;
	}

	public void setLinkType(QName linkType) {
		this.linkType = linkType;
	}

	public String getMyRole() {
		return myRole;
	}

	public void setMyRole(String myRole) {
		this.myRole = myRole;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPartnerRole() {
		return partnerRole;
	}

	public void setPartnerRole(String partnerRole) {
		this.partnerRole = partnerRole;
	}
}
