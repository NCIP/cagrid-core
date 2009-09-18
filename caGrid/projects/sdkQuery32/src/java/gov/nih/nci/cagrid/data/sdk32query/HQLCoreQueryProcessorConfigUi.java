package gov.nih.nci.cagrid.data.sdk32query;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.data.cql.ui.CQLQueryProcessorConfigUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  HQLCoreQueryProcessorConfigUi
 *  UI for configuring the HQL implementation of CQL against
 *  the caCORE SDK v3.1
 * 
 * @author David Ervin
 * 
 * @created Apr 24, 2007 12:06:11 PM
 * @version $Id: HQLCoreQueryProcessorConfigUi.java,v 1.3 2009-01-29 20:14:19 dervin Exp $ 
 */
public class HQLCoreQueryProcessorConfigUi extends CQLQueryProcessorConfigUI {
    
    public static final String APPLICATION_SERVICE_URL = "appserviceUrl";
    public static final String USE_CSM_FLAG = "useCsmSecurity";
    public static final String CASE_INSENSITIVE_QUERYING = "queryCaseInsensitive";
    public static final String CSM_CONFIGURATION_FILENAME = "csmConfigurationFilename";
    public static final String CSM_CONTEXT_NAME = "csmContextName";
    public static final String USE_LOCAL_APPSERVICE = "useLocalAppservice";

    private JCheckBox useLocalCheckBox = null;
    private JCheckBox caseInsensitiveCheckBox = null;
    private JCheckBox useCsmCheckBox = null;
    private JPanel checkBoxPanel = null;
    private JLabel urlLabel = null;
    private JTextField urlTextField = null;
    private JLabel csmContextLabel = null;
    private JTextField csmContextTextField = null;
    private JButton copyUrlButton = null;
    private JLabel csmConfigLabel = null;
    private JTextField csmConfigTextField = null;
    private JButton browseButton = null;
    private JPanel inputPanel = null;
    
    private File serviceDir = null;
    
    public HQLCoreQueryProcessorConfigUi() {
        super();
        initialize();
    }
    

    public Properties getConfiguredProperties() {
        Properties props = new Properties();
        props.setProperty(HQLCoreQueryProcessor.APPLICATION_SERVICE_URL, 
            getUrlTextField().getText());
        props.setProperty(HQLCoreQueryProcessor.CASE_INSENSITIVE_QUERYING,
            String.valueOf(getCaseInsensitiveCheckBox().isSelected()));
        props.setProperty(HQLCoreQueryProcessor.USE_LOCAL_APPSERVICE,
            String.valueOf(getUseLocalCheckBox().isSelected()));
        props.setProperty(HQLCoreQueryProcessor.USE_CSM_FLAG,
            String.valueOf(getUseCsmCheckBox().isSelected()));
        props.setProperty(HQLCoreQueryProcessor.CSM_CONTEXT_NAME,
            getCsmContextTextField().getText());
        props.setProperty(HQLCoreQueryProcessor.CSM_CONFIGURATION_FILENAME,
            getCsmConfigTextField().getText());
        return props;
    }


    public void setUpUi(File serviceDir, Properties cqlProcessorProperties) {
        this.serviceDir = serviceDir;
        String serviceUrl = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.APPLICATION_SERVICE_URL);
        getUrlTextField().setText(serviceUrl);
        String caseInsensitiveValue = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.CASE_INSENSITIVE_QUERYING);
        boolean caseInsensitive = Boolean.valueOf(caseInsensitiveValue).booleanValue();
        getCaseInsensitiveCheckBox().setSelected(caseInsensitive);
        String csmConfigFilename = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.CSM_CONFIGURATION_FILENAME);
        getCsmConfigTextField().setText(csmConfigFilename);
        String csmContextName = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.CSM_CONTEXT_NAME);
        getCsmContextTextField().setText(csmContextName);
        String useCsmValue = cqlProcessorProperties.getProperty(
            HQLCoreQueryProcessor.USE_CSM_FLAG);
        boolean useCsm = Boolean.valueOf(useCsmValue).booleanValue();
        getUseCsmCheckBox().setSelected(useCsm);
        String useLocalValue = cqlProcessorProperties.getProperty(HQLCoreQueryProcessor.USE_LOCAL_APPSERVICE);
        boolean useLocal = Boolean.valueOf(useLocalValue).booleanValue();
        getUseLocalCheckBox().setSelected(useLocal);
        enableRelaventComponents();
    }
    
    
    public Dimension getPreferredSize() {
        return new Dimension(500, 150);
    }
    
    
    private void initialize() {
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.gridx = 0;
        gridBagConstraints9.fill = GridBagConstraints.BOTH;
        gridBagConstraints9.weightx = 1.0D;
        gridBagConstraints9.anchor = GridBagConstraints.NORTH;
        gridBagConstraints9.gridheight = 2;
        gridBagConstraints9.gridy = 1;
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints8.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.add(getCheckBoxPanel(), gridBagConstraints8);
        this.add(getInputPanel(), gridBagConstraints9);
    }


    /**
     * This method initializes useLocalCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getUseLocalCheckBox() {
        if (useLocalCheckBox == null) {
            useLocalCheckBox = new JCheckBox();
            useLocalCheckBox.setText("Use Local Service API");
            useLocalCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            useLocalCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    enableRelaventComponents();
                }
            });
        }
        return useLocalCheckBox;
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
            caseInsensitiveCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
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
            useCsmCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            useCsmCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    enableRelaventComponents();
                }
            });
        }
        return useCsmCheckBox;
    }


    /**
     * This method initializes checkBoxPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCheckBoxPanel() {
        if (checkBoxPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(2);
            gridLayout.setColumns(3);
            checkBoxPanel = new JPanel();
            checkBoxPanel.setLayout(gridLayout);
            checkBoxPanel.add(getUseLocalCheckBox(), null);
            checkBoxPanel.add(getCaseInsensitiveCheckBox(), null);
            checkBoxPanel.add(getUseCsmCheckBox(), null);
        }
        return checkBoxPanel;
    }


    /**
     * This method initializes urlLabel
     * 
     * @return javax.swing.JLabel
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
     * This method initializes csmConfigLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getCsmConfigLabel() {
        if (csmConfigLabel == null) {
            csmConfigLabel = new JLabel();
            csmConfigLabel.setText("CSM Configuration File:");
        }
        return csmConfigLabel;
    }


    /**
     * This method initializes csmConfigTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCsmConfigTextField() {
        if (csmConfigTextField == null) {
            csmConfigTextField = new JTextField();
            csmConfigTextField.setEditable(false);
        }
        return csmConfigTextField;
    }


    /**
     * This method initializes browseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String etcDir = serviceDir.getAbsolutePath() 
                        + File.separator + "etc";
                    if (getCsmConfigTextField().getText().length() != 0) {
                        // delete any old config file
                        File oldConfig = new File(etcDir + File.separator 
                            + getCsmConfigTextField().getText());
                        if (oldConfig.exists()) {
                            oldConfig.delete();
                        }
                    }
                    JFileChooser chooser = new JFileChooser();
                    chooser.setApproveButtonText("Select");
                    int choice = chooser.showOpenDialog(HQLCoreQueryProcessorConfigUi.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        File originalFile = chooser.getSelectedFile();
                        File outputFile = new File(serviceDir.getAbsolutePath() 
                            + File.separator + "etc" + File.separator + originalFile.getName());
                        try {
                            Utils.copyFile(originalFile, outputFile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error copying selected " +
                                "file to service directory: " + ex.getMessage(), ex);
                        }
                        getCsmConfigTextField().setText(outputFile.getName());
                    }
                }
            });
        }
        return browseButton;
    }


    /**
     * This method initializes inputPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInputPanel() {
        if (inputPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 2;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 2;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 2;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            inputPanel = new JPanel();
            inputPanel.setLayout(new GridBagLayout());
            inputPanel.add(getUrlLabel(), gridBagConstraints);
            inputPanel.add(getCsmContextLabel(), gridBagConstraints1);
            inputPanel.add(getCsmConfigLabel(), gridBagConstraints2);
            inputPanel.add(getUrlTextField(), gridBagConstraints3);
            inputPanel.add(getCsmContextTextField(), gridBagConstraints4);
            inputPanel.add(getCsmConfigTextField(), gridBagConstraints5);
            inputPanel.add(getCopyUrlButton(), gridBagConstraints6);
            inputPanel.add(getBrowseButton(), gridBagConstraints7);
        }
        return inputPanel;
    }
    
    
    private void enableRelaventComponents() {
        boolean localChecked = getUseLocalCheckBox().isSelected();
        boolean csmChecked = getUseCsmCheckBox().isSelected();
        
        PortalUtils.setContainerEnabled(getInputPanel(), true);
        
        getUseCsmCheckBox().setEnabled(true);

        getCsmContextLabel().setEnabled(csmChecked);
        getCsmContextTextField().setEnabled(csmChecked);
        getCsmContextTextField().setText("");
        getCopyUrlButton().setEnabled(csmChecked);

        getCsmConfigLabel().setEnabled(csmChecked);
        getCsmConfigTextField().setEnabled(csmChecked);
        getCsmConfigTextField().setText("");
        getBrowseButton().setEnabled(csmChecked);
        
        if (localChecked) {
            PortalUtils.setContainerEnabled(getInputPanel(), false);
            getUseCsmCheckBox().setSelected(false);
            getUseCsmCheckBox().setEnabled(false);
            getUrlTextField().setText("");
        }
    }
}
