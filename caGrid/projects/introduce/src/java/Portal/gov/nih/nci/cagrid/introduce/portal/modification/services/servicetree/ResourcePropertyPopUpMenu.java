package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties.ModifyResourcePropertiesPanel;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class ResourcePropertyPopUpMenu extends JPopupMenu {

	private JMenuItem removeResourcePropertyMenuItem = null;
	private ResourcePropertyTypeTreeNode node;
	private JMenuItem editResourcePropertyMenuItem = null;


	/**
	 * This method initializes
	 * 
	 */
	public ResourcePropertyPopUpMenu(ResourcePropertyTypeTreeNode node) {
		super();
		this.node = node;
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	public void initialize() {
	    this.removeAll();
		this.add(getRemoveResourcePropertyMenuItem());
		//only if this is the first service  and it has a file can you edit hte metadata
		if(node.getResourcePropertyType() !=null && node.getResourcePropertyType().isPopulateFromFile()){
			this.add(getEditResourcePropertyMenuItem());
		}
	}
	
	


	@Override
    public void show(Component invoker, int x, int y) {
	    initialize();
        super.show(invoker, x, y);
    }


    /**
	 * This method initializes removeResourcePropertyMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRemoveResourcePropertyMenuItem() {
		if (removeResourcePropertyMenuItem == null) {
			removeResourcePropertyMenuItem = new JMenuItem();
			removeResourcePropertyMenuItem.setIcon(IntroduceLookAndFeel.getRemoveResourcePropertyIcon());
			removeResourcePropertyMenuItem.setText("Remove Resource Property");
			removeResourcePropertyMenuItem.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					ResourcePropertiesTypeTreeNode parent = ((ResourcePropertiesTypeTreeNode) node.getParent());
				        CommonTools.removeResourceProperty(parent.getService(), ((ResourcePropertyType) node.getUserObject()).getQName());
				        parent.remove(node);  
				        ServicesJTree.getInstance().setServices(parent.getInfo());
				}
			});
		}
		return removeResourcePropertyMenuItem;
	}


	/**
	 * This method initializes editResourcePropertyMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	public JMenuItem getEditResourcePropertyMenuItem() {
		if (editResourcePropertyMenuItem == null) {
			editResourcePropertyMenuItem = new JMenuItem();
			editResourcePropertyMenuItem.setIcon(IntroduceLookAndFeel.getModifyResourcePropertyIcon());
			editResourcePropertyMenuItem.setText("Edit Resource Property");
			editResourcePropertyMenuItem
					.addMouseListener(new java.awt.event.MouseAdapter() {
						public void mousePressed(java.awt.event.MouseEvent e) {
							SpecificServiceInformation info = new SpecificServiceInformation(((ResourcePropertiesTypeTreeNode)node.getParent()).getInfo(),((ResourcePropertiesTypeTreeNode)node.getParent()).getService());
							try {
								ModifyResourcePropertiesPanel.viewEditResourceProperty(node.getResourcePropertyType(), info);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});
		}
		return editResourcePropertyMenuItem;
	}

}
