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
