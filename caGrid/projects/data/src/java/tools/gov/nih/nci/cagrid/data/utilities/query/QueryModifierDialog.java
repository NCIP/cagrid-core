package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/** 
 *  QueryModifierDialog
 *  Configures a query modifier
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 22, 2006 
 * @version $Id$ 
 */
public class QueryModifierDialog extends JDialog {
	
	private Object target;
	private TypeTraverser traverser;
	private QueryModifier existingModifier = null;
	private QueryModifier configuredModifier = null;
	
	private JLabel targetTypeLabel = null;
	private JTextField targetTypeTextField = null;
	private JRadioButton multipleAttributesRadioButton = null;
	private JRadioButton distinctAttributeRadioButton = null;
	private JCheckBox countCheckBox = null;
	private JScrollPane attributesScrollPane = null;
	private JPanel attributesPanel = null;
	private JPanel attribButtonPanel = null;
	private JPanel datatypePanel = null;
	private JPanel mainPanel = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	
	private QueryModifierDialog(Object target, TypeTraverser traverser, QueryModifier modifiers) {
		this.target = target;
		this.traverser = traverser;
		this.existingModifier = modifiers;
		initialize();
	}
	
	
	private void initialize() {
        this.setContentPane(getMainPanel());
        this.setSize(new java.awt.Dimension(446,280));
        if (existingModifier != null) {
        	populateModifiers();
        }
        setVisible(true);
	}
	
	
	private void populateModifiers() {
		getCountCheckBox().setSelected(existingModifier.isCountOnly());
		if (existingModifier.getDistinctAttribute() != null) {
			// find the JRadioButton needed and select it
			for (int i = 0; i < getAttributesPanel().getComponentCount(); i++) {
				JRadioButton rb = (JRadioButton) getAttributesPanel().getComponent(i);
				if (rb.getText().equals(existingModifier.getDistinctAttribute())) {
					rb.setSelected(true);
					break;
				}
			}
		} else if (existingModifier.getAttributeNames() != null) {
			Set<String> names = new HashSet<String>();
			Collections.addAll(names, existingModifier.getAttributeNames());
			for (int i = 0; i < getAttributesPanel().getComponentCount(); i++) {
				JCheckBox check = (JCheckBox) getAttributesPanel().getComponent(i);
				check.setSelected(names.contains(check.getText()));
			}
		}
	}
	
	
	public static QueryModifier getModifier(Object target, TypeTraverser traverser) {
		return getModifier(target, traverser, null);
	}
	
	
	public static QueryModifier getModifier(Object target, TypeTraverser traverser, QueryModifier existingMods) {
		QueryModifierDialog dialog = new QueryModifierDialog(target, traverser, existingMods);
		return dialog.configuredModifier;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getTargetTypeLabel() {
		if (targetTypeLabel == null) {
			targetTypeLabel = new JLabel();
			targetTypeLabel.setText("Target Data Type:");
		}
		return targetTypeLabel;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTargetTypeTextField() {
		if (targetTypeTextField == null) {
			targetTypeTextField = new JTextField();
			targetTypeTextField.setEditable(false);
			targetTypeTextField.setText(target.getName());
		}
		return targetTypeTextField;
	}


	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getMultipleAttributesRadioButton() {
		if (multipleAttributesRadioButton == null) {
			multipleAttributesRadioButton = new JRadioButton();
			multipleAttributesRadioButton.setText("Multiple Attributes");
			if (existingModifier != null && existingModifier.getAttributeNames() != null) {
				multipleAttributesRadioButton.setSelected(true);
			}
		}
		return multipleAttributesRadioButton;
	}


	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getDistinctAttributeRadioButton() {
		if (distinctAttributeRadioButton == null) {
			distinctAttributeRadioButton = new JRadioButton();
			distinctAttributeRadioButton.setText("Distinct Attribute");
			if (existingModifier != null && existingModifier.getDistinctAttribute() != null) {
				distinctAttributeRadioButton.setSelected(true);
			}
		}
		return distinctAttributeRadioButton;
	}


	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCountCheckBox() {
		if (countCheckBox == null) {
			countCheckBox = new JCheckBox();
			countCheckBox.setText("Count Results");
			if (existingModifier != null) {
				countCheckBox.setSelected(existingModifier.isCountOnly());
			}
		}
		return countCheckBox;
	}


	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getAttributesScrollPane() {
		if (attributesScrollPane == null) {
			attributesScrollPane = new JScrollPane();
			attributesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			attributesScrollPane.setViewportView(getAttributesPanel());
			attributesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Attributes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
		}
		return attributesScrollPane;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAttributesPanel() {
		if (attributesPanel == null) {
			attributesPanel = new JPanel();
			attributesPanel.setLayout(new GridBagLayout());
			populateAttributesPanel();			
		}
		return attributesPanel;
	}
	
	
	private void populateAttributesPanel() {
		// remove all items from the attributes panel
		attributesPanel.removeAll();		
		AttributeType[] types = traverser.getAttributes(new BaseType(target.getName()));
		for (int i = 0; i < types.length; i++) {
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridx = 0;
			cons.gridy = 1;
			cons.insets = new Insets(2,2,2,2);
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.anchor = GridBagConstraints.WEST;
			if (getMultipleAttributesRadioButton().isSelected()) {
				JCheckBox check = new JCheckBox(types[i].getName());
				getAttributesPanel().add(check, cons);
			} else {
				JRadioButton rb = new JRadioButton(types[i].getName());
				getAttributesPanel().add(rb, cons);
			}
		}
		if (getDistinctAttributeRadioButton().isSelected()) {
			ButtonGroup group = new ButtonGroup();
			for (int i = 0; i < getAttributesPanel().getComponentCount(); i++) {
				JRadioButton rb = (JRadioButton) getAttributesPanel().getComponent(i);
				group.add(rb);
			}
		}
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAttribButtonPanel() {
		if (attribButtonPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 1;
			attribButtonPanel = new JPanel();
			attribButtonPanel.setLayout(new GridBagLayout());
			attribButtonPanel.add(getMultipleAttributesRadioButton(), gridBagConstraints);
			attribButtonPanel.add(getDistinctAttributeRadioButton(), gridBagConstraints1);
			attribButtonPanel.add(getCountCheckBox(), gridBagConstraints2);
			ButtonGroup group = new ButtonGroup();
			group.add(getMultipleAttributesRadioButton());
			group.add(getDistinctAttributeRadioButton());
		}
		return attribButtonPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDatatypePanel() {
		if (datatypePanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints3.gridy = 0;
			datatypePanel = new JPanel();
			datatypePanel.setLayout(new GridBagLayout());
			datatypePanel.add(getTargetTypeLabel(), gridBagConstraints3);
			datatypePanel.add(getTargetTypeTextField(), gridBagConstraints4);
		}
		return datatypePanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			gridBagConstraints10.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints7.gridy = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.weighty = 1.0D;
			gridBagConstraints6.gridheight = 2;
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.weightx = 1.0D;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getDatatypePanel(), gridBagConstraints5);
			mainPanel.add(getAttributesScrollPane(), gridBagConstraints6);
			mainPanel.add(getAttribButtonPanel(), gridBagConstraints7);
			mainPanel.add(getButtonPanel(), gridBagConstraints10);
		}
		return mainPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints9.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getOkButton(), gridBagConstraints8);
			buttonPanel.add(getCancelButton(), gridBagConstraints9);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					configuredModifier = new QueryModifier();
					configuredModifier.setCountOnly(getCountCheckBox().isSelected());
					if (getDistinctAttributeRadioButton().isSelected()) {
						for (int i = 0; i < getAttributesPanel().getComponentCount(); i++) {
							JRadioButton rb = (JRadioButton) getAttributesPanel().getComponent(i);
							if (rb.isSelected()) {
								configuredModifier.setDistinctAttribute(rb.getText());
								break;
							}
						}
					} else if (getMultipleAttributesRadioButton().isSelected()) {
						List<String> names = new ArrayList<String>();
						for (int i = 0; i < getAttributesPanel().getComponentCount(); i++) {
							JCheckBox check = (JCheckBox) getAttributesPanel().getComponent(i);
							if (check.isSelected()) {
								names.add(check.getText());
							}
						}
						if (names.size() != 0) {
							String[] attribNames = new String[names.size()];
							names.toArray(attribNames);
							configuredModifier.setAttributeNames(attribNames);
						}
					}
					setVisible(false);
					dispose();
				}
			});
		}
		return okButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					configuredModifier = null;
					setVisible(false);
					dispose();
				}
			});
		}
		return cancelButton;
	}
}
