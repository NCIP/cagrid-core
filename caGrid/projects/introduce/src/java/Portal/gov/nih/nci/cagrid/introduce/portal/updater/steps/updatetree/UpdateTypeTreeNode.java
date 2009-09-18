package gov.nih.nci.cagrid.introduce.portal.updater.steps.updatetree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Abstract tree node representing a configureation panel
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * 
 * @created Nov 22, 2004
 * @version $Id: NamespacesTypeTreeNode.java,v 1.4 2006/04/05 18:17:49 hastings
 *          Exp $
 */
public abstract class UpdateTypeTreeNode extends DefaultMutableTreeNode {

	private DefaultTreeModel model;
	private String name;

	public UpdateTypeTreeNode(String displayName, DefaultTreeModel model) {
		super();
		this.model = model;
		this.name = displayName;
		this.setUserObject(displayName);
	}

	public String toString() {
		return this.getUserObject().toString();
	}

	public void setModel( DefaultTreeModel model) {
		this.model = model;
		this.initialize();
	}
	
	public DefaultTreeModel getModel() {
		return model;
	}
	
	public String getName() {
		return this.name;
	}

	 /* This method must be implemented and must pupulate itself and any of it's
	 * children
	 */
	public abstract void initialize();
}
