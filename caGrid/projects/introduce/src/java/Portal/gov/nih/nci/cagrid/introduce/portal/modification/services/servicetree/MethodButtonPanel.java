package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.methods.MethodViewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class MethodButtonPanel extends ServiceContextsOptionsPanel {

	private JButton modifyMethodButton = null;
	private JButton removeButton = null;


	/**
	 * This method initializes
	 */
	public MethodButtonPanel(ServicesJTree tree) {
		super(tree);
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
		gridBagConstraints3.gridy = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		this.setLayout(new GridBagLayout());
		this.add(getModifyMethodButton(), gridBagConstraints);
		this.add(getRemoveButton(), gridBagConstraints3);
	}


	public void setCanModify(boolean canModify) {
		this.getModifyMethodButton().setEnabled(canModify);
	}


	/**
	 * This method initializes addServiceButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getModifyMethodButton() {
		if (modifyMethodButton == null) {
		    modifyMethodButton = new JButton();
		    modifyMethodButton.setText("Modify Method");
		    modifyMethodButton.setIcon(IntroduceLookAndFeel.getModifyMethodIcon());
		    modifyMethodButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					DefaultMutableTreeNode tnode = MethodButtonPanel.this.getTree().getCurrentNode();
					if (tnode instanceof MethodTypeTreeNode) {
					    MethodViewer viewer = new MethodViewer(((MethodTypeTreeNode) tnode).getMethod(), ((MethodTypeTreeNode) tnode).getInfo());
	                    viewer.setVisible(true);
					}

				}

			});
		}
		return modifyMethodButton;
	}


	/**
	 * This method initializes removeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove Method");
			removeButton.setIcon(IntroduceLookAndFeel.getRemoveMethodIcon());
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DefaultMutableTreeNode tnode = MethodButtonPanel.this.getTree().getCurrentNode();
					if (tnode instanceof MethodTypeTreeNode) {
					    MethodType removedMethod = (MethodType) tnode.getUserObject();
	                    CommonTools.removeMethod(((MethodsTypeTreeNode) tnode.getParent()).getService().getMethods(),
	                        removedMethod);
	                    MethodsTypeTreeNode parent = ((MethodsTypeTreeNode) tnode.getParent());
	                    parent.remove(tnode);
	                    ServicesJTree.getInstance().setServices(parent.getInfo());
					}

				}
			});
		}
		return removeButton;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
