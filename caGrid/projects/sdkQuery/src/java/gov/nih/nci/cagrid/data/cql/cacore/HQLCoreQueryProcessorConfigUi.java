package gov.nih.nci.cagrid.data.cql.cacore;

import gov.nih.nci.cagrid.data.cql.ui.CQLQueryProcessorConfigUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** 
 *  HQLCoreQueryProcessorConfigUi
 *  UI for configuring the HQL implementation of CQL against
 *  the caCORE SDK v3.1
 * 
 * @author David Ervin
 * 
 * @created Apr 23, 2007 3:49:15 PM
 * @version $Id: HQLCoreQueryProcessorConfigUi.java,v 1.4 2009-01-29 20:14:17 dervin Exp $ 
 */
public class HQLCoreQueryProcessorConfigUi extends CQLQueryProcessorConfigUI {
    
    public static final String APPLICATION_SERVICE_URL = "appserviceUrl";
    public static final String CASE_INSENSITIVE_QUERYING = "queryCaseInsensitive";
    public static final String USE_CSM_FLAG = "useCsmSecurity";
    public static final String CSM_CONTEXT_NAME = "csmContextName";

    private JLabel urlLabel = null;
    private JTextField urlTextField = null;
    private JCheckBox caseInsensitiveCheckBox = null;
    private JCheckBox useCsmCheckBox = null;
    private JLabel csmContextLabel = null;
    private JTextField csmContextTextField = null;
    private JButton copyUrlButton = null;
    private JPanel optionsPanel = null;
    
    
    public HQLCoreQueryProcessorConfigUi() {
        super();
        initialize();
    }
    
    
    private void initialize() {
        // set up the interface layout
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.gridwidth = 3;
        gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints21.gridy = 0;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 2;
        gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints11.gridy = 2;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 2;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.gridx = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridy = 1;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(400, 100));
        this.add(getUrlLabel(), gridBagConstraints);
        this.add(getUrlTextField(), gridBagConstraints1);
        this.add(getCsmContextLabel(), gridBagConstraints2);
        this.add(getCsmContextTextField(), gridBagConstraints3);
        this.add(getCopyUrlButton(), gridBagConstraints11);
        this.add(getOptionsPanel(), gridBagConstraints21);
    }


    /**
     * This method initializes urlLabel
     */
    private JLabel getUrlLabel() {
        if (urlLabel == null) {
            urlLabel = new JLabel();
            urlLabel.setText("Remote Service URL:");
        }
        return urlLabel;
    }


    /**
     * This method initializes urlTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUrlTextField() {
        if (urlTextField == null) {
            urlTextField = new JTextField();
        }
        return urlTextField;
    }


    /**
     * This method initializes caseInsensitiveCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCaseInsensitiveCheckBox() {
        if (caseInsensitiveCheckBox == null) {
            caseInsensitiveCheckBox = new JCheckBox();
            caseInsensitiveCheckBox.setText("Case Insensitive Queries");
        }
        return caseInsensitiveCheckBox;
    }


    /**
     * This method initializes useCsmCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getUseCsmCheckBox() {
        if (useCsmCheckBox == null) {
            useCsmCheckBox = new JCheckBox();
            useCsmCheckBox.setText("Use CSM Security");
            useCsmCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    setCsmConfigEnabled(useCsmCheckBox.isSelected());
                }
            });
        }
        return useCsmCheckBox;
    }


    /**
     * This method initializes csmContextLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getCsmContextLabel() {
        if (csmContextLabel == null) {
            csmContextLabel = new JLabel();
            csmContextLabel.setText("CSM Context Name:");
        }
        return csmContextLabel;
    }


    /**
     * This method initializes csmContextTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCsmContextTextField() {
        if (csmContextTextField == null) {
            csmContextTextField = new JTextField();
        }
        return csmContextTextField;
    }


    /**
     * This method initializes copyUrlButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCopyUrlButton() {
        if (copyUrlButton == null) {
            copyUrlButton = new JButton();
            copyUrlButton.setText("Copy App URL");
            copyUrlButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String url = getUrlTextField().getText();
                    getCsmContextTextField().setText(url);
                }
            });
        }
        return copyUrlButton;
    }


    /**
     * This method initializes optionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getOptionsPanel() {
        if (optionsPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 0;
            optionsPanel = new JPanel();
            optionsPanel.setLayout(new GridBagLayout());
            optionsPanel.add(getCaseInsensitiveCheckBox(), gridBagConstraints4);
            optionsPanel.add(getUseCsmCheckBox(), gridBagConstraints5);
        }
        return optionsPanel;
    }


    private void setCsmConfigEnabled(boolean enable) {
        getCsmContextLabel().setEnabled(enable);
        getCsmContextTextField().setEnabled(enable);
        if (!enable) {
            getCsmContextTextField().setText("");
        }
        getCopyUrlButton().setEnabled(enable);
    }
    
    
    public void setUpUi(File serviceDir, Properties cqlProcessorProperties) {
        String serviceUrl = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.APPLICATION_SERVICE_URL);
        getUrlTextField().setText(serviceUrl);
        String caseInsensitiveValue = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.CASE_INSENSITIVE_QUERYING);
        if (caseInsensitiveValue != null) {
            getCaseInsensitiveCheckBox().setSelected(
                Boolean.valueOf(caseInsensitiveValue).booleanValue());
        }
        String useCsmValue = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.USE_CSM_FLAG);
        if (useCsmValue != null) {
            boolean csmSelected = Boolean.valueOf(useCsmValue).booleanValue();
            getUseCsmCheckBox().setSelected(csmSelected);
            setCsmConfigEnabled(csmSelected);
        }
        String csmContext = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.CSM_CONTEXT_NAME);
        getCsmContextTextField().setText(csmContext);
    }
    

    public Properties getConfiguredProperties() {
        Properties props = new Properties();
        props.setProperty(HQLCoreQueryProcessor.APPLICATION_SERVICE_URL, getUrlTextField().getText());
        props.setProperty(HQLCoreQueryProcessor.CASE_INSENSITIVE_QUERYING, 
            String.valueOf(getCaseInsensitiveCheckBox().isSelected()));
        props.setProperty(HQLCoreQueryProcessor.USE_CSM_FLAG, 
            String.valueOf(getUseCsmCheckBox().isSelected()));
        props.setProperty(HQLCoreQueryProcessor.CSM_CONTEXT_NAME, getCsmContextTextField().getText());
        return props;
    }
    
    
    public Dimension getPreferredSize() {
        return new Dimension(450, 120);
    }
}
