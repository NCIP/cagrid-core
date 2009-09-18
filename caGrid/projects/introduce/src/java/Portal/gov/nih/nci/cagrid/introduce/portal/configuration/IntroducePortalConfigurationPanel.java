package gov.nih.nci.cagrid.introduce.portal.configuration;


import gov.nih.nci.cagrid.introduce.beans.configuration.IntroducePortalConfiguration;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.cagrid.grape.ConfigurationBasePanel;
import org.cagrid.grape.ConfigurationDescriptorTreeNode;
import org.cagrid.grape.LookAndFeel;
import javax.swing.JCheckBox;

public class IntroducePortalConfigurationPanel extends ConfigurationBasePanel {

    private JPanel titlePanel = null;

    private JLabel titleLabel = null;

    private JLabel icon = null;

    private JPanel configurationPanel = null;

    private Logger log;

    private JLabel namespaceReplacementPolicyLabel = null;

    private JComboBox namespaceReplacementPolicyComboBox = null;

    private JLabel softwareUpdateSiteURLLabel = null;

    private JTextField softwareUpdateSiteURLTextField = null;
    
    private IntroducePortalConfiguration iConf = null;

    private JLabel checkForUpdatesLabel = null;

    private JCheckBox checkForUpdatesCheckBox = null;

    /**
     * This is the default constructor
     */
    public IntroducePortalConfigurationPanel(ConfigurationDescriptorTreeNode treeNode, Object conf) {
        super(treeNode, conf);
        log = Logger.getLogger(this.getClass().getName());
        iConf = (IntroducePortalConfiguration)conf;
        initialize();
        
    }
    
    private IntroducePortalConfiguration getIntroducePortalConfiguration(){
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
            titleLabel.setText("Introduce Portal Configuration");
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
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridy = 3;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 3;
            checkForUpdatesLabel = new JLabel();
            checkForUpdatesLabel.setText("Check For Updates On Startup");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 2;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 2;
            softwareUpdateSiteURLLabel = new JLabel();
            softwareUpdateSiteURLLabel.setText("Software Update Site URL");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 0;
            namespaceReplacementPolicyLabel = new JLabel();
            namespaceReplacementPolicyLabel.setText("Namespace Replacement Policy");
            configurationPanel = new JPanel();
            configurationPanel.setLayout(new GridBagLayout());
            configurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configuration",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            configurationPanel.add(namespaceReplacementPolicyLabel, gridBagConstraints5);
            configurationPanel.add(getNamespaceReplacementPolicyComboBox(), gridBagConstraints6);
            configurationPanel.add(softwareUpdateSiteURLLabel, gridBagConstraints7);
            configurationPanel.add(getSoftwareUpdateSiteURLTextField(), gridBagConstraints8);
            configurationPanel.add(checkForUpdatesLabel, gridBagConstraints3);
            configurationPanel.add(getCheckForUpdatesCheckBox(), gridBagConstraints4);
        }
        return configurationPanel;
    }



    /**
     * This method initializes namespaceReplacementPolicyComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getNamespaceReplacementPolicyComboBox() {
        if (namespaceReplacementPolicyComboBox == null) {
            namespaceReplacementPolicyComboBox = new JComboBox();
            namespaceReplacementPolicyComboBox.addItem(getIntroducePortalConfiguration().getNamespaceReplacementPolicy().ERROR);
            namespaceReplacementPolicyComboBox.addItem(getIntroducePortalConfiguration().getNamespaceReplacementPolicy().IGNORE);
            namespaceReplacementPolicyComboBox.addItem(getIntroducePortalConfiguration().getNamespaceReplacementPolicy().REPLACE);
            
            namespaceReplacementPolicyComboBox.setSelectedItem(getIntroducePortalConfiguration().getNamespaceReplacementPolicy());
            
            namespaceReplacementPolicyComboBox.addActionListener(new ActionListener() {
            
                public void actionPerformed(ActionEvent e) {
                   getIntroducePortalConfiguration().setNamespaceReplacementPolicy((NamespaceReplacementPolicy)namespaceReplacementPolicyComboBox.getSelectedItem());
            
                }
            
            });
        }
        return namespaceReplacementPolicyComboBox;
    }


    /**
     * This method initializes softwareUpdateSiteURLTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getSoftwareUpdateSiteURLTextField() {
        if (softwareUpdateSiteURLTextField == null) {
            softwareUpdateSiteURLTextField = new JTextField();
            softwareUpdateSiteURLTextField.setText(getIntroducePortalConfiguration().getUpdateSiteURL());
            
            softwareUpdateSiteURLTextField.getDocument().addDocumentListener(new DocumentListener() {
            
                public void removeUpdate(DocumentEvent e) {
                    getIntroducePortalConfiguration().setUpdateSiteURL(softwareUpdateSiteURLTextField.getText());
            
                }
            
            
                public void insertUpdate(DocumentEvent e) {
                    getIntroducePortalConfiguration().setUpdateSiteURL(softwareUpdateSiteURLTextField.getText());
            
                }
            
            
                public void changedUpdate(DocumentEvent e) {
                    getIntroducePortalConfiguration().setUpdateSiteURL(softwareUpdateSiteURLTextField.getText());
            
                }
            
            });
        }
        return softwareUpdateSiteURLTextField;
    }

    /**
     * This method initializes checkForUpdatesCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getCheckForUpdatesCheckBox() {
        if (checkForUpdatesCheckBox == null) {
            checkForUpdatesCheckBox = new JCheckBox();
            checkForUpdatesCheckBox.setSelected(getIntroducePortalConfiguration().isCheckForUpdatesOnStartup());
            checkForUpdatesCheckBox.addChangeListener(new ChangeListener() {
            
                public void stateChanged(ChangeEvent e) {
                   getIntroducePortalConfiguration().setCheckForUpdatesOnStartup(getCheckForUpdatesCheckBox().isSelected());
            
                }
            });
        }
        return checkForUpdatesCheckBox;
    }

}
