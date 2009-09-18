package org.cagrid.grape;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.cagrid.grape.configuration.GeneralConfiguration;
import org.cagrid.grape.configuration.Properties;
import org.cagrid.grape.configuration.Property;
import org.cagrid.grape.model.ConfigurationEditor;


public class GeneralConfigurationEditor extends ConfigurationBasePanel {

	private static final long serialVersionUID = 1L;

	private JPanel titlePanel = null;

	private JLabel titleLabel = null;

	private JLabel icon = null;

	private JPanel propertyDescriptionPanel = null;

	private JScrollPane jScrollPane = null;

	private JTextArea propertyDescription = null;

	private Logger log;


	/**
	 * This is the default constructor
	 */
	public GeneralConfigurationEditor(ConfigurationDescriptorTreeNode treeNode, Object conf) {
		super(treeNode, conf);
		initialize();
		log = Logger.getLogger(this.getClass().getName());
		Properties p = getGeneralConfiguration().getProperties();
		if (p != null) {
			Property[] prop = p.getProperty();
			if (prop != null) {
				for (int i = 0; i < prop.length; i++) {
					try {
						ConfigurationEditor editor = new ConfigurationEditor();
						editor.setDisplayName(prop[i].getName());
						editor.setConfigurationEditorPanel(GeneralConfigurationPropertyEditor.class.getName());
						this.addEditor(editor, prop[i]);
					} catch (Exception e) {
						log.error("Error creating the property editor for the property " + prop[i].getName() + " !!!");
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}


	public GeneralConfiguration getGeneralConfiguration() {
		return (GeneralConfiguration) this.getConfigurationObject();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.fill = GridBagConstraints.BOTH;
		gridBagConstraints21.weightx = 1.0D;
		gridBagConstraints21.weighty = 1.0D;
		gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints21.gridy = 2;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		this.setSize(500, 400);
		this.setLayout(new GridBagLayout());
		this.add(getTitlePanel(), gridBagConstraints);
		this.add(getPropertyDescriptionPanel(), gridBagConstraints21);
	}


	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 0.0D;
			gridBagConstraints2.gridy = 0;
			icon = new JLabel(LookAndFeel.getLogoNoText22x22());
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridx = 1;
			titleLabel = new JLabel();
			titleLabel.setText(getGeneralConfiguration().getName());
			titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.add(icon, gridBagConstraints2);
			titlePanel.add(titleLabel, gridBagConstraints1);
		}
		return titlePanel;
	}


	/**
	 * This method initializes propertyDescriptionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPropertyDescriptionPanel() {
		if (propertyDescriptionPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			propertyDescriptionPanel = new JPanel();
			propertyDescriptionPanel.setLayout(new GridBagLayout());
			propertyDescriptionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Description",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
			propertyDescriptionPanel.add(getJScrollPane(), gridBagConstraints5);
		}
		return propertyDescriptionPanel;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getPropertyDescription());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes propertyDescription
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getPropertyDescription() {
		if (propertyDescription == null) {
			propertyDescription = new JTextArea();
			propertyDescription.setLineWrap(true);
			propertyDescription.setFont(new Font("Arial", Font.PLAIN, 10));
			propertyDescription.setEditable(false);
			propertyDescription.setText(getGeneralConfiguration().getDescription());
		}
		return propertyDescription;
	}

}
