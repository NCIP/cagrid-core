package gov.nih.nci.cagrid.introduce.portal.configuration;


import gov.nih.nci.cagrid.introduce.beans.extension.Properties;
import gov.nih.nci.cagrid.introduce.beans.extension.PropertiesProperty;
import gov.nih.nci.cagrid.introduce.portal.common.GenericPropertiesPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.cagrid.grape.ConfigurationBasePanel;
import org.cagrid.grape.ConfigurationDescriptorTreeNode;
import org.cagrid.grape.LookAndFeel;

public class IntroduceGlobalExtensionPropertiesConfigurationPanel extends ConfigurationBasePanel {

    private JPanel titlePanel = null;

    private JLabel titleLabel = null;

    private JLabel icon = null;

    private JPanel configurationPanel = null;

    private Logger log;

    private Properties iConf = null;

    private JScrollPane serviceURLsScrollPane = null;

    private GenericPropertiesPanel serviceURLsPanel = null;

    /**
     * This is the default constructor
     */
    public IntroduceGlobalExtensionPropertiesConfigurationPanel(ConfigurationDescriptorTreeNode treeNode, Object conf) {
        super(treeNode, conf);
        log = Logger.getLogger(this.getClass().getName());
        iConf = (Properties)conf;
        initialize();
        addURLs();
        
    }
    
    private Properties getProperties(){
        return this.iConf;
    }
    
    private void addURLs(){
        if(getProperties()!=null && getProperties().getProperty()!=null){
            for (int i = 0; i < getProperties().getProperty().length; i++) {
                final PropertiesProperty prop = getProperties().getProperty(i);
                getPropertiesPanel().addTextField(this.getPropertiesPanel(), prop.getDisplayName(), prop.getValue(),prop.getDescription(), i, true);
                getPropertiesPanel().getTextField(prop.getDisplayName()).setForeground(Color.BLUE);
                final JTextField field = getPropertiesPanel().getTextField(prop.getDisplayName());
                getPropertiesPanel().getTextField(prop.getDisplayName()).getDocument().addDocumentListener(new DocumentListener() {
                
                    public void removeUpdate(DocumentEvent e) {
                        prop.setValue(field.getText());
                
                    }
                
                
                    public void insertUpdate(DocumentEvent e) {
                        prop.setValue(field.getText());
                
                    }
                
                
                    public void changedUpdate(DocumentEvent e) {
                        prop.setValue(field.getText());
                
                    }
                
                });

            }
        }

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
        this.add(getConfigurationPanel(), gridBagConstraints21);
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
            titleLabel.setText("Service URLs");
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            titlePanel = new JPanel();
            titlePanel.setLayout(new GridBagLayout());
            titlePanel.add(icon, gridBagConstraints2);
            titlePanel.add(titleLabel, gridBagConstraints1);
        }
        return titlePanel;
    }


    /**
     * This method initializes configurationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getConfigurationPanel() {
        if (configurationPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.weightx = 1.0;
            configurationPanel = new JPanel();
            configurationPanel.setLayout(new GridBagLayout());
            configurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configuration",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            configurationPanel.add(getServiceURLsScrollPane(), gridBagConstraints3);
        }
        return configurationPanel;
    }

    /**
     * This method initializes serviceURLsScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getServiceURLsScrollPane() {
        if (serviceURLsScrollPane == null) {
            serviceURLsScrollPane = new JScrollPane();
            serviceURLsScrollPane.setPreferredSize(new Dimension(400, 200));
            serviceURLsScrollPane.setViewportView(getPropertiesPanel());
        }
        return serviceURLsScrollPane;
    }

    /**
     * This method initializes serviceURLsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private GenericPropertiesPanel getPropertiesPanel() {
        if (serviceURLsPanel == null) {
            serviceURLsPanel = new GenericPropertiesPanel();
            serviceURLsPanel.setLayout(new GridBagLayout());
            serviceURLsPanel.setBackground(Color.WHITE);
        }
        return serviceURLsPanel;
    }

}
