package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.service.Identifiable;
import gov.nih.nci.cagrid.introduce.beans.service.Lifetime;
import gov.nih.nci.cagrid.introduce.beans.service.ResourceFrameworkOptions;
import gov.nih.nci.cagrid.introduce.beans.service.Secure;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.ModifyService;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class ServicesPopUpMenu extends JPopupMenu {

    private JMenuItem addResourceMenuItem = null;
    private ServicesTypeTreeNode node;


    /**
     * This method initializes
     */
    public ServicesPopUpMenu(ServicesTypeTreeNode node) {
        super();
        this.node = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(getAddResourceMenuItem());

    }


    /**
     * This method initializes addResourceMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAddResourceMenuItem() {
        if (this.addResourceMenuItem == null) {
            this.addResourceMenuItem = new JMenuItem();
            this.addResourceMenuItem.setText("Add Service Context");
            this.addResourceMenuItem.setIcon(IntroduceLookAndFeel.getCreateServiceSmallIcon());
            this.addResourceMenuItem.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    ServicesPopUpMenu.addService(ServicesPopUpMenu.this.node);
                }

            });
        }
        return this.addResourceMenuItem;
    }


    public static void addService(ServicesTypeTreeNode node) {
        ServiceType service = new ServiceType();
        service.setMethods(new MethodsType());
        service.setResourcePropertiesList(new ResourcePropertiesListType());
        service.setResourceFrameworkOptions(new ResourceFrameworkOptions());
        service.getResourceFrameworkOptions().setLifetime(new Lifetime());
        service.getResourceFrameworkOptions().setIdentifiable(new Identifiable());
        service.getResourceFrameworkOptions().setSecure(new Secure());

        ModifyService comp = new ModifyService(new SpecificServiceInformation(node.getInfo(), service), true);
        comp.setVisible(true);

        if (!comp.wasClosed()) {
            CommonTools.addService(node.getInfo().getServices(), service);
            ServicesJTree.getInstance().setServices(node.getInfo());
        }
    }

}
