package org.cagrid.csm.authorization.extension.gui;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificMethodInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.csm.authorization.extension.beans.CSMAuthorizationDescription;
import org.cagrid.csm.authorization.extension.beans.ProtectionMethod;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */

public class CSMAuthorizationPanel extends JPanel {

    public final static String CSM_CONFIGURATION_FILE = "csmConfiguration";

    private static final long serialVersionUID = 1L;
    private JLabel protectionMethodLabel = null;
    private JComboBox protectionType = null;
    private JLabel protectionElemLabel = null;
    private JTextField protectionElement = null;
    private JLabel privilegeLabel = null;
    private JComboBox privilege = null;
    private String serviceType;
    private String methodName;
    private CSMAuthorizationDescription authDesc;
    private SpecificServiceInformation info = null;

    private JLabel appContextLabel = null;

    private JTextField applicationContext = null;


    /**
     * This is the default constructor
     */
    public CSMAuthorizationPanel(SpecificServiceInformation info, CSMAuthorizationDescription authDesc) {
        this(info, null, authDesc);
    }


    public CSMAuthorizationPanel(SpecificServiceInformation info, String methodName,
        CSMAuthorizationDescription authDesc) {
        super();
        this.info = info;
        this.authDesc = authDesc;
        this.serviceType = info.getService().getName();
        this.methodName = methodName;
        initialize();
        if (authDesc != null)
            setAuthorization(this.authDesc);
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints21.gridy = 0;
        gridBagConstraints21.weightx = 1.0;
        gridBagConstraints21.anchor = GridBagConstraints.WEST;
        gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints21.gridx = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.anchor = GridBagConstraints.WEST;
        gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints11.gridy = 0;
        appContextLabel = new JLabel();
        appContextLabel.setText("CSM Application Context");
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridy = 3;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.anchor = GridBagConstraints.WEST;
        gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints5.gridx = 1;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.anchor = GridBagConstraints.WEST;
        gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints4.gridy = 3;
        privilegeLabel = new JLabel();
        privilegeLabel.setText("Privilege");
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 2;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.anchor = GridBagConstraints.WEST;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.gridy = 2;
        protectionElemLabel = new JLabel();
        protectionElemLabel.setText("Protection Element");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        protectionMethodLabel = new JLabel();
        protectionMethodLabel.setText("Protection Method");
        this.setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(null, "Common Security Module (CSM)",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
            new Color(62, 109, 181)));
        this.add(protectionMethodLabel, gridBagConstraints1);
        this.add(getProtectionType(), gridBagConstraints);
        this.add(protectionElemLabel, gridBagConstraints2);
        this.add(getProtectionElement(), gridBagConstraints3);
        this.add(privilegeLabel, gridBagConstraints4);
        this.add(getPrivilege(), gridBagConstraints5);
        this.add(appContextLabel, gridBagConstraints11);
        this.add(getApplicationContext(), gridBagConstraints21);
    }


    /**
     * This method initializes protectionType
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProtectionType() {
        if (protectionType == null) {
            protectionType = new JComboBox();
            protectionType.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    StringBuffer pe = new StringBuffer();
                    if (protectionType.getSelectedItem().equals(ProtectionMethod.ServiceType)) {
                        pe.append(serviceType);
                        if (methodName != null) {
                            pe.append(":" + "[METHOD_NAME]");
                        }
                        getProtectionElement().setEditable(false);
                        getProtectionElement().setText(pe.toString());
                    } else if (protectionType.getSelectedItem().equals(ProtectionMethod.ServiceURI)) {
                        pe.append("[DEPLOYED_SERVICE_URL]");
                        if (methodName != null) {
                            pe.append(":" + "[METHOD_NAME]");
                        }
                        getProtectionElement().setEditable(false);
                        getProtectionElement().setText(pe.toString());
                    } else {
                        getProtectionElement().setEditable(true);
                    }
                }
            });
            protectionType.addItem(ProtectionMethod.ServiceType);
            protectionType.addItem(ProtectionMethod.ServiceURI);
            protectionType.addItem(ProtectionMethod.Custom);
        }
        return protectionType;
    }


    /**
     * This method initializes protectionElement
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionElement() {
        if (protectionElement == null) {
            protectionElement = new JTextField();
            protectionElement.setEditable(false);
        }
        return protectionElement;
    }


    /**
     * This method initializes privilege
     * 
     * @return javax.swing.JTextField
     */
    private JComboBox getPrivilege() {
        if (privilege == null) {
            privilege = new JComboBox();
            privilege.setEditable(true);
            privilege.addItem("EXECUTE");
            privilege.addItem("READ");
            privilege.addItem("WRITE");
            privilege.addItem("CREATE");
            privilege.addItem("ACCESS");
            privilege.addItem("UPDATE");
            privilege.addItem("DELETE");
        }
        return privilege;
    }


    public void setAuthorization(CSMAuthorizationDescription csm) {
        this.getProtectionType().setSelectedItem(csm.getProtectionMethod());
        if (csm.getProtectionMethod().equals(ProtectionMethod.Custom) && csm.getCustomProtectionMethod() != null) {
            this.getProtectionElement().setText(csm.getCustomProtectionMethod());
        }
        boolean hasPrivilege = false;
        for (int i = 0; i < getPrivilege().getItemCount(); i++) {
            if (getPrivilege().getItemAt(i).equals(csm.getPrivilege())) {
                hasPrivilege = true;
            }
        }
        if (!hasPrivilege) {
            getPrivilege().addItem(csm.getPrivilege());
        }
        this.getPrivilege().setSelectedItem(csm.getPrivilege());
        this.getApplicationContext().setText(csm.getApplicationContext());
    }


    public CSMAuthorizationDescription getAuthorization() throws Exception {
        CSMAuthorizationDescription csm = new CSMAuthorizationDescription();
        csm.setProtectionMethod((ProtectionMethod) getProtectionType().getSelectedItem());

        if (csm.getProtectionMethod().equals(ProtectionMethod.Custom)) {
            String custom = Utils.clean(this.getProtectionElement().getText());
            if (custom == null) {
                StringBuffer sb = new StringBuffer();

                if (methodName != null) {
                    sb.append("You must specify a protection element to protect the method, " + methodName
                        + " with CSM!!!");
                } else {
                    sb.append("You must specify a protection element to protect the service, " + serviceType
                        + " with CSM!!!");
                }
                throw new Exception(sb.toString());
            } else {
                csm.setCustomProtectionMethod(custom);
            }

        }

        String priv = Utils.clean((String) getPrivilege().getSelectedItem());
        if (priv == null) {
            StringBuffer sb = new StringBuffer();

            if (methodName != null) {
                sb.append("You must specify a privilege to protect the method, " + methodName + " with CSM!!!");
            } else {
                sb.append("You must specify a privilege to protect the service, " + serviceType + " with CSM!!!");
            }
            throw new Exception(sb.toString());
        }

        csm.setPrivilege(priv);

        String application = Utils.clean(getApplicationContext().getText());
        if (application == null) {
            StringBuffer sb = new StringBuffer();
            if (methodName != null) {
                sb.append("You must specify a CSM application context to protect the method, " + methodName
                    + " with CSM!!!");
            } else {
                sb.append("You must specify a CSM application context to protect the service, " + serviceType
                    + " with CSM!!!");
            }
            throw new Exception(sb.toString());
        }
        csm.setApplicationContext(application);
        return csm;
    }


    /**
     * This method initializes applicationContext
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = new JTextField();
        }
        return applicationContext;
    }

}
