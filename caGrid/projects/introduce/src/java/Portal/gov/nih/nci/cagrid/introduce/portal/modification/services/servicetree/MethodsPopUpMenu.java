package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.methods.MethodViewer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.xml.namespace.QName;


public class MethodsPopUpMenu extends JPopupMenu {

    private JMenuItem addMethodMenuItem = null;
    MethodsTypeTreeNode node;


    /**
     * This method initializes
     */
    public MethodsPopUpMenu(MethodsTypeTreeNode node) {
        super();
        this.node = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(getAddMethodMenuItem());

    }


    /**
     * This method initializes addMethodMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAddMethodMenuItem() {
        if (addMethodMenuItem == null) {
            addMethodMenuItem = new JMenuItem();
            addMethodMenuItem.setText("Add Method");
            addMethodMenuItem.setIcon(IntroduceLookAndFeel.getAddMethodIcon());
            addMethodMenuItem.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    MethodsPopUpMenu.addMethod(MethodsPopUpMenu.this.node);
                }
            });
        }
        return addMethodMenuItem;
    }


    public static void addMethod(MethodsTypeTreeNode node) {
        MethodType method = new MethodType();
        method.setName("newMethod");
        MethodTypeOutput output = new MethodTypeOutput();
        output.setQName(new QName("", "void"));
        method.setOutput(output);

        MethodViewer viewer = new MethodViewer(method, node.getInfo());
        viewer.setVisible(true);

        if (!viewer.wasClosed()) {
                CommonTools.addMethod(node.getService(), method);
                ServicesJTree.getInstance().setServices(node.getInfo());
        }
    }

}
