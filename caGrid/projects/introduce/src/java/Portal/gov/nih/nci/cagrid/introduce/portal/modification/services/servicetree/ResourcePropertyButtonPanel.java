package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties.ModifyResourcePropertiesPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;


public class ResourcePropertyButtonPanel extends ServiceContextsOptionsPanel {

    private JButton removeResourcePropertyButton = null;
    private JButton editButton = null;


    /**
     * This method initializes
     */
    public ResourcePropertyButtonPanel(ServicesJTree tree) {
        super(tree);
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.add(getRemoveResourcePropertyButton(), gridBagConstraints);
        this.add(getEditButton(), gridBagConstraints1);

    }


    public void updateView() {
        if (ResourcePropertyButtonPanel.this.getTree().getCurrentNode() != null
            && ResourcePropertyButtonPanel.this.getTree().getCurrentNode() instanceof ResourcePropertyTypeTreeNode) {
            ResourcePropertyTypeTreeNode node = (ResourcePropertyTypeTreeNode) ResourcePropertyButtonPanel.this
                .getTree().getCurrentNode();
            if (node.getResourcePropertyType().isPopulateFromFile()) {
                getEditButton().setVisible(true);
                return;
            }
        } 
        getEditButton().setVisible(false);
    }


    public void setCanModify(boolean canModify) {
        this.getRemoveResourcePropertyButton().setEnabled(canModify);
    }


    /**
     * This method initializes addServiceButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveResourcePropertyButton() {
        if (removeResourcePropertyButton == null) {
            removeResourcePropertyButton = new JButton();
            removeResourcePropertyButton.setText("Remove Resource Property");
            removeResourcePropertyButton.setIcon(IntroduceLookAndFeel.getRemoveResourcePropertyIcon());
            removeResourcePropertyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    DefaultMutableTreeNode tnode = ResourcePropertyButtonPanel.this.getTree().getCurrentNode();
                    if (tnode instanceof ResourcePropertyTypeTreeNode) {
                        ResourcePropertiesTypeTreeNode parent = ((ResourcePropertiesTypeTreeNode) tnode.getParent());
                        CommonTools.removeResourceProperty(parent.getService(), ((ResourcePropertyType) tnode
                            .getUserObject()).getQName());
                        parent.remove(tnode);
                        ServicesJTree.getInstance().setServices(parent.getInfo());
                    }

                }

            });
        }
        return removeResourcePropertyButton;
    }


    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    public JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setIcon(IntroduceLookAndFeel.getModifyResourcePropertyIcon());
            editButton.setText("Edit Resource Property");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ResourcePropertyTypeTreeNode node = (ResourcePropertyTypeTreeNode) ResourcePropertyButtonPanel.this
                        .getTree().getCurrentNode();
                    SpecificServiceInformation info = new SpecificServiceInformation(
                        ((ResourcePropertiesTypeTreeNode) node.getParent()).getInfo(),
                        ((ResourcePropertiesTypeTreeNode) node.getParent()).getService());
                    try {
                        ModifyResourcePropertiesPanel.viewEditResourceProperty(node.getResourcePropertyType(), info);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        return editButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
