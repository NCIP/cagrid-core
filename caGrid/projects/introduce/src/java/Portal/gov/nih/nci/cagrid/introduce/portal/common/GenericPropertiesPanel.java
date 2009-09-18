package gov.nih.nci.cagrid.introduce.portal.common;

import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 */
public class GenericPropertiesPanel extends JPanel {

    private Map textFields;
    private Map labels;
    private Map passwordFields;


    public GenericPropertiesPanel() {
        super();
        this.textFields = new HashMap();
        this.labels = new HashMap();
        this.passwordFields = new HashMap();
    }


    public String getTextFieldValue(String label) {
        JTextField field = (JTextField) textFields.get(label);
        if (field != null) {
            return field.getText();
        } else {
            return null;
        }
    }
    
    public JTextField getTextField(String label) {
        JTextField field = (JTextField) textFields.get(label);
        if (field != null) {
            return field;
        } else {
            return null;
        }
    }
    
    public JLabel getLabel(String label) {
        JLabel jlabel = (JLabel) labels.get(label);
        if (jlabel != null) {
            return jlabel;
        } else {
            return null;
        }
    }


    public void setTextFieldValue(String label, String value) {
        JTextField field = (JTextField) textFields.get(label);
        if (field != null) {
            field.setText(value);
        }
    }


    public void setTextFieldValueAndDisable(String label, String value) {
        JTextField field = (JTextField) textFields.get(label);
        if (field != null) {
            field.setText(value);
            field.setEditable(false);
        }
    }


    public void setJPasswordFieldValueAndDisable(String label, String value) {
        JPasswordField field = (JPasswordField) textFields.get(label);
        if (field != null) {
            field.setText(value);
            field.setEditable(false);
        }
    }


    public String getPasswordFieldValue(String label) {
        JPasswordField field = (JPasswordField) passwordFields.get(label);
        if (field != null) {
            return new String(field.getPassword());
        } else {
            return null;
        }
    }
    
    
    public void addTextField(JPanel panel, String label, String value, String tooltip, int y, boolean enabled){
        JLabel label1 = new JLabel();
        label1.setText(label);
        JComponent component = null;
        JTextField text = new JTextField();
        text.setText(value);
        text.setEditable(enabled);
        if(tooltip!=null){
            label1.setToolTipText(tooltip);
            text.setToolTipText(tooltip);
        }
        component = text;
        this.textFields.put(label, text);
        this.labels.put(label,label1);

        GridBagConstraints const1 = new GridBagConstraints();
        const1.gridx = 0;
        const1.gridy = y;
        const1.insets = new java.awt.Insets(5, 5, 5, 5);
        const1.anchor = java.awt.GridBagConstraints.WEST;

        GridBagConstraints const2 = new GridBagConstraints();
        const2.gridx = 1;
        const2.gridy = y;
        const2.weightx = 1;
        const2.weighty = 1;
        const2.fill = GridBagConstraints.HORIZONTAL;
        const2.insets = new java.awt.Insets(5, 5, 5, 5);
        const2.anchor = java.awt.GridBagConstraints.WEST;

        panel.add(label1, const1);
        panel.add(component, const2);
    }


    public void addTextField(JPanel panel, String label, String value, int y, boolean enabled) {
        addTextField(panel, label, value, null, y, enabled);
    }


    public void addPasswordField(JPanel panel, String label, String value, int y, boolean enabled) {
        JLabel label1 = new JLabel();
        label1.setText(label);
        JComponent component = null;
        JPasswordField text = new JPasswordField();
        text.setText(value);
        text.setEditable(enabled);
        component = text;
        this.passwordFields.put(label, text);
        this.labels.put(label,label1);

        GridBagConstraints const1 = new GridBagConstraints();
        const1.gridx = 0;
        const1.gridy = y;
        const1.insets = new java.awt.Insets(5, 5, 5, 5);
        const1.anchor = java.awt.GridBagConstraints.WEST;

        GridBagConstraints const2 = new GridBagConstraints();
        const2.gridx = 1;
        const2.gridy = y;
        const2.weightx = 1;
        const2.weighty = 1;
        const2.fill = GridBagConstraints.HORIZONTAL;
        const2.insets = new java.awt.Insets(5, 5, 5, 5);
        const2.anchor = java.awt.GridBagConstraints.WEST;

        panel.add(label1, const1);
        panel.add(component, const2);
    }

}