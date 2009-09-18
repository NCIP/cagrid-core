package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties.ModifyResourcePropertiesComponent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cagrid.grape.GridApplication;


public class ResourcePropertiesPopUpMenu extends JPopupMenu {

    private ResourcePropertiesTypeTreeNode node;
    private JMenuItem modifyResourcePropetiesMenuItem = null;


    /**
     * This method initializes
     */
    public ResourcePropertiesPopUpMenu(ResourcePropertiesTypeTreeNode node) {
        super();
        this.node = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(getModifyResourcePropetiesMenuItem());

    }


    /**
     * This method initializes modifyResourcePropetiesMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getModifyResourcePropetiesMenuItem() {
        if (this.modifyResourcePropetiesMenuItem == null) {
            this.modifyResourcePropetiesMenuItem = new JMenuItem();
            this.modifyResourcePropetiesMenuItem.setText("Modify Resource Properties");
            this.modifyResourcePropetiesMenuItem.setIcon(IntroduceLookAndFeel.getModifyResourcePropertiesIcon());
            this.modifyResourcePropetiesMenuItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    ResourcePropertiesPopUpMenu.modifyResourceProperties(ResourcePropertiesPopUpMenu.this.node);
                }

            });
        }
        return this.modifyResourcePropetiesMenuItem;
    }


    public static void modifyResourceProperties(ResourcePropertiesTypeTreeNode node) {
    	SpecificServiceInformation info = new SpecificServiceInformation(node.getInfo(), node.getService());
        ModifyResourcePropertiesComponent comp = new ModifyResourcePropertiesComponent(info, false);
        comp.setSize(800, 500);
        GridApplication.getContext().showDialog(comp);
        //rebuild the tree
        ServicesJTree.getInstance().setServices(node.getInfo());    
    }

}
