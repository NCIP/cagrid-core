package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.cagrid.grape.configuration.GeneralConfiguration;
import org.cagrid.grape.configuration.Properties;
import org.cagrid.grape.configuration.Property;
import org.cagrid.grape.configuration.Values;


public class GeneralConfigurationPanel extends ConfigurationBasePanel {

	private static final long serialVersionUID = 1L;

	private JPanel titlePanel = null;

	private JLabel titleLabel = null;

	private JLabel icon = null;

	private Map<String, Property> properties;

	private JPanel propertiesPanel = null;

	private JPanel selectorPanel = null;

	private JComboBox propertySelector = null;

	private JPanel propertyDescriptionPanel = null;

	private JScrollPane jScrollPane = null;

	private JTextArea propertyDescription = null;

	private JPanel valuesPanel = null;

	private JList values = null;

	private DefaultListModel valuesModel = null;

	private JPanel actionPanel = null;

	private JTextField valueToAdd = null;

	private JButton addButton = null;

	private JButton removeButton = null;

	private JScrollPane jScrollPane1 = null;

	private JPanel priorityPanel = null;

	private JButton increaseButton = null;

	private JButton decreaseButton = null;


	/**
	 * This is the default constructor
	 */
	public GeneralConfigurationPanel(ConfigurationDescriptorTreeNode treeNode, Object conf) {
		super(treeNode, conf);
		this.properties = new HashMap<String, Property>();
		initialize();
	}


	public GeneralConfiguration getGeneralConfiguration() {
		return (GeneralConfiguration) this.getConfigurationObject();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.weightx = 1.0D;
		gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints12.gridy = 4;
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.weighty = 1.0D;
		gridBagConstraints31.fill = GridBagConstraints.BOTH;
		gridBagConstraints31.gridy = 3;
		gridBagConstraints31.weightx = 1.0D;
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.weightx = 1.0D;
		gridBagConstraints21.weighty = 0.0D;
		gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints21.gridy = 2;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.weightx = 1.0D;
		gridBagConstraints11.weighty = 0.0D;
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.gridy = 1;
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
		this.add(getPropertiesPanel(), gridBagConstraints11);
		this.add(getPropertyDescriptionPanel(), gridBagConstraints21);
		this.add(getValuesPanel(), gridBagConstraints31);
		this.add(getActionPanel(), gridBagConstraints12);
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
	 * This method initializes propertiesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPropertiesPanel() {
		if (propertiesPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.gridy = 0;
			propertiesPanel = new JPanel();
			propertiesPanel.setLayout(new GridBagLayout());
			propertiesPanel.add(getSelectorPanel(), gridBagConstraints3);
		}
		return propertiesPanel;
	}


	/**
	 * This method initializes selectorPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSelectorPanel() {
		if (selectorPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.weightx = 1.0;
			selectorPanel = new JPanel();
			selectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Property",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
			selectorPanel.setLayout(new GridBagLayout());
			selectorPanel.add(getPropertySelector(), gridBagConstraints4);
		}
		return selectorPanel;
	}


	/**
	 * This method initializes propertySelector
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getPropertySelector() {
		if (propertySelector == null) {
			propertySelector = new JComboBox();
			propertySelector.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setCurrentProperty((String) propertySelector.getSelectedItem());
				}
			});
			Properties p = this.getGeneralConfiguration().getProperties();
			if (p != null) {
				Property[] props = p.getProperty();
				if (props != null) {
					for (int i = 0; i < props.length; i++) {
						properties.put(props[i].getName(), props[i]);
						propertySelector.addItem(props[i].getName());
					}
				}
			}
		}
		return propertySelector;
	}


	private void setCurrentProperty(String name) {
		Property p = properties.get(name);
		this.getPropertyDescription().setText(p.getDescription());
		loadValues(p);
	}


	private void loadValues(Property p) {
		getValues();
		this.valuesModel.removeAllElements();
		Values v = p.getValues();
		if (v != null) {
			String[] vals = v.getValue();
			for (int i = 0; i < vals.length; i++) {
				this.valuesModel.addElement(vals[i]);
			}
		}
	}


	/**
	 * This method initializes propertyDescriptionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPropertyDescriptionPanel() {
		if (propertyDescriptionPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
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
		}
		return propertyDescription;
	}


	/**
	 * This method initializes valuesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getValuesPanel() {
		if (valuesPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.BOTH;
			gridBagConstraints10.weighty = 1.0;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.weightx = 1.0;
			valuesPanel = new JPanel();
			valuesPanel.setLayout(new GridBagLayout());
			valuesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Values",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
			valuesPanel.add(getJScrollPane1(), gridBagConstraints10);
			valuesPanel.add(getPriorityPanel(), gridBagConstraints6);
		}
		return valuesPanel;
	}


	/**
	 * This method initializes values
	 * 
	 * @return javax.swing.JList
	 */
	private JList getValues() {
		if (values == null) {
			valuesModel = new DefaultListModel();
			values = new JList(valuesModel);
			values.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return values;
	}


	/**
	 * This method initializes actionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.gridx = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridy = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.weightx = 1.0;
			actionPanel = new JPanel();
			actionPanel.setLayout(new GridBagLayout());
			actionPanel.add(getValueToAdd(), gridBagConstraints7);
			actionPanel.add(getAddButton(), gridBagConstraints8);
			actionPanel.add(getRemoveButton(), gridBagConstraints9);
			actionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Add/Remove Value(s)",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
		}
		return actionPanel;
	}


	/**
	 * This method initializes valueToAdd
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getValueToAdd() {
		if (valueToAdd == null) {
			valueToAdd = new JTextField();
		}
		return valueToAdd;
	}


	/**
	 * This method initializes addButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.setIcon(LookAndFeel.getAddIcon());
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addValue();
				}
			});
		}
		return addButton;
	}


	/**
	 * This method initializes removeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.setIcon(LookAndFeel.getRemoveIcon());
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeValue();
				}
			});
		}
		return removeButton;
	}


	private void moveUp() {
		int index = values.getSelectedIndex();
		if (index > 0) {
			Property p = properties.get(getPropertySelector().getSelectedItem());
			Values v = p.getValues();
			String[] vals = v.getValue();
			String temp = vals[index - 1];
			vals[index - 1] = vals[index];
			vals[index] = temp;
			loadValues(p);
			values.setSelectedIndex(index - 1);
		}
	}


	private void moveDown() {
		int index = values.getSelectedIndex();
		if (index != -1) {
			Property p = properties.get(getPropertySelector().getSelectedItem());
			Values v = p.getValues();
			String[] vals = v.getValue();
			if (index < (vals.length - 1)) {
				String temp = vals[index + 1];
				vals[index + 1] = vals[index];
				vals[index] = temp;
				loadValues(p);
				values.setSelectedIndex(index + 1);
			}

		}
	}


	private void addValue() {
		String value = Utils.clean(valueToAdd.getText());
		if (value == null) {
			showErrorMessage("Error Adding Value", "No value specified, please specify a value to add!!!");
			return;
		} else {

			Property p = properties.get(getPropertySelector().getSelectedItem());
			Values v = p.getValues();
			String[] newVals = null;
			if (v != null) {
				String[] vals = v.getValue();
				if (vals != null) {
					newVals = new String[vals.length + 1];
					for (int i = 0; i < vals.length; i++) {
						newVals[i] = vals[i];
					}
					newVals[vals.length] = value;
					v.setValue(newVals);
				} else {
					newVals = new String[1];
					newVals[0] = value;
					v.setValue(newVals);
				}
			} else {
				v = new Values();
				p.setValues(v);
				newVals = new String[1];
				newVals[0] = value;
				v.setValue(newVals);
			}
			this.loadValues(p);
		}
	}


	private void removeValue() {
		int index = values.getSelectedIndex();
		if (index == -1) {
			showErrorMessage("Error Removing Value", "No value selected, please select a value to remove!!!");
		} else {

			Property p = properties.get(getPropertySelector().getSelectedItem());
			Values v = p.getValues();
			String[] vals = v.getValue();
			if (vals.length == 1) {
				p.setValues(null);
			} else {
				String[] newVals = new String[vals.length - 1];
				int curr = 0;
				for (int i = 0; i < vals.length; i++) {
					if (index != i) {
						newVals[curr] = vals[i];
						curr = curr + 1;
					}
				}
				v.setValue(newVals);

			}
			this.loadValues(p);

		}
	}


	public void showErrorMessage(String title, String msg) {
		showErrorMessage(title, new String[]{msg});
	}


	public void showErrorMessage(String title, String[] msg) {
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
	}


	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getValues());
		}
		return jScrollPane1;
	}


	/**
	 * This method initializes priorityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPriorityPanel() {
		if (priorityPanel == null) {
			priorityPanel = new JPanel();
			priorityPanel.setLayout(new FlowLayout());
			priorityPanel.add(getIncreaseButton(), null);
			priorityPanel.add(getDecreaseButton(), null);
		}
		return priorityPanel;
	}


	/**
	 * This method initializes increaseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getIncreaseButton() {
		if (increaseButton == null) {
			increaseButton = new JButton();
			increaseButton.setText("Move Up");
			increaseButton.setIcon(LookAndFeel.getUpIcon());
			increaseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					moveUp();
				}
			});
		}
		return increaseButton;
	}


	/**
	 * This method initializes decreaseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDecreaseButton() {
		if (decreaseButton == null) {
			decreaseButton = new JButton();
			decreaseButton.setText("Decrease");
			decreaseButton.setIcon(LookAndFeel.getDownIcon());
			decreaseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					moveDown();
				}
			});
		}
		return decreaseButton;
	}
}
