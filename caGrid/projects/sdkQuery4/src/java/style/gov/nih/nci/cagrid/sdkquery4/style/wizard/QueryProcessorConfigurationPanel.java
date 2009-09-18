package gov.nih.nci.cagrid.sdkquery4.style.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.GroupSelectionListener;
import gov.nih.nci.cagrid.data.ui.NotifyingButtonGroup;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.data.ui.wizard.OneTimeInfoDialogUtil;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.sdkquery4.style.wizard.config.QueryProcessorBaseConfigurationStep;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/** 
 *  QueryProcessorConfigurationPanel
 *  Panel to configure the caCORE SDK Query Processor
 * 
 * @author David Ervin
 * 
 * @created Nov 27, 2007 4:50:32 PM
 * @version $Id: QueryProcessorConfigurationPanel.java,v 1.20 2009-02-05 20:33:36 dervin Exp $ 
 */
public class QueryProcessorConfigurationPanel extends AbstractWizardPanel {
    // keys for validation
    public static final String KEY_OUTPUT_DIRECTORY = "SDK Output Directory";
    public static final String KEY_APPLICATION_NAME = "Application name";
    public static final String KEY_BEANS_JAR = "Beans Jar file";
    public static final String KEY_LOCAL_CONFIG_DIR = "Local configuration directory";
    public static final String KEY_REMOTE_CONFIG_DIR = "Remote configuration directory";
    public static final String KEY_ORM_JAR = "ORM Jar file";
    public static final String KEY_HOST_NAME = "Application service host name";
    public static final String KEY_PORT_NUMBER = "Application port number";
    
    // bit bucket key for the beans jar filename
    public static final String KEY_BEANS_JAR_FILENAME = "SDK 4.0 Beans Jar";

    private JLabel applicationNameLabel = null;
    private JTextField applicationNameTextField = null;
    private JLabel beansJarLabel = null;
    private JTextField beansJarTextField = null;
    private JButton beansBrowseButton = null;
    private JLabel localConfigDirLabel = null;
    private JTextField localConfigDirTextField = null;
    private JButton localConfigBrowseButton = null;
    private JPanel basicConfigPanel = null;
    private JRadioButton localApiRadioButton = null;
    private JRadioButton remoteApiRadioButton = null;
    private JLabel ormJarLabel = null;
    private JTextField ormJarTextField = null;
    private JButton ormJarBrowseButton = null;
    private JLabel hostNameLabel = null;
    private JTextField hostNameTextField = null;
    private JLabel portLabel = null;
    private JTextField portTextField = null;
    private JPanel localApiPanel = null;
    private JPanel remoteApiPanel = null;
    private JPanel apiConfigPanel = null;
    private JPanel mainPanel = null;
    private JCheckBox caseInsensitiveCheckBox = null;
    private JRadioButton simpleConfigRadioButton = null;
    private JRadioButton advancedRadioButton = null;
    private JPanel configTypePanel = null;
    private JLabel outputDirLabel = null;
    private JTextField outputDirTextField = null;
    private JButton outputDirBrowseButton = null;
    private JPanel outputDirSelectionPanel = null;
    private JLabel remoteConfigDirLabel = null;
    private JTextField remoteConfigDirTextField = null;
    private JButton remoteConfigBrowseButton = null;
    
    private IconFeedbackPanel validationPanel = null;
    private ValidationResultModel validationModel = null;
    private DocumentChangeAdapter documentChangeListener = null;
    
    private QueryProcessorBaseConfigurationStep configurationStep = null;

    /**
     * @param extensionDescription
     * @param info
     */
    public QueryProcessorConfigurationPanel(
        ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.configurationStep = new QueryProcessorBaseConfigurationStep(info);
        this.documentChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }


    public String getPanelShortName() {
        return "Configuration";
    }


    public String getPanelTitle() {
        return "caCORE Query Processor Configuration";
    }


    public void update() {
        // This method is (mostly) blank, since it's the first panel to run, 
        // and no other panel changes the values used by this panel
        validateInput();
    }
    
    
    public void movingNext() {
        // called when the 'next' button is clicked
        // copy the beans jar in to the service
        try {
            configurationStep.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error applying configuration", ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
        initRadioGroups();
        this.setLayout(new GridLayout());
        this.add(getValidationPanel());
        // set up for validation
        configureValidation();
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            validationPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationPanel;
    }
    
    
    private void initRadioGroups() {
        NotifyingButtonGroup apiRadioGroup = new NotifyingButtonGroup();
        apiRadioGroup.addGroupSelectionListener(new GroupSelectionListener() {
            public void selectionChanged(ButtonModel previousSelection, ButtonModel currentSelection) {
                validateInput();
            }
        });
        apiRadioGroup.add(getLocalApiRadioButton());
        apiRadioGroup.add(getRemoteApiRadioButton());
        apiRadioGroup.setSelected(getLocalApiRadioButton().getModel(), true);
        setLocalRemoteComponentsEnabled();
        
        NotifyingButtonGroup configTypeGroup = new NotifyingButtonGroup();
        configTypeGroup.addGroupSelectionListener(new GroupSelectionListener() {
            public void selectionChanged(ButtonModel previousSelection, ButtonModel currentSelection) {
                validateInput();
            }
        });
        configTypeGroup.add(getSimpleConfigRadioButton());
        configTypeGroup.add(getAdvancedRadioButton());
        configTypeGroup.setSelected(getSimpleConfigRadioButton().getModel(), true);
        setSimpleAdvancedComponentsEnabled();
    }
    
    
    /**
     * This method initializes applicationNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getApplicationNameLabel() {
        if (applicationNameLabel == null) {
            applicationNameLabel = new JLabel();
            applicationNameLabel.setText("Application Name:");
        }
        return applicationNameLabel;
    }


    /**
     * This method initializes applicationNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getApplicationNameTextField() {
        if (applicationNameTextField == null) {
            applicationNameTextField = new JTextField();
            applicationNameTextField.setToolTipText("The name of the caCORE SDK application");
            applicationNameTextField.getDocument().addDocumentListener(documentChangeListener);
            applicationNameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configurationStep.setApplicationName(getApplicationNameTextField().getText());
                }
            });
        }
        return applicationNameTextField;
    }


    /**
     * This method initializes beansJarLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getBeansJarLabel() {
        if (beansJarLabel == null) {
            beansJarLabel = new JLabel();
            beansJarLabel.setText("Beans Jar:");
        }
        return beansJarLabel;
    }


    /**
     * This method initializes beansJarTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getBeansJarTextField() {
        if (beansJarTextField == null) {
            beansJarTextField = new JTextField();
            beansJarTextField.setEditable(false);
            beansJarTextField.setToolTipText("The domain data type beans jar file");
            beansJarTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return beansJarTextField;
    }


    /**
     * This method initializes beansBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getBeansBrowseButton() {
        if (beansBrowseButton == null) {
            beansBrowseButton = new JButton();
            beansBrowseButton.setText("Browse");
            beansBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String[] message = {
                        "Select the jar within your caCORE SDK installation which",
                        "contains the generated domain data type beans.",
                        "Typically, this jar fill will be",
                        "output" + File.separator + "<application>" + File.separator + 
                            "package" + File.separator + "local-client" + File.separator + "<application>-beans.jar"
                    };
                    OneTimeInfoDialogUtil.showInfoDialog(
                        QueryProcessorConfigurationPanel.class, KEY_BEANS_JAR, message);
                    try {
                        String fullFilename = ResourceManager.promptFile(null, FileFilters.JAR_FILTER);
                        if (getBeansJarTextField().getText().length() != 0) {
                            File originalFile = new File(getBeansJarTextField().getText());
                            File copiedFile = new File(getServiceInformation().getBaseDirectory(), 
                                "lib" + File.separator + originalFile.getName());
                            if (copiedFile.exists()) {
                                copiedFile.delete();
                            }
                        }
                        getBeansJarTextField().setText(fullFilename);
                        configurationStep.setBeansJarLocation(fullFilename);
                        validateInput();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return beansBrowseButton;
    }


    /**
     * This method initializes localConfigDirLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getLocalConfigDirLabel() {
        if (localConfigDirLabel == null) {
            localConfigDirLabel = new JLabel();
            localConfigDirLabel.setText("Local Conf Directory:");
        }
        return localConfigDirLabel;
    }


    /**
     * This method initializes localConfigDirTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getLocalConfigDirTextField() {
        if (localConfigDirTextField == null) {
            localConfigDirTextField = new JTextField();
            localConfigDirTextField.setEditable(false);
            localConfigDirTextField.setToolTipText("The caCORE SDK local client configuration directory");
            localConfigDirTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return localConfigDirTextField;
    }


    /**
     * This method initializes localConfigBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getLocalConfigBrowseButton() {
        if (localConfigBrowseButton == null) {
            localConfigBrowseButton = new JButton();
            localConfigBrowseButton.setText("Browse");
            localConfigBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String[] message = {
                        "Select the directory within your caCORE SDK installation which",
                        "contains the local application service configuration files.",
                        "Typically, this directory will be",
                        "output" + File.separator + "<application>" + File.separator + 
                            "package" + File.separator + "local-client" + File.separator + "conf"
                    };
                    OneTimeInfoDialogUtil.showInfoDialog(
                        QueryProcessorConfigurationPanel.class, KEY_LOCAL_CONFIG_DIR, message);
                    try {
                        String filename = ResourceManager.promptDir(null);
                        getLocalConfigDirTextField().setText(filename);
                        configurationStep.setLocalConfigDir(filename);
                        validateInput();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return localConfigBrowseButton;
    }


    /**
     * This method initializes basicConfigPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getBasicConfigPanel() {
        if (basicConfigPanel == null) {
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 2;
            gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints31.gridy = 4;
            GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
            gridBagConstraints28.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints28.gridy = 4;
            gridBagConstraints28.weightx = 1.0;
            gridBagConstraints28.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints28.gridx = 1;
            GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
            gridBagConstraints111.gridx = 0;
            gridBagConstraints111.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints111.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints111.gridy = 4;
            GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
            gridBagConstraints110.gridx = 0;
            gridBagConstraints110.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints110.gridwidth = 3;
            gridBagConstraints110.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints110.gridy = 5;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 2;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 3;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 3;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 3;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 2;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 0;
            basicConfigPanel = new JPanel();
            basicConfigPanel.setLayout(new GridBagLayout());
            basicConfigPanel.add(getApplicationNameLabel(), gridBagConstraints);
            basicConfigPanel.add(getApplicationNameTextField(), gridBagConstraints1);
            basicConfigPanel.add(getBeansJarLabel(), gridBagConstraints2);
            basicConfigPanel.add(getBeansJarTextField(), gridBagConstraints3);
            basicConfigPanel.add(getBeansBrowseButton(), gridBagConstraints4);
            basicConfigPanel.add(getLocalConfigDirLabel(), gridBagConstraints5);
            basicConfigPanel.add(getLocalConfigDirTextField(), gridBagConstraints6);
            basicConfigPanel.add(getLocalConfigBrowseButton(), gridBagConstraints7);
            basicConfigPanel.add(getCaseInsensitiveCheckBox(), gridBagConstraints110);
            basicConfigPanel.add(getRemoteConfigDirLabel(), gridBagConstraints111);
            basicConfigPanel.add(getRemoteConfigDirTextField(), gridBagConstraints28);
            basicConfigPanel.add(getRemoteConfigBrowseButton(), gridBagConstraints31);
        }
        return basicConfigPanel;
    }


    /**
     * This method initializes localApiRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getLocalApiRadioButton() {
        if (localApiRadioButton == null) {
            localApiRadioButton = new JRadioButton();
            localApiRadioButton.setText("Local API");
            localApiRadioButton.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    setLocalRemoteComponentsEnabled();
                }
            });
        }
        return localApiRadioButton;
    }


    /**
     * This method initializes remoteApiRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getRemoteApiRadioButton() {
        if (remoteApiRadioButton == null) {
            remoteApiRadioButton = new JRadioButton();
            remoteApiRadioButton.setText("Remote API");
            remoteApiRadioButton.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    setLocalRemoteComponentsEnabled();
                }
            });
        }
        return remoteApiRadioButton;
    }


    /**
     * This method initializes ormJarLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getOrmJarLabel() {
        if (ormJarLabel == null) {
            ormJarLabel = new JLabel();
            ormJarLabel.setText("ORM Jar:");
        }
        return ormJarLabel;
    }


    /**
     * This method initializes ormJarTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getOrmJarTextField() {
        if (ormJarTextField == null) {
            ormJarTextField = new JTextField();
            ormJarTextField.setEditable(false);
            ormJarTextField.setToolTipText("The caCORE SDK Object-Relational-Mapping Jar file");
            ormJarTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return ormJarTextField;
    }


    /**
     * This method initializes ormJarBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getOrmJarBrowseButton() {
        if (ormJarBrowseButton == null) {
            ormJarBrowseButton = new JButton();
            ormJarBrowseButton.setText("Browse");
            ormJarBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String[] message = {
                        "Select the jar within your caCORE SDK installation which",
                        "contains the object relational mapping files.",
                        "Typically, this jar fill will be",
                        "output" + File.separator + "<application>" + File.separator + 
                            "package" + File.separator + "local-client" + File.separator + "<application>-orm.jar"
                    };
                    OneTimeInfoDialogUtil.showInfoDialog(
                        QueryProcessorConfigurationPanel.class, KEY_ORM_JAR, message);
                    try {
                        String filename = ResourceManager.promptFile(null, FileFilters.JAR_FILTER);
                        if (getOrmJarTextField().getText().length() != 0) {
                            // TODO: remove old orm jar text field from service lib dir
                            File oldJar = new File(getOrmJarTextField().getText());
                            File copiedOldJar = new File(getServiceInformation().getBaseDirectory(),
                                "lib" + File.separator + oldJar.getName());
                            if (copiedOldJar.exists()) {
                                copiedOldJar.delete();
                            }
                        }
                        getOrmJarTextField().setText(filename);
                        configurationStep.setOrmJarLocation(filename);
                        validateInput();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return ormJarBrowseButton;
    }
    
    
    /**
     * This method initializes caseInsensitiveCheckBox  
     *  
     * @return javax.swing.JCheckBox    
     */
    private JCheckBox getCaseInsensitiveCheckBox() {
        if (caseInsensitiveCheckBox == null) {
            caseInsensitiveCheckBox = new JCheckBox();
            caseInsensitiveCheckBox.setText("Case Insensitive Querying");
            caseInsensitiveCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    configurationStep.setCaseInsensitiveQueries(
                        getCaseInsensitiveCheckBox().isSelected());
                    validateInput();
                }
            });
        }
        return caseInsensitiveCheckBox;
    }


    /**
     * This method initializes hostNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getHostNameLabel() {
        if (hostNameLabel == null) {
            hostNameLabel = new JLabel();
            hostNameLabel.setText("Host Name:");
        }
        return hostNameLabel;
    }


    /**
     * This method initializes hostNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getHostNameTextField() {
        if (hostNameTextField == null) {
            hostNameTextField = new JTextField();
            hostNameTextField.setToolTipText("The network host name of the caCORE SDK system (eg. http://example.com)");
            hostNameTextField.getDocument().addDocumentListener(documentChangeListener);
            hostNameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configurationStep.setHostName(getHostNameTextField().getText());
                }
            });
        }
        return hostNameTextField;
    }


    /**
     * This method initializes portLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getPortLabel() {
        if (portLabel == null) {
            portLabel = new JLabel();
            portLabel.setText("Port:");
        }
        return portLabel;
    }


    /**
     * This method initializes portTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.setToolTipText("The network port of the caCORE SDK system (eg. 8080)");
            portTextField.getDocument().addDocumentListener(documentChangeListener);
            portTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    try {
                        configurationStep.setHostPort(Integer.valueOf(portTextField.getText()));
                    } catch (Exception ex) {
                        // this will happen if the value isn't an integer
                    }
                }
            });
        }
        return portTextField;
    }


    /**
     * This method initializes localApiPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getLocalApiPanel() {
        if (localApiPanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 2;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 0;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridy = 0;
            localApiPanel = new JPanel();
            localApiPanel.setLayout(new GridBagLayout());
            localApiPanel.add(getOrmJarLabel(), gridBagConstraints8);
            localApiPanel.add(getOrmJarTextField(), gridBagConstraints9);
            localApiPanel.add(getOrmJarBrowseButton(), gridBagConstraints10);
        }
        return localApiPanel;
    }


    /**
     * This method initializes remoteApiPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getRemoteApiPanel() {
        if (remoteApiPanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.gridy = 1;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 0;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 0;
            remoteApiPanel = new JPanel();
            remoteApiPanel.setLayout(new GridBagLayout());
            remoteApiPanel.add(getHostNameLabel(), gridBagConstraints11);
            remoteApiPanel.add(getHostNameTextField(), gridBagConstraints12);
            remoteApiPanel.add(getPortLabel(), gridBagConstraints13);
            remoteApiPanel.add(getPortTextField(), gridBagConstraints14);
        }
        return remoteApiPanel;
    }


    /**
     * This method initializes apiConfigPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getApiConfigPanel() {
        if (apiConfigPanel == null) {
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 1;
            gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.weightx = 1.0D;
            gridBagConstraints18.gridy = 1;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 1;
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.weightx = 1.0D;
            gridBagConstraints17.gridy = 0;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.anchor = GridBagConstraints.NORTH;
            gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints16.gridy = 1;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.anchor = GridBagConstraints.NORTH;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints15.gridy = 0;
            apiConfigPanel = new JPanel();
            apiConfigPanel.setLayout(new GridBagLayout());
            apiConfigPanel.add(getLocalApiRadioButton(), gridBagConstraints15);
            apiConfigPanel.add(getRemoteApiRadioButton(), gridBagConstraints16);
            apiConfigPanel.add(getLocalApiPanel(), gridBagConstraints17);
            apiConfigPanel.add(getRemoteApiPanel(), gridBagConstraints18);
        }
        return apiConfigPanel;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weightx = 0.25D;
            gridBagConstraints21.gridy = 0;
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 0;
            gridBagConstraints20.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints20.weightx = 1.0D;
            gridBagConstraints20.gridy = 2;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints19.weightx = 1.0D;
            gridBagConstraints19.gridy = 1;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getBasicConfigPanel(), gridBagConstraints19);
            mainPanel.add(getApiConfigPanel(), gridBagConstraints20);
            mainPanel.add(getConfigTypePanel(), gridBagConstraints21);
        }
        return mainPanel;
    }
    
    
    /**
     * This method initializes simpleConfigRadioButton  
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getSimpleConfigRadioButton() {
        if (simpleConfigRadioButton == null) {
            simpleConfigRadioButton = new JRadioButton();
            simpleConfigRadioButton.setText("Simple");
            simpleConfigRadioButton.setToolTipText("Configure most options automatically");
            simpleConfigRadioButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    setSimpleAdvancedComponentsEnabled();
                }
            });
        }
        return simpleConfigRadioButton;
    }


    /**
     * This method initializes advancedRadioButton  
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getAdvancedRadioButton() {
        if (advancedRadioButton == null) {
            advancedRadioButton = new JRadioButton();
            advancedRadioButton.setText("Advanced");
            advancedRadioButton.setToolTipText("Configure each option individually");
            advancedRadioButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    setSimpleAdvancedComponentsEnabled();
                }
            });
        }
        return advancedRadioButton;
    }


    /**
     * This method initializes configTypePanel  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getConfigTypePanel() {
        if (configTypePanel == null) {
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints25.gridy = 0;
            gridBagConstraints25.weightx = 1.0D;
            gridBagConstraints25.gridx = 2;
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints27.gridy = 0;
            gridBagConstraints27.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints27.gridx = 0;
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints26.gridy = 0;
            gridBagConstraints26.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints26.gridx = 1;
            configTypePanel = new JPanel();
            configTypePanel.setLayout(new GridBagLayout());
            configTypePanel.add(getSimpleConfigRadioButton(), gridBagConstraints26);
            configTypePanel.add(getAdvancedRadioButton(), gridBagConstraints27);
            configTypePanel.add(getOutputDirSelectionPanel(), gridBagConstraints25);
        }
        return configTypePanel;
    }


    /**
     * This method initializes outputDirLabel   
     *  
     * @return javax.swing.JLabel   
     */
    private JLabel getOutputDirLabel() {
        if (outputDirLabel == null) {
            outputDirLabel = new JLabel();
            outputDirLabel.setText("SDK Output Directory:");
        }
        return outputDirLabel;
    }


    /**
     * This method initializes outputDirTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getOutputDirTextField() {
        if (outputDirTextField == null) {
            outputDirTextField = new JTextField();
            outputDirTextField.setToolTipText("The output directory generated by the caCORE SDK");
            outputDirTextField.setEditable(false);
        }
        return outputDirTextField;
    }


    /**
     * This method initializes outputDirBrowseButton    
     *  
     * @return javax.swing.JButton  
     */
    private JButton getOutputDirBrowseButton() {
        if (outputDirBrowseButton == null) {
            outputDirBrowseButton = new JButton();
            outputDirBrowseButton.setText("Browse");
            outputDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        selectSdkOutputDirectory();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error processing selection", ex.getMessage(), ex);
                    }
                }
            });
        }
        return outputDirBrowseButton;
    }


    /**
     * This method initializes outputDirSelectionPanel  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getOutputDirSelectionPanel() {
        if (outputDirSelectionPanel == null) {
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.gridx = 3;
            gridBagConstraints24.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints24.gridy = 0;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints23.gridy = 0;
            gridBagConstraints23.weightx = 1.0;
            gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints23.gridx = 2;
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 1;
            gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints22.gridy = 0;
            outputDirSelectionPanel = new JPanel();
            outputDirSelectionPanel.setLayout(new GridBagLayout());
            outputDirSelectionPanel.add(getOutputDirLabel(), gridBagConstraints22);
            outputDirSelectionPanel.add(getOutputDirTextField(), gridBagConstraints23);
            outputDirSelectionPanel.add(getOutputDirBrowseButton(), gridBagConstraints24);
        }
        return outputDirSelectionPanel;
    }
    
    
    /**
     * This method initializes remoteConfigDirLabel 
     *  
     * @return javax.swing.JLabel   
     */
    private JLabel getRemoteConfigDirLabel() {
        if (remoteConfigDirLabel == null) {
            remoteConfigDirLabel = new JLabel();
            remoteConfigDirLabel.setText("Remote Conf Directory:");
        }
        return remoteConfigDirLabel;
    }


    /**
     * This method initializes remoteConfigDirTextField 
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getRemoteConfigDirTextField() {
        if (remoteConfigDirTextField == null) {
            remoteConfigDirTextField = new JTextField();
            remoteConfigDirTextField.setToolTipText("The caCORE SDK remote client configuration directory");
            remoteConfigDirTextField.setEditable(false);
        }
        return remoteConfigDirTextField;
    }


    /**
     * This method initializes remoteConfigBrowseButton 
     *  
     * @return javax.swing.JButton  
     */
    private JButton getRemoteConfigBrowseButton() {
        if (remoteConfigBrowseButton == null) {
            remoteConfigBrowseButton = new JButton();
            remoteConfigBrowseButton.setText("Browse");
            remoteConfigBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String[] message = {
                        "Select the directory within your caCORE SDK installation which",
                        "contains the remote application service configuration files.",
                        "Typically, this directory will be",
                        "output" + File.separator + "<application>" + File.separator + 
                            "package" + File.separator + "remote-client" + File.separator + "conf"
                    };
                    OneTimeInfoDialogUtil.showInfoDialog(
                        QueryProcessorConfigurationPanel.class, KEY_REMOTE_CONFIG_DIR, message);
                    try {
                        String filename = ResourceManager.promptDir(null);
                        getRemoteConfigDirTextField().setText(filename);
                        configurationStep.setRemoteConfigDir(filename);
                        validateInput();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return remoteConfigBrowseButton;
    }
    
    
    // -----------
    // validation
    // -----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getOutputDirTextField(), KEY_OUTPUT_DIRECTORY);
        ValidationComponentUtils.setMessageKey(getApplicationNameTextField(), KEY_APPLICATION_NAME);
        ValidationComponentUtils.setMessageKey(getBeansJarTextField(), KEY_BEANS_JAR);
        ValidationComponentUtils.setMessageKey(getLocalConfigDirTextField(), KEY_LOCAL_CONFIG_DIR);
        ValidationComponentUtils.setMessageKey(getRemoteConfigDirTextField(), KEY_REMOTE_CONFIG_DIR);
        ValidationComponentUtils.setMessageKey(getOrmJarTextField(), KEY_ORM_JAR);
        ValidationComponentUtils.setMessageKey(getHostNameTextField(), KEY_HOST_NAME);
        ValidationComponentUtils.setMessageKey(getPortTextField(), KEY_PORT_NUMBER);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        String appName = getApplicationNameTextField().getText();
        if (ValidationUtils.isBlank(appName)) {
            result.add(new SimpleValidationMessage(
                KEY_APPLICATION_NAME + " cannot be blank", Severity.ERROR, KEY_APPLICATION_NAME));
        } else if (appName.split("\\s").length != 1) {
            result.add(new SimpleValidationMessage(
                KEY_APPLICATION_NAME + " cannot contain whitespace", Severity.ERROR, KEY_APPLICATION_NAME));
        }
        
        if (ValidationUtils.isBlank(getBeansJarTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_BEANS_JAR + " cannot be blank", Severity.ERROR, KEY_BEANS_JAR));
        } else {
            // TODO: validate the beans jar somehow
        }
        
        if (ValidationUtils.isBlank(getLocalConfigDirTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_LOCAL_CONFIG_DIR + " cannot be blank", Severity.ERROR, KEY_LOCAL_CONFIG_DIR));
        } else {
            // TODO: validate the local configuration directory
        }
        
        
        if (ValidationUtils.isBlank(getRemoteConfigDirTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_REMOTE_CONFIG_DIR + " cannot be blank", Severity.ERROR, KEY_REMOTE_CONFIG_DIR));
        } else {
            // TODO: validate the remote configuration directory
        }
        
        if (getLocalApiRadioButton().isSelected()) {
            if (ValidationUtils.isBlank(getOrmJarTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_ORM_JAR + " cannot be blank", Severity.ERROR, KEY_ORM_JAR));
            } else {
                // TODO: validate the ORM jar
            }
        } else { // remote API
            if (ValidationUtils.isBlank(getHostNameTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_HOST_NAME + " cannot be blank", Severity.ERROR, KEY_HOST_NAME));
            } else {
                URL remoteUrl = null;
                try {
                    remoteUrl = new URL(getHostNameTextField().getText());
                } catch (Exception ex) {
                    result.add(new SimpleValidationMessage(
                        KEY_HOST_NAME + " does not parse as a URL", Severity.ERROR, KEY_HOST_NAME));
                }
                if (remoteUrl != null && ValidationUtils.isBlank(remoteUrl.getHost())) {
                    result.add(new SimpleValidationMessage(
                        KEY_HOST_NAME + " does not contain a host name", Severity.ERROR, KEY_HOST_NAME));
                }
            }
            
            if (ValidationUtils.isBlank(getPortTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_PORT_NUMBER + " cannot be blank", Severity.ERROR, KEY_PORT_NUMBER));
            } else {
                try {
                    Integer.parseInt(getPortTextField().getText());
                } catch (Exception ex) {
                    result.add(new SimpleValidationMessage(
                        KEY_PORT_NUMBER + " is not an integer", Severity.ERROR, KEY_PORT_NUMBER));
                }
            }
        }
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        // update next button enabled
        setNextEnabled(!validationModel.hasErrors());
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
    
    
    // ------------
    // helpers
    // ------------
    
    
    private void setLocalRemoteComponentsEnabled() {
        boolean local = getLocalApiRadioButton().isSelected();
        
        getOrmJarLabel().setEnabled(local);
        getOrmJarTextField().setEnabled(local);
        getOrmJarBrowseButton().setEnabled(local);
        getHostNameLabel().setEnabled(!local);
        getHostNameTextField().setEnabled(!local);
        getPortLabel().setEnabled(!local);
        getPortTextField().setEnabled(!local);
        
        configurationStep.setUseLocalApi(local);
    }
    
    
    private void setSimpleAdvancedComponentsEnabled() {
        boolean simple = getSimpleConfigRadioButton().isSelected();
        
        PortalUtils.setContainerEnabled(getBasicConfigPanel(), !simple);
        PortalUtils.setContainerEnabled(getRemoteApiPanel(), !simple);
        getOutputDirBrowseButton().setEnabled(simple);
    }
    
    
    private void selectSdkOutputDirectory() throws Exception {
        String[] infoMessage = {
            "Select the output directory generated",
            "by your caCORE SDK installation.",
            "The wizard will then attempt to locate",
            "the configuration files and libraries",
            "generated by the caCORE SDK and fill in",
            "these values for you."
        };
        OneTimeInfoDialogUtil.showInfoDialog(
            QueryProcessorConfigurationPanel.class, 
            KEY_OUTPUT_DIRECTORY, infoMessage);
        String selection = ResourceManager.promptDir(null);
        if (selection != null && selection.length() != 0) {
            // selection made
            File selectedDir = new File(selection);
            getOutputDirTextField().setText(selectedDir.getAbsolutePath());
            File[] outputContents = selectedDir.listFiles(new FileFilter() {
                public boolean accept(File path) {
                    return path.isDirectory() && !path.getName().startsWith(".");
                }
            });
            if (outputContents.length != 1) {
                StringBuffer err = new StringBuffer();
                err.append("Unable to locate application directory:\n");
                err.append("Expected to find one directory, but found ");
                err.append(outputContents.length);
                throw new Exception(err.toString());
            }
            File applicationOutDir = outputContents[0];
            throwExceptionIfNotDirectory(applicationOutDir);
            getApplicationNameTextField().setText(applicationOutDir.getName());
            configurationStep.setApplicationName(applicationOutDir.getName());
            File packageDir = new File(applicationOutDir, "package");
            throwExceptionIfNotDirectory(packageDir);
            // find remote and local client directories
            File localClientDir = new File(packageDir, "local-client");
            File remoteClientDir = new File(packageDir, "remote-client");
            throwExceptionIfNotDirectory(localClientDir);
            throwExceptionIfNotDirectory(remoteClientDir);
            File remoteClientLibDir = new File(remoteClientDir, "lib");
            File remoteClientConfDir = new File(remoteClientDir, "conf");
            File localClientLibDir = new File(localClientDir, "lib");
            File localClientConfDir = new File(localClientDir, "conf");
            throwExceptionIfNotDirectory(remoteClientLibDir);
            throwExceptionIfNotDirectory(remoteClientConfDir);
            throwExceptionIfNotDirectory(localClientLibDir);
            throwExceptionIfNotDirectory(localClientConfDir);
            // set the remote-client config
            getRemoteConfigDirTextField().setText(remoteClientConfDir.getAbsolutePath());
            configurationStep.setRemoteConfigDir(remoteClientConfDir.getAbsolutePath());
            // set the local-client config
            getLocalConfigDirTextField().setText(localClientConfDir.getAbsolutePath());
            configurationStep.setLocalConfigDir(localClientConfDir.getAbsolutePath());
            // find the beans jar
            File beansJar = new File(remoteClientLibDir, applicationOutDir.getName() + "-beans.jar");
            getBeansJarTextField().setText(beansJar.getAbsolutePath());
            configurationStep.setBeansJarLocation(beansJar.getAbsolutePath());
            // find the ORM jar
            File ormJar = new File(localClientLibDir, applicationOutDir.getName() + "-orm.jar");
            getOrmJarTextField().setText(ormJar.getAbsolutePath());
            configurationStep.setOrmJarLocation(ormJar.getAbsolutePath());
            // run validation
            validateInput();
        }
    }
    
    
    private void throwExceptionIfNotDirectory(File checkMe) throws FileNotFoundException {
        if (checkMe == null || !checkMe.exists() || !checkMe.isDirectory()) {
            throw new FileNotFoundException("Unable to locate directory " + 
                (checkMe == null ? "<null>" : checkMe.getAbsolutePath()));
        }
    }
}
