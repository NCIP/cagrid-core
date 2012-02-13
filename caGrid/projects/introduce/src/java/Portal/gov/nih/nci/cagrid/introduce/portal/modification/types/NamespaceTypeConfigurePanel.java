package gov.nih.nci.cagrid.introduce.portal.modification.types;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;
import javax.swing.JCheckBox;

public class NamespaceTypeConfigurePanel extends JPanel {

	private JLabel namespaceLabel = null;

	private JLabel packageNameLabel = null;

	private JLabel locationLabel = null;

	private JTextField namespaceText = null;

	private JTextField packageNameText = null;

	private JTextField locationText = null;

	private NamespaceType type;  //  @jve:decl-index=0:

	private ValidationResultModel validationModel = new DefaultValidationResultModel();

	private JPanel mainPanel = null;

	public static final String PACKAGE_NAME = "package name";  //  @jve:decl-index=0:

	private JCheckBox generateStubsCheckBox = null;
	
	private SchemaElementTypeConfigurePanel typePanel;

	/**
	 * This method initializes
	 * 
	 */
	public NamespaceTypeConfigurePanel(SchemaElementTypeConfigurePanel typePanel) {
		super();
		this.typePanel = typePanel;
		typePanel.setEnabled(false);
		initialize();;
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
		if(type.getGenerateStubs()!=null && !type.getGenerateStubs().booleanValue()){
			getGenerateStubsCheckBox().setSelected(false);
		} else {
			getGenerateStubsCheckBox().setSelected(true);
		}
		validateInput();
	}

	public void clear() {
		type = null;
		getNamespaceText().setText("");
		getPackageNameText().setText("");
		getLocationText().setText("");
		validateInput();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.fill = GridBagConstraints.BOTH;
		gridBagConstraints6.weightx = 1.0D;
		gridBagConstraints6.weighty = 1.0D;
		gridBagConstraints6.gridy = 0;
		locationLabel = new JLabel();
		locationLabel.setText("Location");
		packageNameLabel = new JLabel();
		packageNameLabel.setText("Package");
		namespaceLabel = new JLabel();
		namespaceLabel.setText("Namespace");
		this.setLayout(new GridBagLayout());
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				"Namespace Type Configuration",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Dialog",
						Font.BOLD, 12),
				PortalLookAndFeel.getPanelLabelColor()));
		//this.add(getMainPanel(), gridBagConstraints6);
		this.add(new IconFeedbackPanel(this.validationModel,this.getMainPanel()),gridBagConstraints6);
	
		initValidation();
	}

	private void initValidation() {
		ValidationComponentUtils.setMessageKey(getPackageNameText(), PACKAGE_NAME);
		validateInput();
		updateComponentTreeSeverity();
	}

	private void validateInput() {

		ValidationResult result = new ValidationResult();

		if (ValidationUtils.isBlank(this.getPackageNameText().getText()) && (ValidationUtils.isNotBlank(getNamespaceText().getText()) && !getNamespaceText().getText().equals(IntroduceConstants.W3CNAMESPACE))) {
			result.add(new SimpleValidationMessage(PACKAGE_NAME
					+ " must not be blank.", Severity.ERROR, PACKAGE_NAME));
		} else if (!CommonTools.isValidPackageName(this.getPackageNameText()
				.getText())) {

			result
					.add(new SimpleValidationMessage(
							PACKAGE_NAME
									+ " is not valid",
							Severity.ERROR, PACKAGE_NAME));

		} else if (!CommonTools.isSuggestedPackageName(this.getPackageNameText()
				.getText())) {

			result
					.add(new SimpleValidationMessage(
							PACKAGE_NAME
									+ " does not look like a java best practice",
							Severity.WARNING, PACKAGE_NAME));

		}

		this.validationModel.setResult(result);
		updateComponentTreeSeverity();
	}

	private void updateComponentTreeSeverity() {
		ValidationComponentUtils
				.updateComponentTreeMandatoryAndBlankBackground(this);
		ValidationComponentUtils.updateComponentTreeSeverityBackground(this,
				this.validationModel.getResult());
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
			namespaceText.getDocument().addDocumentListener(
					new DocumentListener() {
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
			packageNameText.getDocument().addDocumentListener(
					new DocumentListener() {
						public void changedUpdate(DocumentEvent e) {
							if (type != null) {
								type.setPackageName(getPackageNameText()
										.getText());
								validateInput();
							}
						}

						public void removeUpdate(DocumentEvent e) {
							if (type != null) {
								type.setPackageName(getPackageNameText()
										.getText());
								validateInput();
							}
						}

						public void insertUpdate(DocumentEvent e) {
							if (type != null) {
								type.setPackageName(getPackageNameText()
										.getText());
								validateInput();
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
			locationText.getDocument().addDocumentListener(
					new DocumentListener() {
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
	 * This method initializes mainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.weighty = 0.0D;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 3;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 1;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getLocationText(), gridBagConstraints5);
			mainPanel.add(getPackageNameText(), gridBagConstraints4);
			mainPanel.add(getNamespaceText(), gridBagConstraints3);
			mainPanel.add(locationLabel, gridBagConstraints2);
			mainPanel.add(packageNameLabel, gridBagConstraints1);
			mainPanel.add(namespaceLabel, gridBagConstraints);
			mainPanel.add(getGenerateStubsCheckBox(), gridBagConstraints11);
		}
		return mainPanel;
	}

	/**
	 * This method initializes generateStubsCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getGenerateStubsCheckBox() {
		if (generateStubsCheckBox == null) {
			generateStubsCheckBox = new JCheckBox();
			generateStubsCheckBox.setText("Generate Java Beans");
			generateStubsCheckBox.setToolTipText("Tells Introduce wether or not to generate java beans for the data types in this schema.");
			generateStubsCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(type!=null){
						type.setGenerateStubs(new Boolean(generateStubsCheckBox.isSelected()));
						if(typePanel.getSchemaElementType()!=null){
							if(type.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)){
								typePanel.setHide(true);
							} else {
							typePanel.setHide(type.getGenerateStubs().booleanValue());
							}
						}
					} else {
						if(typePanel.getSchemaElementType()!=null){
							typePanel.setHide(false);
						}
					}
				}
			});
		
		}
		return generateStubsCheckBox;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

} // @jve:decl-index=0:visual-constraint="10,10"
