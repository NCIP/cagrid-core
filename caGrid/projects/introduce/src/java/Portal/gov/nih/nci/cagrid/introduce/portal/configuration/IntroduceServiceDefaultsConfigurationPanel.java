package gov.nih.nci.cagrid.introduce.portal.configuration;


import gov.nih.nci.cagrid.introduce.beans.configuration.IntroduceServiceDefaults;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.cagrid.grape.ConfigurationBasePanel;
import org.cagrid.grape.ConfigurationDescriptorTreeNode;
import org.cagrid.grape.LookAndFeel;

public class IntroduceServiceDefaultsConfigurationPanel extends ConfigurationBasePanel {

    private JPanel titlePanel = null;

    private JLabel titleLabel = null;

    private JLabel icon = null;

    private JPanel configurationPanel = null;

    private Logger log;

    private JLabel serviceNameLabel = null;

    private JTextField serviceNameTextField = null;
    
    private IntroduceServiceDefaults iConf = null;

    private JLabel serviceNamespaceLabel = null;

    private JLabel servicePackageLabel = null;

    private JLabel deploymentPrefixLabel = null;

    private JLabel indexServiceLabel = null;

    private JTextField serviceNamespaceTextField = null;

    private JTextField packageNameTextField = null;

    private JTextField deploymentPrefixTextField = null;

    private JTextField indexServiceURLTextField = null;

    /**
     * This is the default constructor
     */
    public IntroduceServiceDefaultsConfigurationPanel(ConfigurationDescriptorTreeNode treeNode, Object conf) {
        super(treeNode, conf);
        log = Logger.getLogger(this.getClass().getName());
        iConf = (IntroduceServiceDefaults)conf;
        initialize();
        
    }
    
    private IntroduceServiceDefaults getIntroduceServiceDefaults(){
        return this.iConf;
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
            titleLabel.setText("Service Creation Defaults");
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
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.BOTH;
            gridBagConstraints12.gridy = 4;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.BOTH;
            gridBagConstraints10.gridy = 2;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.BOTH;
            gridBagConstraints9.gridy = 1;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 4;
            indexServiceLabel = new JLabel();
            indexServiceLabel.setText("Index Service Registration URL");
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 3;
            deploymentPrefixLabel = new JLabel();
            deploymentPrefixLabel.setText("Deployment Prefix");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 2;
            servicePackageLabel = new JLabel();
            servicePackageLabel.setText("Package Name");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 1;
            serviceNamespaceLabel = new JLabel();
            serviceNamespaceLabel.setText("Namespace");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 0;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 0;
            serviceNameLabel = new JLabel();
            serviceNameLabel.setText("Name");
            configurationPanel = new JPanel();
            configurationPanel.setLayout(new GridBagLayout());
            configurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configuration",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            configurationPanel.add(serviceNameLabel, gridBagConstraints7);
            configurationPanel.add(getServiceNameTextField(), gridBagConstraints8);
            configurationPanel.add(serviceNamespaceLabel, gridBagConstraints3);
            configurationPanel.add(servicePackageLabel, gridBagConstraints4);
            configurationPanel.add(deploymentPrefixLabel, gridBagConstraints5);
            configurationPanel.add(indexServiceLabel, gridBagConstraints6);
            configurationPanel.add(getServiceNamespaceTextField(), gridBagConstraints9);
            configurationPanel.add(getPackageNameTextField(), gridBagConstraints10);
            configurationPanel.add(getDeploymentPrefixTextField(), gridBagConstraints11);
            configurationPanel.add(getIndexServiceURLTextField(), gridBagConstraints12);
        }
        return configurationPanel;
    }



    /**
     * This method initializes serviceNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getServiceNameTextField() {
        if (serviceNameTextField == null) {
            serviceNameTextField = new JTextField();
            serviceNameTextField.setText(getIntroduceServiceDefaults().getServiceName());
            serviceNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            
                public void removeUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServiceName(serviceNameTextField.getText());
            
                }
            
            
                public void insertUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServiceName(serviceNameTextField.getText());
            
                }
            
            
                public void changedUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServiceName(serviceNameTextField.getText());
            
                }
            
            });
        }
        return serviceNameTextField;
    }

    /**
     * This method initializes serviceNamespaceTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getServiceNamespaceTextField() {
        if (serviceNamespaceTextField == null) {
            serviceNamespaceTextField = new JTextField();
            serviceNamespaceTextField.setText(getIntroduceServiceDefaults().getServiceNamespace());
            serviceNamespaceTextField.getDocument().addDocumentListener(new DocumentListener() {
            
                public void removeUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServiceNamespace(serviceNamespaceTextField.getText());
            
                }
            
            
                public void insertUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServiceNamespace(serviceNamespaceTextField.getText());
            
                }
            
            
                public void changedUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServiceNamespace(serviceNamespaceTextField.getText());
            
                }
            
            });
        }
        return serviceNamespaceTextField;
    }

    /**
     * This method initializes packageNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getPackageNameTextField() {
        if (packageNameTextField == null) {
            packageNameTextField = new JTextField();
            packageNameTextField.setText(getIntroduceServiceDefaults().getServicePackage());
            packageNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            
                public void removeUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServicePackage(packageNameTextField.getText());
            
                }
            
            
                public void insertUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServicePackage(packageNameTextField.getText());
            
                }
            
            
                public void changedUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setServicePackage(packageNameTextField.getText());
            
                }
            
            });
        }
        return packageNameTextField;
    }

    /**
     * This method initializes deploymentPrefixTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getDeploymentPrefixTextField() {
        if (deploymentPrefixTextField == null) {
            deploymentPrefixTextField = new JTextField();
            deploymentPrefixTextField.setText(getIntroduceServiceDefaults().getDeploymentPrefix());
            deploymentPrefixTextField.getDocument().addDocumentListener(new DocumentListener() {
            
                public void removeUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setDeploymentPrefix(deploymentPrefixTextField.getText());
            
                }
            
            
                public void insertUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setDeploymentPrefix(deploymentPrefixTextField.getText());
            
                }
            
            
                public void changedUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setDeploymentPrefix(deploymentPrefixTextField.getText());
            
                }
            
            });
        }
        return deploymentPrefixTextField;
    }

    /**
     * This method initializes indexServiceURLTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getIndexServiceURLTextField() {
        if (indexServiceURLTextField == null) {
            indexServiceURLTextField = new JTextField();
            indexServiceURLTextField.setText(getIntroduceServiceDefaults().getIndexServiceRegistrationURL());
            indexServiceURLTextField.getDocument().addDocumentListener(new DocumentListener() {
            
                public void removeUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setIndexServiceRegistrationURL(indexServiceURLTextField.getText());
                 }
            
            
                public void insertUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setIndexServiceRegistrationURL(indexServiceURLTextField.getText());
            
                }
            
            
                public void changedUpdate(DocumentEvent e) {
                    getIntroduceServiceDefaults().setIndexServiceRegistrationURL(indexServiceURLTextField.getText());
            
                }
            
            });
        }
        return indexServiceURLTextField;
    }

}
