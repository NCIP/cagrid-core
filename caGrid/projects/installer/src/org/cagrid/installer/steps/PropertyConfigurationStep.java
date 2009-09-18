/**
 * 
 */
package org.cagrid.installer.steps;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.options.BooleanPropertyConfigurationOption;
import org.cagrid.installer.steps.options.FilePropertyConfigurationOption;
import org.cagrid.installer.steps.options.ListPropertyConfigurationOption;
import org.cagrid.installer.steps.options.PasswordPropertyConfigurationOption;
import org.cagrid.installer.steps.options.PropertyConfigurationOption;
import org.cagrid.installer.steps.options.TextPropertyConfigurationOption;
import org.cagrid.installer.steps.options.ListPropertyConfigurationOption.LabelValuePair;
import org.cagrid.installer.validator.Validator;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.WizardModel;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class PropertyConfigurationStep extends PanelWizardStep {

    private static final Log logger = LogFactory.getLog(PropertyConfigurationStep.class);

    private List<PropertyConfigurationOption> options = new ArrayList<PropertyConfigurationOption>();

    private JPanel optionsPanel = null;

    private List<String> optionKeys = new ArrayList<String>();

    private List<Component> optionValueFields = new ArrayList<Component>();

    protected Map<String, Boolean> requiredFields = new HashMap<String, Boolean>();

    protected CaGridInstallerModel model;

    private List<Validator> validators = new ArrayList<Validator>();

    private Map<String, TableModel> tableModels = new HashMap<String, TableModel>();

    private List<JLabel> optionLabels = new ArrayList<JLabel>();


    public List<Validator> getValidators() {
        return validators;
    }


    public void setValidators(List<Validator> validators) {
        this.validators = validators;
    }


    /**
	 * 
	 */
    public PropertyConfigurationStep() {

    }


    /**
     * @param arg0
     * @param arg1
     */
    public PropertyConfigurationStep(String name, String description) {
        super(name, description);
    }


    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public PropertyConfigurationStep(String name, String description, Icon icon) {
        super(name, description, icon);
    }


    public void init(WizardModel m) {
        if (!(m instanceof CaGridInstallerModel)) {
            throw new IllegalStateException("This step requires a StatefulWizardModel instance.");
        }
        this.model = (CaGridInstallerModel) m;

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.gridy = 1;
        this.setLayout(new GridBagLayout());
        this.add(getOptionsPanel(), gridBagConstraints1);

        for (PropertyConfigurationOption option : getOptions()) {
            if (option instanceof PasswordPropertyConfigurationOption) {
                addPasswordOption((PasswordPropertyConfigurationOption) option);
            } else if (option instanceof FilePropertyConfigurationOption) {
                addFileOption((FilePropertyConfigurationOption) option);
            } else if (option instanceof TextPropertyConfigurationOption) {
                addTextOption((TextPropertyConfigurationOption) option);
            } else if (option instanceof ListPropertyConfigurationOption) {
                addListOption((ListPropertyConfigurationOption) option);
            } else if (option instanceof BooleanPropertyConfigurationOption) {
                addBooleanOption((BooleanPropertyConfigurationOption) option);
            } else {
                throw new IllegalStateException("Unknown PropertyConfigurationOption type: "
                    + option.getClass().getName());
            }
        }
        checkComplete();
    }


    public void prepare() {
        for (String key : this.requiredFields.keySet()) {
            if (this.model.isSet(key)) {
                this.requiredFields.put(key, true);
            }
        }
        checkComplete();
    }


    protected void checkComplete() {
        boolean allRequiredFieldsSpecified = true;
        for (String key : this.requiredFields.keySet()) {
            boolean isSet = this.requiredFields.get(key);
            // logger.debug("Checking " + key + ": " + isSet);
            if (!isSet) {
                allRequiredFieldsSpecified = false;
                break;
            }
        }
        setComplete(allRequiredFieldsSpecified);
    }


    protected void addBooleanOption(BooleanPropertyConfigurationOption option) {
        String defaultValue = this.model.getProperty(option.getName());
        try {
            Boolean.valueOf(defaultValue);
        } catch (Exception ex) {
            defaultValue = String.valueOf(option.getDefaultValue());
        }
        JCheckBox valueField = new JCheckBox();
        if (option.isRequired()) {
            addRequiredListener(option.getName(), valueField);
        }
        valueField.setSelected(option.getDefaultValue());
        addOption(option.getName(), option.getDescription(), valueField);
    }


    protected void addRequiredListener(final String requiredField, ItemSelectable valueField) {
        // logger.debug("Added required field: " + requiredField);
        this.requiredFields.put(requiredField, false);
        valueField.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (PropertyConfigurationStep.this.requiredFields.containsKey(requiredField)) {
                    boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                    PropertyConfigurationStep.this.requiredFields.put(requiredField, selected);
                    PropertyConfigurationStep.this.checkComplete();
                }
            }

        });
    }


    protected void addListOption(ListPropertyConfigurationOption option) {
        String defaultValue = this.model.getProperty(option.getName());
        LabelValuePair defaultValuePair = null;
        if (defaultValue != null) {
            boolean foundIt = false;
            for (LabelValuePair value : option.getChoices()) {
                if (value.getValue().equals(defaultValue)) {
                    foundIt = true;
                    defaultValuePair = value;
                    break;
                }
            }

        } else {
            defaultValuePair = option.getChoices()[0];
        }
        JComboBox valueField = new JComboBox(option.getChoices());
        if (defaultValuePair != null) {
            valueField.setSelectedItem(defaultValuePair);
        }
        if (option.isRequired()) {
            addRequiredListener(option.getName(), valueField);
            this.requiredFields.put(option.getName(), true);
        }
        addOption(option.getName(), option.getDescription(), valueField);
    }


    protected void addFileOption(final FilePropertyConfigurationOption option) {
        final JButton control = new JButton(option.getBrowseLabel());
        final PropertyConfigurationStep window = this;
        control.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JFileChooser fc = new JFileChooser();
                String[] extensions = option.getExtensions();
                if (extensions != null && extensions.length > 0) {
                    fc.setFileFilter(new ExtensionFilter(option.getExtensions()));
                } else {
                    fc.setAcceptAllFileFilterUsed(true);
                }
                if (option.isDirectoriesOnly()) {
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                }

                int returnVal = fc.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    JTextField valueField = (JTextField) window.getOption(option.getName());
                    valueField.setText(fc.getSelectedFile().getAbsolutePath());
                }
            }

        });
        addTextOption(option, control);
    }


    protected void addTextOption(TextPropertyConfigurationOption option) {
        addTextOption(option, null);
    }


    protected void addTextOption(TextPropertyConfigurationOption option, Component control) {
        String defaultValue = this.model.getProperty(option.getName());
        if (defaultValue == null) {
            defaultValue = option.getDefaultValue();
        }
        final String requiredField = option.getName();
        final JTextField valueField = new JTextField(defaultValue);
        if (option.isRequired()) {
            boolean isSet = defaultValue != null && defaultValue.trim().length() > 0;
            this.requiredFields.put(option.getName(), isSet);
            valueField.addCaretListener(new CaretListener() {
                public void caretUpdate(CaretEvent evt) {
                    if (PropertyConfigurationStep.this.requiredFields.containsKey(requiredField)) {
                        boolean selected = valueField.getText() != null && valueField.getText().trim().length() > 0;
                        PropertyConfigurationStep.this.requiredFields.put(requiredField, selected);
                        PropertyConfigurationStep.this.checkComplete();
                    }
                }

            });
        }
        addOption(option.getName(), option.getDescription(), valueField, control);
    }


    protected void addPasswordOption(PasswordPropertyConfigurationOption option) {
        String defaultValue = this.model.getProperty(option.getName());
        if (defaultValue == null) {
            defaultValue = option.getDefaultValue();
        }
        final String requiredField = option.getName();
        final JPasswordField valueField = new JPasswordField(defaultValue);
        if (option.isRequired()) {
            boolean isSet = defaultValue != null && defaultValue.trim().length() > 0;
            this.requiredFields.put(option.getName(), isSet);
            valueField.addCaretListener(new CaretListener() {
                public void caretUpdate(CaretEvent evt) {
                    if (PropertyConfigurationStep.this.requiredFields.containsKey(requiredField)) {
                        boolean selected = valueField.getPassword() != null
                            && String.valueOf(valueField.getPassword()).trim().length() > 0;
                        PropertyConfigurationStep.this.requiredFields.put(requiredField, selected);
                        PropertyConfigurationStep.this.checkComplete();
                    }
                }

            });
        }
        addOption(option.getName(), option.getDescription(), valueField);
    }


    protected void addOption(String key, String description, Component valueField) {
        addOption(key, description, valueField, null);
    }


    protected void addOption(String key, String description, Component valueField, Component control) {
        this.optionKeys.add(key);

        JLabel label = new JLabel(description);
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.gridy = this.optionKeys.size() - 1;
        this.getOptionsPanel().add(label, gridBagConstraints1);

        this.optionLabels.add(label);
        this.optionValueFields.add(valueField);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.gridy = this.optionKeys.size() - 1;
        gridBagConstraints2.weightx = 1;
        gridBagConstraints2.insets = new Insets(2, 5, 2, 2);
        this.getOptionsPanel().add(valueField, gridBagConstraints2);

        if (control != null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 2;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridy = this.optionKeys.size() - 1;
            gridBagConstraints3.weightx = 1;
            gridBagConstraints3.insets = new Insets(2, 5, 2, 2);
            this.getOptionsPanel().add(control, gridBagConstraints3);
        }
    }


    public void applyState() throws InvalidStateException {

        Map<String, String> tempState = new HashMap<String, String>();
        try {
            for (int i = 0; i < optionKeys.size(); i++) {
                String key = optionKeys.get(i);
                Component c = optionValueFields.get(i);
                String value = null;
                if (c instanceof JPasswordField) {
                    value = String.valueOf(((JPasswordField) c).getPassword());
                } else if (c instanceof JTextField) {
                    value = ((JTextField) c).getText();
                } else if (c instanceof JCheckBox) {
                    value = String.valueOf(((JCheckBox) c).isSelected());
                } else if (c instanceof JComboBox) {
                    LabelValuePair pair = (LabelValuePair) ((JComboBox) c).getSelectedItem();
                    value = pair.getValue();
                }
                tempState.put(key, value);
            }
        } catch (Exception ex) {
            throw new InvalidStateException("Error occurred: " + ex.getMessage(), ex);
        }

        Map<String, String> m = new HashMap<String, String>();
        m.putAll(this.model.getStateMap());
        m.putAll(tempState);
        validate(m);

        for (String key : tempState.keySet()) {
            this.model.setProperty(key, tempState.get(key));
        }
    }


    protected void validate(Map<String, String> state) throws InvalidStateException {
        for (Validator v : getValidators()) {
            v.validate(state);
        }
    }


    private JPanel getOptionsPanel() {
        if (optionsPanel == null) {
            optionsPanel = new JPanel();
            optionsPanel.setLayout(new GridBagLayout());
        }
        return optionsPanel;
    }


    public List<PropertyConfigurationOption> getOptions() {
        return options;
    }


    public void setOptions(List<PropertyConfigurationOption> options) {
        this.options = options;
    }


    protected Component getOption(String key) {
        Component option = null;
        int idx = this.optionKeys.indexOf(key);
        if (idx != -1) {
            option = this.optionValueFields.get(idx);
        }
        return option;
    }


    protected JLabel getLabel(String key) {
        JLabel label = null;
        int idx = this.optionKeys.indexOf(key);
        if (idx != -1) {
            label = this.optionLabels.get(idx);
        }
        return label;
    }


    private static class ExtensionFilter extends FileFilter {

        private String description = "*";

        private String[] extensions;


        ExtensionFilter() {
            this(new String[0]);
        }


        ExtensionFilter(String[] extensions) {
            this.extensions = extensions;
            if (this.extensions.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.extensions.length; i++) {
                    sb.append("*");
                    sb.append(this.extensions[i]);
                    if (i + 1 < this.extensions.length) {
                        sb.append(",");
                    }
                }
                this.description = sb.toString();
            }
        }


        @Override
        public boolean accept(File file) {
            boolean accept = true;
            if (this.extensions.length > 0) {
                accept = false;
                for (String ext : this.extensions) {
                    if (file.getAbsolutePath().endsWith(ext)) {
                        accept = true;
                        break;
                    }
                }
            }
            return accept;
        }


        @Override
        public String getDescription() {
            return this.description;
        }

    }

}
