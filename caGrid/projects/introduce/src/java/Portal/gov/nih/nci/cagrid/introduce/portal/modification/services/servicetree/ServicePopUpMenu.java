package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.ModifyService;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class ServicePopUpMenu extends JPopupMenu {

    ServiceTypeTreeNode node;
    private JMenuItem modificationMenuItem = null;


    /**
     * This method initializes
     */
    public ServicePopUpMenu(ServiceTypeTreeNode node) {
        super();
        this.node = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(getModificationMenuItem());
    }


    /**
     * This method initializes modificationMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getModificationMenuItem() {
        if (this.modificationMenuItem == null) {
            this.modificationMenuItem = new JMenuItem();
            this.modificationMenuItem.setText("Modify Service");
            this.modificationMenuItem.setIcon(IntroduceLookAndFeel.getModifyServiceSmallIcon());
            this.modificationMenuItem.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);

                    ModifyService comp = new ModifyService(new SpecificServiceInformation(
                        ServicePopUpMenu.this.node.getInfo(), ServicePopUpMenu.this.node.getServiceType()), false);
                    comp.pack();
                    comp.setVisible(true);
                }

            });
        }
        return this.modificationMenuItem;
    }
}
