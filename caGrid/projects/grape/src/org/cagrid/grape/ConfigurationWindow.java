package org.cagrid.grape;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import org.apache.log4j.Logger;


public class ConfigurationWindow extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel treePanel = null;

	private JPanel displayPanel = null;

	private JScrollPane jScrollPane = null;

	private JTree configurationTree = null;

	private CardLayout displayLayout;

	private JSplitPane jSplitPane = null;

	private JPanel buttonPanel = null;

	private JButton applyButton = null;

	private JButton cancelButton = null;

	private Logger log;


	/**
	 * @param owner
	 */
	public ConfigurationWindow(Frame owner) throws Exception {
		super(owner);
		setModal(false);
		this.log = Logger.getLogger(this.getClass().getName());
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() throws Exception {
		this.setSize(300, 200);
		this.setTitle("Preferences");
		this.setContentPane(getJContentPane());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() throws Exception {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}


	/**
	 * This method initializes treePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTreePanel() throws Exception {
		if (treePanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			treePanel = new JPanel();
			treePanel.setLayout(new GridBagLayout());
			treePanel.add(getJScrollPane(), gridBagConstraints2);
		}
		return treePanel;
	}


	/**
	 * This method initializes displayPanel
	 * 
	 * @return javax.swing.JPanel
	 */

	private JPanel getDisplayPanel() {
		if (displayPanel == null) {
			displayLayout = new CardLayout();
			displayPanel = new JPanel(displayLayout);
		}
		return displayPanel;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() throws Exception {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getConfigurationTree());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes configurationTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getConfigurationTree() throws Exception {
		if (configurationTree == null) {
			configurationTree = new ConfigurationTree(this);
		}
		return configurationTree;
	}


	protected void addDisplayPanel(String name, JPanel panel) {
		displayPanel.add(name, panel);
	}


	protected void showDisplayPanel(String name) {
		displayLayout.show(displayPanel, name);
		validate();
	}


	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() throws Exception {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(200);
			getDisplayPanel();
			jSplitPane.setLeftComponent(getTreePanel());
			jSplitPane.setRightComponent(getDisplayPanel());

		}
		return jSplitPane;
	}


	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getApplyButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes applyButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			applyButton = new JButton();
			applyButton.setText("Save");
			final ConfigurationWindow win = this;
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						GridApplication.getContext().getConfigurationManager().saveAll();
						dispose();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(win,
							"An unexpected error occurred applying you configuration changes!!!", "Unexpected Error",
							JOptionPane.ERROR_MESSAGE);
						log.error(ex.getMessage(), ex);
					}
				}
			});
		}
		return applyButton;
	}


	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}
}
