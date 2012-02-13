package gov.nih.nci.cagrid.introduce.portal.modification.services;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class ServiceTypeConfigurePanel extends JPanel {

	private JLabel namespaceLabel = null;
	private JLabel packageNameLabel = null;
	private JLabel locationLabel = null;
	private JTextField namespaceText = null;
	private JTextField packageNameText = null;
	private JTextField locationText = null;

	private NamespaceType type;


	/**
	 * This method initializes
	 * 
	 */
	public ServiceTypeConfigurePanel() {
		super();
		initialize();
	}


	public void setNamespaceType(NamespaceType type) {
		this.type = type;
		getNamespaceText().setText(type.getNamespace());
		getPackageNameText().setText(type.getPackageName());
		getLocationText().setText(type.getLocation());
		if (type.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
			getPackageNameText().setEditable(false);
		} else {
			getPackageNameText().setEditable(true);
		}
	}


	public void clear() {
		type = null;
		getNamespaceText().setText("");
		getPackageNameText().setText("");
		getLocationText().setText("");
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.gridx = 1;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 1;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = 2;
		locationLabel = new JLabel();
		locationLabel.setText("Location");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 1;
		packageNameLabel = new JLabel();
		packageNameLabel.setText("Package");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		namespaceLabel = new JLabel();
		namespaceLabel.setText("Namespace");
		this.setLayout(new GridBagLayout());
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Namespace Type Configuration",
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
			null, PortalLookAndFeel.getPanelLabelColor()));
		this.add(namespaceLabel, gridBagConstraints);
		this.add(packageNameLabel, gridBagConstraints1);
		this.add(locationLabel, gridBagConstraints2);
		this.add(getNamespaceText(), gridBagConstraints3);
		this.add(getPackageNameText(), gridBagConstraints4);
		this.add(getLocationText(), gridBagConstraints5);

	}


	/**
	 * This method initializes namespaceText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNamespaceText() {
		if (namespaceText == null) {
			namespaceText = new JTextField();
			namespaceText.setEditable(false);
			namespaceText.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					if (type != null) {
						type.setNamespace(getNamespaceText().getText());
					}
				}


				public void removeUpdate(DocumentEvent e) {
					if (type != null) {
						type.setNamespace(getNamespaceText().getText());
					}
				}


				public void insertUpdate(DocumentEvent e) {
					if (type != null) {
						type.setNamespace(getNamespaceText().getText());
					}
				}
			});
		}
		return namespaceText;
	}


	/**
	 * This method initializes packageNameText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPackageNameText() {
		if (packageNameText == null) {
			packageNameText = new JTextField();
			packageNameText.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					if (type != null) {
						type.setPackageName(getPackageNameText().getText());
					}
				}


				public void removeUpdate(DocumentEvent e) {
					if (type != null) {
						type.setPackageName(getPackageNameText().getText());
					}
				}


				public void insertUpdate(DocumentEvent e) {
					if (type != null) {
						type.setPackageName(getPackageNameText().getText());
					}
				}
			});
		}
		return packageNameText;
	}


	/**
	 * This method initializes locationText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getLocationText() {
		if (locationText == null) {
			locationText = new JTextField();
			locationText.setEditable(false);
			locationText.setEnabled(true);
			locationText.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					if (type != null) {
						type.setLocation(getLocationText().getText());
					}
				}


				public void removeUpdate(DocumentEvent e) {
					if (type != null) {
						type.setLocation(getLocationText().getText());
					}
				}


				public void insertUpdate(DocumentEvent e) {
					if (type != null) {
						type.setLocation(getLocationText().getText());
					}
				}
			});
		}
		return locationText;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

} // @jve:decl-index=0:visual-constraint="10,10"
