package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.ModifyService;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.SwingConstants;


public class ServiceButtonPanel extends ServiceContextsOptionsPanel {

    private JButton modifyServiceButton = null;
    private JButton addMethodButton = null;
    private JButton modifyResourcesButton = null;


    public ServiceButtonPanel(ServicesJTree tree) {
        super(tree);
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints2.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.add(getModifyServiceButton(), gridBagConstraints);
        this.add(getAddMethodButton(), gridBagConstraints1);
        this.add(getModifyResourcesButton(), gridBagConstraints2);
    }


    /**
     * This method initializes modifyServiceButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getModifyServiceButton() {
        if (this.modifyServiceButton == null) {
            this.modifyServiceButton = new JButton();
            this.modifyServiceButton.setText("Modify Service");
            this.modifyServiceButton.setIcon(IntroduceLookAndFeel.getModifyServiceSmallIcon());
            this.modifyServiceButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    DefaultMutableTreeNode tnode = ServiceButtonPanel.this.getTree().getCurrentNode();
                    if (tnode instanceof ServiceTypeTreeNode) {
                        ServiceTypeTreeNode node = (ServiceTypeTreeNode) tnode;
                        ModifyService comp = new ModifyService( new SpecificServiceInformation(node.getInfo(),
                            node.getServiceType()), false);
                        comp.pack();
                        comp.setVisible(true);
                    }
                }

            });
        }
        return this.modifyServiceButton;
    }


    /**
     * This method initializes addMethodButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddMethodButton() {
        if (this.addMethodButton == null) {
            this.addMethodButton = new JButton();
            this.addMethodButton.setText("Add Method");
            this.addMethodButton.setIcon(IntroduceLookAndFeel.getAddMethodIcon());
            this.addMethodButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode tnode = ServiceButtonPanel.this.getTree().getCurrentNode();
                    tnode = (DefaultMutableTreeNode) tnode.getChildAt(0);
                    if (tnode instanceof MethodsTypeTreeNode) {
                        MethodsPopUpMenu.addMethod((MethodsTypeTreeNode) tnode);
                    }
                }

            });
        }
        return this.addMethodButton;
    }


    /**
     * This method initializes modifyResourcesButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getModifyResourcesButton() {
        if (this.modifyResourcesButton == null) {
            this.modifyResourcesButton = new JButton();
            this.modifyResourcesButton.setText("Modify Resource Properties");
            this.modifyResourcesButton.setIcon(IntroduceLookAndFeel.getModifyResourcePropertiesIcon());
            this.modifyResourcesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // this first node will be the selected service context node
                    ServiceTypeTreeNode serviceNode = (ServiceTypeTreeNode) getTree().getCurrentNode();
                    // locate or create a resource properties node
                    ResourcePropertiesTypeTreeNode resourceNode = null;
                    if (serviceNode.getChildCount() >= 2
                        && serviceNode.getChildAt(1) instanceof ResourcePropertiesTypeTreeNode) {
                        resourceNode = (ResourcePropertiesTypeTreeNode) serviceNode.getChildAt(1);
                    } else {
                        ResourcePropertiesListType resourcePropsList = new ResourcePropertiesListType();
                        serviceNode.getServiceType().setResourcePropertiesList(resourcePropsList);
                        resourceNode = new ResourcePropertiesTypeTreeNode(serviceNode.getServiceType(), serviceNode.getModel(), serviceNode
                            .getInfo());
                        serviceNode.getModel().insertNodeInto(resourceNode, serviceNode, serviceNode.getChildCount());
                    }
                    ResourcePropertiesPopUpMenu.modifyResourceProperties(resourceNode);

                }

            });
        }
        return this.modifyResourcesButton;
    }

}
