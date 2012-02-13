package gov.nih.nci.cagrid.introduce.portal.updater.steps;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;

public abstract class BasePropretyConfigureStep extends PanelWizardStep {

	private JPanel optionsPanel = null;

	private List myOptionsKeys = new ArrayList();  //  @jve:decl-index=0:

	private List myOptionsTextFieldValues = new ArrayList(); // @jve:decl-index=0:

	private Map globalOptionContainer = new HashMap();  //  @jve:decl-index=0:

	/**
	 * This method initializes
	 * 
	 */
	public BasePropretyConfigureStep(Map globalMap) {
		super();
		this.globalOptionContainer = globalMap;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0D;
		gridBagConstraints1.weighty = 1.0D;
		gridBagConstraints1.gridy = 1;
		this.setLayout(new GridBagLayout());
		this.add(getOptionsPanel(), gridBagConstraints1);

	}

	public void addListOption(String key, String[] values, String description) {
		this.myOptionsKeys.add(key);
		this.globalOptionContainer.put(key, values[0]);
		JLabel label = new JLabel(description);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = myOptionsKeys.size() - 1;
		JComboBox valueField = new JComboBox(values);
		valueField.setSelectedItem(values[0]);
		this.myOptionsTextFieldValues.add(valueField);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = myOptionsKeys.size() - 1;
		gridBagConstraints2.weightx = 1;
		gridBagConstraints2.insets = new Insets(2, 5, 2, 2);
		this.getOptionsPanel().add(label, gridBagConstraints1);
		this.getOptionsPanel().add(valueField, gridBagConstraints2);
	}

	public void addBooleanOption(String key, boolean value, String description) {
		this.myOptionsKeys.add(key);
		this.globalOptionContainer.put(key, String.valueOf(value));
		JLabel label = new JLabel(description);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = myOptionsKeys.size() - 1;
		JCheckBox valueField = new JCheckBox();
		valueField.setSelected(value);
		this.myOptionsTextFieldValues.add(valueField);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = myOptionsKeys.size() - 1;
		gridBagConstraints2.weightx = 1;
		gridBagConstraints2.insets = new Insets(2, 5, 2, 2);
		this.getOptionsPanel().add(label, gridBagConstraints1);
		this.getOptionsPanel().add(valueField, gridBagConstraints2);
	}

	public void addOption(String key, String value, String description) {
		this.myOptionsKeys.add(key);
		this.globalOptionContainer.put(key, value);
		JLabel label = new JLabel(description);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = myOptionsKeys.size() - 1;
		JTextField valueField = new JTextField(value);
		this.myOptionsTextFieldValues.add(valueField);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = myOptionsKeys.size() - 1;
		gridBagConstraints2.weightx = 1;
		gridBagConstraints2.insets = new Insets(2, 5, 2, 2);
		this.getOptionsPanel().add(label, gridBagConstraints1);
		this.getOptionsPanel().add(valueField, gridBagConstraints2);
	}

	/**
	 * This method initializes optionsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setLayout(new GridBagLayout());
		}
		return optionsPanel;
	}

	public void applyState() throws InvalidStateException {
		super.applyState();
		for (int i = 0; i < myOptionsKeys.size(); i++) {
			if (myOptionsTextFieldValues.get(i) instanceof JTextField) {
                this.globalOptionContainer.put(myOptionsKeys.get(i), 
                    ((JTextField) myOptionsTextFieldValues.get(i)).getText());
            } else if (myOptionsTextFieldValues.get(i) instanceof JCheckBox) {
                this.globalOptionContainer.put(myOptionsKeys.get(i), 
                    String.valueOf(
                        ((JCheckBox) myOptionsTextFieldValues.get(i)).isSelected()));
            } else if (myOptionsTextFieldValues.get(i) instanceof JComboBox) {
                this.globalOptionContainer.put(myOptionsKeys.get(i), 
                    String.valueOf(
                        ((JComboBox) myOptionsTextFieldValues.get(i)).getSelectedItem()));
            }
		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"
