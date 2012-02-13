package gov.nih.nci.cagrid.introduce.portal.modification.services;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.Custom;
import gov.nih.nci.cagrid.introduce.beans.service.Identifiable;
import gov.nih.nci.cagrid.introduce.beans.service.Lifetime;
import gov.nih.nci.cagrid.introduce.beans.service.Main;
import gov.nih.nci.cagrid.introduce.beans.service.Notification;
import gov.nih.nci.cagrid.introduce.beans.service.Persistent;
import gov.nih.nci.cagrid.introduce.beans.service.ResourceFrameworkOptions;
import gov.nih.nci.cagrid.introduce.beans.service.ResourcePropertyManagement;
import gov.nih.nci.cagrid.introduce.beans.service.Secure;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.Singleton;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.ModificationViewer;
import gov.nih.nci.cagrid.introduce.portal.modification.security.ServiceSecurityPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties.ResourceFrameworkOptionsManager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;
import javax.swing.JTabbedPane;


public class ModifyService extends JDialog {
    
    private static final Logger logger = Logger.getLogger(ModifyService.class);

    private SpecificServiceInformation service;

    private JPanel mainPanel = null;

    private JPanel buttonPanel = null;

    private JButton doneButton = null;

    private JPanel contentPanel = null;

    private JLabel serviceNameLabel = null;

    private JTextField serviceNameTextField = null;

    private JLabel serviceNamespaceLabel = null;

    private JTextField namespaceTextField = null;

    private JLabel servicePackageNameLabel = null;

    private JTextField servicePackageNameTextField = null;

    private ServiceSecurityPanel securityPanel = null;

    private boolean isNew;

    private ValidationResultModel validationModel = new DefaultValidationResultModel();

    private static final String SERVICE_NAME = "Service name";

    private static final String SERVICE_NAMESPACE = "Service namespace"; // @jve:decl-index=0:

    private static final String SERVICE_PACKAGE = "Service package name";

    private boolean wasClosed = false;

    private JLabel descriptionLabel = null;

    private JScrollPane textBoxPane = null;

    private JTextPane jTextPane = null;

    private JPanel infoPanel = null;

    private ResourceFrameworkOptionsManager resourceOptionsPanel = null;

    private JTabbedPane mainTabbedPane = null;

    private JPanel emptyPanel = null;


    /**
     * This method initializes
     */
    public ModifyService(SpecificServiceInformation service, boolean isNew) {
        super(GridApplication.getContext().getApplication());
        this.setModal(true);
        this.isNew = isNew;
        this.service = service;

        initialize();
        if (service.getService().getName() != null && service.getService().getName().length() > 0) {
            getServiceNameTextField().setText(service.getService().getName());
        } else {
            getServiceNameTextField().setText(
                service.getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME)
                    + "Context");
        }
        if (service.getService().getNamespace() != null && service.getService().getNamespace().length() > 0) {
            getNamespaceTextField().setText(service.getService().getNamespace());
        } else {
            getNamespaceTextField().setText(
                service.getIntroduceServiceProperties().getProperty(
                    IntroduceConstants.INTRODUCE_SKELETON_NAMESPACE_DOMAIN)
                    + "/Context");
        }
        if (service.getService().getPackageName() != null && service.getService().getPackageName().length() > 0) {
            getServicePackageNameTextField().setText(service.getService().getPackageName());
        } else {
            getServicePackageNameTextField().setText(service.getServices().getService(0).getPackageName() + ".context");
        }

    }


    public boolean wasClosed() {
        return this.wasClosed;
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                super.windowClosing(e);
                wasClosed = true;
                setVisible(false);
                dispose();
            }

        });

        this.setContentPane(getMainPanel());
        this.setTitle("Modify Service Context");

        this.setSize(new Dimension(315, 275));

        initValidation();

        this.pack();
        GridApplication.getContext().centerDialog(this);

    }


    private void initValidation() {
        ValidationComponentUtils.setMessageKey(getServiceNameTextField(), SERVICE_NAME);
        ValidationComponentUtils.setMessageKey(getNamespaceTextField(), SERVICE_NAMESPACE);
        ValidationComponentUtils.setMessageKey(getServicePackageNameTextField(), SERVICE_PACKAGE);

        validateInput();
        updateComponentTreeSeverity();
    }


    private void validateInput() {

        ValidationResult result = new ValidationResult();

        if (ValidationUtils.isBlank(this.getServiceNameTextField().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_NAME + " must not be blank.", Severity.ERROR, SERVICE_NAME));
        } else if (!CommonTools.isValidServiceName(serviceNameTextField.getText())) {
            result.add(new SimpleValidationMessage(SERVICE_NAME
                + " is not valid, Service name must be a java compatible class name ("
                + CommonTools.ALLOWED_JAVA_CLASS_REGEX + ") and must not contain any java reserved words.",
                Severity.ERROR, SERVICE_NAME));
            return;
        }

        if (ValidationUtils.isBlank(this.getNamespaceTextField().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_NAMESPACE + " must not be blank.", Severity.ERROR,
                SERVICE_NAMESPACE));
        } else {
            // make sure it is a valid namespace
            try {
                URI namespaceURI = new URI(this.getNamespaceTextField().getText());
            } catch (Exception e) {
                result.add(new SimpleValidationMessage("Invalid namespace format.", Severity.ERROR, SERVICE_NAMESPACE));
            }
            // make sure there are no collision problems with namespaces
            // or packages.....
            for (int i = 0; i < service.getNamespaces().getNamespace().length; i++) {
                NamespaceType nsType = service.getNamespaces().getNamespace(i);
                if (nsType.getNamespace().equals(namespaceTextField.getText())
                    && !nsType.getPackageName().equals(servicePackageNameTextField.getText())) {
                    result.add(new SimpleValidationMessage(
                        "Namespace Collision, Service Namespace is already being used "
                            + "and Package Name does not match : " + servicePackageNameTextField.getText() + " != "
                            + nsType.getPackageName(), Severity.ERROR, SERVICE_NAMESPACE));
                    break;
                }
            }
        }

        if (ValidationUtils.isBlank(this.getServicePackageNameTextField().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_PACKAGE + " must not be blank.", Severity.ERROR,
                SERVICE_PACKAGE));
        } else if (!CommonTools.isValidPackageName(this.getServicePackageNameTextField().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_PACKAGE
                + " is not in valid java package format or may contain java reserved words.", Severity.ERROR,
                SERVICE_PACKAGE));
        }

        for (int i = 0; i < service.getServiceDescriptor().getServices().getService().length; i++) {
            ServiceType testService = service.getServiceDescriptor().getServices().getService(i);
            if (!testService.equals(service.getService())) {
                if (namespaceTextField.getText().equals(testService.getNamespace())) {
                    result.add(new SimpleValidationMessage(
                        "Service Namespace is not valid, Service namespace must be unique for this service context.",
                        Severity.ERROR, SERVICE_NAMESPACE));
                }
                if (servicePackageNameTextField.getText().equals(testService.getPackageName())) {
                    result
                        .add(new SimpleValidationMessage(
                            "Service Package Name is not valid, Service Package Name must be unique for this service context.",
                            Severity.ERROR, SERVICE_PACKAGE));
                }
                if (serviceNameTextField.getText().equals(testService.getName())) {
                    result.add(new SimpleValidationMessage(
                        "Service Name is not valid, Service Name must be unique for this service context.",
                        Severity.ERROR, SERVICE_NAME));
                }
            }
        }

        this.validationModel.setResult(result);
        updateComponentTreeSeverity();
        updateDoneButton();
    }


    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, this.validationModel.getResult());
    }


    private void updateDoneButton() {
        if (validationModel.hasErrors()) {
            getDoneButton().setEnabled(false);
        } else {
            getDoneButton().setEnabled(true);
        }
    }


    public final class TextBoxListener implements DocumentListener {

        public void changedUpdate(DocumentEvent e) {
            validateInput();
        }


        public void insertUpdate(DocumentEvent e) {
            validateInput();
        }


        public void removeUpdate(DocumentEvent e) {
            validateInput();
        }

    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = GridBagConstraints.BOTH;
            gridBagConstraints15.weighty = 1.0;
            gridBagConstraints15.weightx = 1.0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 2;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getButtonPanel(), gridBagConstraints);
            mainPanel.add(getMainTabbedPane(), gridBagConstraints15);
        }
        return mainPanel;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.gridx = 0;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.add(getDoneButton(), gridBagConstraints1);
        }
        return buttonPanel;
    }


    /**
     * This method initializes doneButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDoneButton() {
        if (doneButton == null) {
            doneButton = new JButton();
            doneButton.setText("Done");
            doneButton.setIcon(IntroduceLookAndFeel.getDoneIcon());
            doneButton.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);

                    service.getService().setName(serviceNameTextField.getText());
                    service.getService().setNamespace(namespaceTextField.getText());
                    service.getService().setPackageName(servicePackageNameTextField.getText());

                    // remember if service was a main service
                    ResourceFrameworkOptions newOptions = new ResourceFrameworkOptions();
                    if (service.getService().getResourceFrameworkOptions().getMain() != null) {
                        newOptions.setMain(new Main());
                    }

                    service.getService().setResourceFrameworkOptions(newOptions);

                    if (getResourceOptionsPanel().getCustomResource().isSelected()) {
                        service.getService().getResourceFrameworkOptions().setCustom(new Custom());
                    } else {
                        service.getService().getResourceFrameworkOptions().setIdentifiable(new Identifiable());
                        if (getResourceOptionsPanel().getSingletonResource().isSelected()) {
                            service.getService().getResourceFrameworkOptions().setSingleton(new Singleton());
                        }
                        if (getResourceOptionsPanel().getLifetimeResource().isSelected()) {
                            service.getService().getResourceFrameworkOptions().setLifetime(new Lifetime());
                        }
                        if (getResourceOptionsPanel().getPersistantResource().isSelected()) {
                            service.getService().getResourceFrameworkOptions().setPersistent(new Persistent());
                        }
                        if (getResourceOptionsPanel().getNotificationResource().isSelected()) {
                            service.getService().getResourceFrameworkOptions().setNotification(new Notification());
                        }
                        if (getResourceOptionsPanel().getSecureResource().isSelected()) {
                            service.getService().getResourceFrameworkOptions().setSecure(new Secure());
                        }
                        if (getResourceOptionsPanel().getResourceProperty().isSelected()) {
                            service.getService().getResourceFrameworkOptions().setResourcePropertyManagement(
                                new ResourcePropertyManagement());
                        }
                    }

                    service.getService().setDescription(getJTextPane().getText());
                    try {
                        service.getService().setServiceSecurity(getSecurityPanel().getServiceSecurity(true));
                    } catch (Exception e1) {
                        logger.error(e1.getMessage(),e1);
                        JOptionPane.showMessageDialog(ModifyService.this, e1.getMessage());
                        return;
                    }

                    dispose();
                }
            });
        }
        return doneButton;
    }


    /**
     * This method initializes contentPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPanel() {
        if (contentPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.weighty = 1.0D;
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 3;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.weightx = 0.0D;
            gridBagConstraints5.weighty = 0.0D;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weighty = 0.0D;
            gridBagConstraints21.gridy = 0;
            descriptionLabel = new JLabel();
            descriptionLabel.setText("Description");
            servicePackageNameLabel = new JLabel();
            servicePackageNameLabel.setText("Package Name");
            serviceNamespaceLabel = new JLabel();
            serviceNamespaceLabel.setText("Namespace");
            serviceNameLabel = new JLabel();
            serviceNameLabel.setText("Service Name");
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.add(getInfoPanel(), gridBagConstraints21);
            contentPanel.add(getResourceOptionsPanel(), gridBagConstraints5);
            contentPanel.add(getEmptyPanel(), gridBagConstraints6);
        }
        return contentPanel;
    }


    /**
     * This method initializes serviceNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getServiceNameTextField() {
        if (serviceNameTextField == null) {
            serviceNameTextField = new JTextField();
            if (!isNew) {
                serviceNameTextField.setEditable(false);
                serviceNameTextField.setEnabled(false);
            }
            serviceNameTextField.getDocument().addDocumentListener(new TextBoxListener());

        }
        return serviceNameTextField;
    }


    /**
     * This method initializes namespaceTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNamespaceTextField() {
        if (namespaceTextField == null) {
            namespaceTextField = new JTextField();
            if (!isNew) {
                namespaceTextField.setEditable(false);
                namespaceTextField.setEnabled(false);
            }
            namespaceTextField.getDocument().addDocumentListener(new TextBoxListener());
        }
        return namespaceTextField;
    }


    /**
     * This method initializes servicePackageNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getServicePackageNameTextField() {
        if (servicePackageNameTextField == null) {
            servicePackageNameTextField = new JTextField();
            if (!isNew) {
                servicePackageNameTextField.setEditable(false);
                servicePackageNameTextField.setEnabled(false);
            }
            servicePackageNameTextField.getDocument().addDocumentListener(new TextBoxListener());
        }
        return servicePackageNameTextField;
    }


    /**
     * This method initializes securityPanel
     * 
     * @return javax.swing.JPanel
     */
    private ServiceSecurityPanel getSecurityPanel() {
        if (securityPanel == null) {
            securityPanel = new ServiceSecurityPanel(service, service.getService());
        }
        return securityPanel;
    }


    /**
     * This method initializes textBoxPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getTextBoxPane() {
        if (textBoxPane == null) {
            textBoxPane = new JScrollPane();
            textBoxPane.setPreferredSize(new Dimension(200, 200));
            textBoxPane.setViewportView(getJTextPane());
        }
        return textBoxPane;
    }


    /**
     * This method initializes jTextPane
     * 
     * @return javax.swing.JTextPane
     */
    private JTextPane getJTextPane() {
        if (jTextPane == null) {
            jTextPane = new JTextPane();
            if (service.getService().getDescription() != null) {
                jTextPane.setText(service.getService().getDescription());
            }
        }
        return jTextPane;
    }


    /**
     * This method initializes infoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.BOTH;
            gridBagConstraints14.gridheight = 3;
            gridBagConstraints14.gridx = 1;
            gridBagConstraints14.gridy = 6;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.weighty = 1.0;
            gridBagConstraints14.insets = new Insets(0, 2, 0, 2);
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridwidth = 1;
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.gridy = 6;
            gridBagConstraints13.insets = new Insets(0, 2, 0, 2);
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.gridy = 4;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.weighty = 1.0D;
            gridBagConstraints8.insets = new Insets(0, 2, 0, 2);
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 4;
            gridBagConstraints7.insets = new Insets(0, 2, 0, 2);
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridx = 1;
            gridBagConstraints10.gridy = 2;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.weighty = 1.0D;
            gridBagConstraints10.insets = new Insets(0, 2, 0, 2);
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 2;
            gridBagConstraints9.insets = new Insets(0, 2, 0, 2);
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.insets = new Insets(0, 2, 0, 2);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.insets = new Insets(0, 2, 0, 2);
            infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createTitledBorder(null, "Service Context Information",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, IntroduceLookAndFeel
                    .getPanelLabelColor()));
            infoPanel.add(serviceNameLabel, gridBagConstraints3);
            infoPanel.add(getServiceNameTextField(), gridBagConstraints4);
            infoPanel.add(serviceNamespaceLabel, gridBagConstraints9);
            infoPanel.add(getNamespaceTextField(), gridBagConstraints10);
            infoPanel.add(servicePackageNameLabel, gridBagConstraints7);
            infoPanel.add(getServicePackageNameTextField(), gridBagConstraints8);
            infoPanel.add(descriptionLabel, gridBagConstraints13);
            infoPanel.add(getTextBoxPane(), gridBagConstraints14);
        }
        return infoPanel;
    }


    /**
     * This method initializes resourceOptionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private ResourceFrameworkOptionsManager getResourceOptionsPanel() {
        if (resourceOptionsPanel == null) {
            resourceOptionsPanel = new ResourceFrameworkOptionsManager(service.getService(),
                (ServiceInformation) service, isNew);
        }
        return resourceOptionsPanel;
    }


    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
            mainTabbedPane.addTab("Information", IntroduceLookAndFeel.getInformIcon(), new IconFeedbackPanel(
                this.validationModel, getContentPanel()), null);
            mainTabbedPane.addTab("Security", IntroduceLookAndFeel.getKeyIcon(), getSecurityPanel(), null);
        }
        return mainTabbedPane;
    }


    /**
     * This method initializes emptyPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEmptyPanel() {
        if (emptyPanel == null) {
            emptyPanel = new JPanel();
            emptyPanel.setLayout(new GridBagLayout());
        }
        return emptyPanel;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
