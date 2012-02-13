package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class MethodsButtonPanel extends ServiceContextsOptionsPanel {

	private JButton addServiceButton = null;


	/**
	 * This method initializes
	 */
	public MethodsButtonPanel(ServicesJTree tree) {
		super(tree);
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		this.setLayout(new GridBagLayout());
		this.add(getAddMethodButton(), gridBagConstraints);

	}


	/**
	 * This method initializes addServiceButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddMethodButton() {
		if (addServiceButton == null) {
			addServiceButton = new JButton();
			addServiceButton.setText("Add Method");
			addServiceButton.setIcon(IntroduceLookAndFeel.getAddMethodIcon());
			addServiceButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					DefaultMutableTreeNode tnode = MethodsButtonPanel.this.getTree().getCurrentNode();
					if (tnode instanceof MethodsTypeTreeNode) {
						MethodsPopUpMenu.addMethod((MethodsTypeTreeNode) tnode);
					}
					ServicesJTree.getInstance().setServices(((MethodsTypeTreeNode) tnode).getInfo());

				}

			});
		}
		return addServiceButton;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
