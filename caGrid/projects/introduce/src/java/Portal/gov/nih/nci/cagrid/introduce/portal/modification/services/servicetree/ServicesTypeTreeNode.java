package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.PopupTreeNode;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


/**
 * Node for representing namepspace
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @created Nov 22, 2004
 * @version $Id: ServicesTypeTreeNode.java,v 1.2 2008-09-03 01:20:36 hastings Exp $
 */
public class ServicesTypeTreeNode extends DefaultMutableTreeNode implements PopupTreeNode {

	private ServicesPopUpMenu menu;
	private DefaultTreeModel model;
	private ServiceInformation info;


	public ServicesTypeTreeNode(ServiceInformation info) {
		super();
		this.info = info;
		menu = new ServicesPopUpMenu(this);
		this.setUserObject("Services");
	}


	public void setModel(DefaultTreeModel model) {
		this.model = model;
	}


	public DefaultTreeModel getModel() {
		return this.model;
	}


	public ServiceInformation getInfo() {
		return this.info;
	}


	public void setServices(ServiceInformation info, DefaultTreeModel model) {
		this.model = model;
		this.info = info;
		if (info.getServices().getService() != null) {
			// starting from one because we want to skip service 0, service 0 is
			// the main service.
			for (int i = 0; i < info.getServices().getService().length; i++) {
				ServiceTypeTreeNode newNode = new ServiceTypeTreeNode(info.getServices().getService(i), info, model);
				model.insertNodeInto(newNode, this, this.getChildCount());
			}
		}
	}


	public void removeResource(ServiceTypeTreeNode node) {
		ServiceType[] newResourceProperty = new ServiceType[info.getServices().getService().length - 1];
		newResourceProperty[0] = info.getServices().getService(0);
		int newResourcePropertyCount = 1;
		for (int i = 0; i < this.getChildCount(); i++) {
			ServiceTypeTreeNode treenode = (ServiceTypeTreeNode) this.getChildAt(i);
			if (!treenode.equals(node)) {
				newResourceProperty[newResourcePropertyCount++] = (ServiceType) treenode.getUserObject();
			}
		}

		info.getServices().setService(newResourceProperty);

		model.removeNodeFromParent(node);
	}


	public String toString() {
		return this.getUserObject().toString();
	}



	public JPopupMenu getPopUpMenu() {
		return this.menu;
	}
}
