package org.cagrid.grape;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.cagrid.grape.configuration.Grid;

public class TargetGridEditor extends ConfigurationBasePanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 * @throws Exception 
	 */
	public TargetGridEditor(ConfigurationDescriptorTreeNode treeNode, Object conf)
			throws Exception {
		super(treeNode, conf);
		initialize();

//		HashMap<String, ConfigurationManager> configurationManagers = GAARDSApplication.getConfigurationManagers();
		
		Set<String> configurationNames = GAARDSApplication.getContext().getConfigurationManager().getConfigurationNames();
		for (String configurationName : configurationNames) {
			if ("default".equalsIgnoreCase(configurationName)) {
				continue;
			}
			Grid grid = GAARDSApplication.getContext().getConfigurationManager().getConfigurationGrid(configurationName);
			
			TargetGridBaseTreeNode gridNode = new TargetGridBaseTreeNode(
					treeNode.getConfigurationWindow(), treeNode.getTree(), grid);
			treeNode.add(gridNode);
			gridNode.processConfigurationGroups();
		}
		
		
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		JLabel logo = null;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.gridy = 0;
		logo = new JLabel(LookAndFeel.getApplicationLogo());
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.anchor = GridBagConstraints.CENTER;
		gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints1.gridy = 2;
		JLabel jLabel = new JLabel();
		jLabel.setText("Target Grid(s)");
		jLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		this.setLayout(new GridBagLayout());
		this.add(jLabel, gridBagConstraints1);
		this.add(logo, gridBagConstraints);

	}

	public void showErrorMessage(String title, String msg) {
		showErrorMessage(title, new String[] { msg });
	}

	public void showErrorMessage(String title, String[] msg) {
		JOptionPane.showMessageDialog(this, msg, title,
				JOptionPane.ERROR_MESSAGE);
	}

}
