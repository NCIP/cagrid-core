package org.cagrid.gaards.ui.dorian.idp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.cagrid.gaards.dorian.idp.IdentityProviderAuditRecord;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.LookAndFeel;


public class IdentityProviderRecordWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel contentPanel = null;

    private JLabel jLabel = null;

    private JTextField targetId = null;

    private JLabel jLabel1 = null;

    private JTextField reportingParty = null;

    private JLabel jLabel2 = null;

    private JTextField eventType = null;

    private JLabel jLabel3 = null;

    private JTextField occurredAt = null;

    private JPanel messagePanel = null;

    private JTextArea message = null;

    private JScrollPane jScrollPane = null;

    private JPanel buttonPanel = null;

    private JButton close = null;

    private JPanel titlePanel = null;

    private IdentityProviderAuditRecord record;


    /**
     * This is the default constructor
     */
    public IdentityProviderRecordWindow(IdentityProviderAuditRecord r) {
        super();
        this.record = r;
        initialize();
        getTargetId().setText(r.getTargetId());
        getReportingParty().setText(r.getReportingPartyId());
        getEventType().setText(r.getAuditType().getValue());
        getOccurredAt().setText(r.getOccurredAt().getTime().toString());
        getMessage().setText(r.getAuditMessage());
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(500, 300);
        this.setContentPane(getJContentPane());
        this.setTitle("Federation Audit Record");
        this.setFrameIcon(DorianLookAndFeel.getAuditingIcon());
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getMainPanel(), BorderLayout.CENTER);
        }
        return jContentPane;
    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.anchor = GridBagConstraints.NORTH;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.weightx = 1.0D;
            gridBagConstraints12.gridy = 0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 3;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.weighty = 1.0D;
            gridBagConstraints21.fill = GridBagConstraints.BOTH;
            gridBagConstraints21.gridy = 2;
            jLabel3 = new JLabel();
            jLabel3.setText("Occurred At");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 1;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getContentPanel(), gridBagConstraints);
            mainPanel.add(getMessagePanel(), gridBagConstraints21);
            mainPanel.add(getButtonPanel(), gridBagConstraints11);
            mainPanel.add(getTitlePanel(), gridBagConstraints12);
        }
        return mainPanel;
    }


    /**
     * This method initializes contentPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPanel() {
        if (contentPanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 3;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.anchor = GridBagConstraints.WEST;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 3;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 2;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Event Type");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 0;
            jLabel1 = new JLabel();
            jLabel1.setText("Reporting Party");
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            jLabel = new JLabel();
            jLabel.setText("Target");
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.add(jLabel, gridBagConstraints1);
            contentPanel.add(getTargetId(), gridBagConstraints2);
            contentPanel.add(jLabel1, gridBagConstraints3);
            contentPanel.add(getReportingParty(), gridBagConstraints4);
            contentPanel.add(jLabel2, gridBagConstraints5);
            contentPanel.add(getEventType(), gridBagConstraints6);
            contentPanel.add(jLabel3, gridBagConstraints7);
            contentPanel.add(getOccurredAt(), gridBagConstraints8);
        }
        return contentPanel;
    }


    /**
     * This method initializes targetId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTargetId() {
        if (targetId == null) {
            targetId = new JTextField();
            targetId.setEnabled(true);
            targetId.setEditable(false);
        }
        return targetId;
    }


    /**
     * This method initializes reportingParty
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getReportingParty() {
        if (reportingParty == null) {
            reportingParty = new JTextField();
            reportingParty.setEditable(false);
        }
        return reportingParty;
    }


    /**
     * This method initializes eventType
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEventType() {
        if (eventType == null) {
            eventType = new JTextField();
            eventType.setEditable(false);
        }
        return eventType;
    }


    /**
     * This method initializes occurredAt
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getOccurredAt() {
        if (occurredAt == null) {
            occurredAt = new JTextField();
            occurredAt.setEditable(false);
        }
        return occurredAt;
    }


    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.BOTH;
            gridBagConstraints10.weighty = 1.0;
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.gridy = 0;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.weightx = 1.0;
            messagePanel = new JPanel();
            messagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Audit Message",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            messagePanel.setLayout(new GridBagLayout());
            messagePanel.add(getJScrollPane(), gridBagConstraints10);
        }
        return messagePanel;
    }


    /**
     * This method initializes message
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getMessage() {
        if (message == null) {
            message = new JTextArea();
            message.setEditable(false);
            message.setWrapStyleWord(true);
            message.setLineWrap(true);
        }
        return message;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getMessage());
        }
        return jScrollPane;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getClose(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes close
     * 
     * @return javax.swing.JButton
     */
    private JButton getClose() {
        if (close == null) {
            close = new JButton();
            close.setText("Close");
            close.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return close;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Audit Record", "Audit Record for " + this.record.getTargetId());
        }
        return titlePanel;
    }

}
