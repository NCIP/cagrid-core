package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;





import javax.swing.JPanel;


public class ServiceContextsOptionsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2377885216463682761L;
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
