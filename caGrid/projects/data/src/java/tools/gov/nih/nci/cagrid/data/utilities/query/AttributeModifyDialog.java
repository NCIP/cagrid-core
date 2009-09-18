package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  AttributeModifyDialog
 *  Dialog to create / modify an attribute
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 15, 2006 
 * @version $Id$ 
 */
public class AttributeModifyDialog extends JDialog {
	
	private AttributeType attribType;
	private String predicateValue;
	private String attributeValue;
	
	private JLabel nameLabel = null;
	private JLabel predicateLabel = null;
	private JLabel valueLabel = null;
	private JLabel typeLabel = null;
	private JTextField nameTextField = null;
	private JTextField valueTextField = null;
	private JTextField typeTextField = null;
	private JComboBox predicateComboBox = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JPanel buttonPanel = null;
	private JPanel entryPanel = null;
	private JPanel mainPanel = null;
	
	private Attribute attribute;
	
	private AttributeModifyDialog(JFrame queryBuilder, AttributeType attribType, String predicate, String value) {
		super(queryBuilder);
		setModal(true);
		this.attribute = null;
		this.attribType = attribType;
		this.predicateValue = predicate;
		this.attributeValue = value;		
		initialize();
	}
	
	
	public static Attribute getAttribute(JFrame queryBuilder, AttributeType attribType) {
		return getAttribute(queryBuilder, attribType, null, null);
	}
	
	
	public static Attribute getAttribute(JFrame queryBuilder, AttributeType attribType, String predicate, String value) {
		AttributeModifyDialog dialog = new AttributeModifyDialog(queryBuilder, attribType, predicate, value);
		return dialog.attribute;
	}
	
	
	private void initialize() {
        this.setTitle("Attribute: " + attribType.getName());
        this.setSize(new java.awt.Dimension(310, 173));
        this.setContentPane(getMainPanel());
        // center the dialog
		int w = getParent().getSize().width;
		int h = getParent().getSize().height;
		int x = getParent().getLocationOnScreen().x;
		int y = getParent().getLocationOnScreen().y;
		Dimension dim = getSize();
		setLocation(w / 2 + x - dim.width / 2, h / 2 + y - dim.height / 2);		
        this.setVisible(true);
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getNameLabel() {
		if (nameLabel == null) {
			nameLabel = new JLabel();
			nameLabel.setText("Name:");
		}
		return nameLabel;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getPredicateLabel() {
		if (predicateLabel == null) {
			predicateLabel = new JLabel();
			predicateLabel.setText("Predicate:");
		}
		return predicateLabel;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getValueLabel() {
		if (valueLabel == null) {
			valueLabel = new JLabel();
			valueLabel.setText("Value:");
		}
		return valueLabel;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getTypeLabel() {
		if (typeLabel == null) {
			typeLabel = new JLabel();
			typeLabel.setText("Type:");
		}
		return typeLabel;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setEditable(false);
			nameTextField.setText(attribType.getName());
		}
		return nameTextField;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getValueTextField() {
		if (valueTextField == null) {
			valueTextField = new JTextField();
			if (attributeValue != null) {
				valueTextField.setText(attributeValue);
			}
		}
		return valueTextField;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTypeTextField() {
		if (typeTextField == null) {
			typeTextField = new JTextField();
			typeTextField.setEditable(false);
			typeTextField.setText(attribType.getDataType());
		}
		return typeTextField;
	}


	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getPredicateComboBox() {
		if (predicateComboBox == null) {
			predicateComboBox = new JComboBox();
			predicateComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					Predicate pred = (Predicate) predicateComboBox.getSelectedItem();
					if (pred == Predicate.IS_NULL || pred == Predicate.IS_NOT_NULL) {
						getValueTextField().setText("");
						getValueTextField().setEnabled(false);
					} else {
						getValueTextField().setEnabled(true);
					}
				}
			});
			// populate the combo box in alphabetical order
			List<Predicate> predicates = new ArrayList<Predicate>();
			try {
				Field[] predFields = Predicate.class.getFields();
				for (int i = 0; i < predFields.length; i++) {
					Field field = predFields[i];
					if (field.getType().equals(Predicate.class)) {
						if (Modifier.isPublic(field.getModifiers())
							&& Modifier.isStatic(field.getModifiers())) {
							predicates.add((Predicate) field.get(null));
						}
					}
				}
			} catch (Exception ex) {
				CompositeErrorDialog.showErrorDialog("Error populating predicate list: " + ex.getMessage(), ex);
			}
			// sort the predicates
			Collections.sort(predicates, new Comparator<Predicate>() {
				public int compare(Predicate o1, Predicate o2) {
					return o1.toString().compareTo(o2.toString());
				}
			});
			// add predicates to the combo
			Iterator predIter = predicates.iterator();
			while (predIter.hasNext()) {
				predicateComboBox.addItem(predIter.next());
			}
			// set the selected predicate value, if applicable
			if (predicateValue != null) {
				for (int i = 0; i < predicateComboBox.getItemCount(); i++) {
					Predicate pred = (Predicate) predicateComboBox.getItemAt(i);
					if (pred.getValue().equals(predicateValue)) {
						predicateComboBox.setSelectedIndex(i);
						break;
					}
				}
			}
		}
		return predicateComboBox;
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
					Predicate pred = (Predicate) getPredicateComboBox().getSelectedItem();
					String value = getValueTextField().getText();
					// TODO: Attribute value of correct type!
					attribute = new Attribute(attribType.getName(), pred, value);
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
					attribute = null;
					dispose();
				}
			});
		}
		return cancelButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getOkButton(), gridBagConstraints);
			buttonPanel.add(getCancelButton(), gridBagConstraints1);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getEntryPanel() {
		if (entryPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 3;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 3;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 0;
			entryPanel = new JPanel();
			entryPanel.setLayout(new GridBagLayout());
			entryPanel.add(getNameLabel(), gridBagConstraints2);
			entryPanel.add(getPredicateLabel(), gridBagConstraints3);
			entryPanel.add(getValueLabel(), gridBagConstraints4);
			entryPanel.add(getTypeLabel(), gridBagConstraints5);
			entryPanel.add(getNameTextField(), gridBagConstraints6);
			entryPanel.add(getPredicateComboBox(), gridBagConstraints7);
			entryPanel.add(getValueTextField(), gridBagConstraints8);
			entryPanel.add(getTypeTextField(), gridBagConstraints9);
		}
		return entryPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.weightx = 1.0D;
			gridBagConstraints10.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getEntryPanel(), gridBagConstraints10);
			mainPanel.add(getButtonPanel(), gridBagConstraints11);
		}
		return mainPanel;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
