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
package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;





import javax.swing.JPanel;


public class ServiceContextsOptionsPanel extends JPanel {

	public ServicesJTree tree;

	public ServiceContextsOptionsPanel() {
		super();
	}

	public ServiceContextsOptionsPanel(ServicesJTree tree) {
		this.tree = tree;
	}

	public ServicesJTree getTree() {
		return tree;
	}


}
