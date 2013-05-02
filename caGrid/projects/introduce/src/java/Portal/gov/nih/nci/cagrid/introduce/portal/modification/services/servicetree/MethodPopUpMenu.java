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

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.methods.MethodViewer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class MethodPopUpMenu extends JPopupMenu {

    private JMenuItem removeMethodMenuItem = null;
    MethodTypeTreeNode node;
    private JMenuItem modifyMethodMenuItem = null;


    /**
     * This method initializes
     */
    public MethodPopUpMenu(MethodTypeTreeNode node) {
        super();
        this.node = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(getRemoveMethodMenuItem());
        this.add(getModifyMethodMenuItem());
    }


    /**
     * This method initializes removeMethodMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getRemoveMethodMenuItem() {
        if (removeMethodMenuItem == null) {
            removeMethodMenuItem = new JMenuItem();
            removeMethodMenuItem.setText("Remove Method");
            removeMethodMenuItem.setIcon(IntroduceLookAndFeel.getRemoveMethodIcon());
            removeMethodMenuItem.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);

                    MethodType removedMethod = (MethodType) node.getUserObject();
                    CommonTools.removeMethod(((MethodsTypeTreeNode) node.getParent()).getService().getMethods(),
                        removedMethod);
                    MethodsTypeTreeNode parent = ((MethodsTypeTreeNode) node.getParent());
                    parent.remove(node);
                    ServicesJTree.getInstance().setServices(((MethodsTypeTreeNode) parent).getInfo());
                    
                }
            });
        }
        return removeMethodMenuItem;
    }


    /**
     * This method initializes modifyMethodMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getModifyMethodMenuItem() {
        if (modifyMethodMenuItem == null) {
            modifyMethodMenuItem = new JMenuItem();
            modifyMethodMenuItem.setIcon(IntroduceLookAndFeel.getModifyMethodIcon());
            modifyMethodMenuItem.setText("Modify Method");
            modifyMethodMenuItem.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    MethodViewer viewer = new MethodViewer(node.getMethod(), node.getInfo());
                    viewer.setVisible(true);

                }
            });
        }
        return modifyMethodMenuItem;
    }

}
