package gov.nih.nci.cagrid.introduce.portal.modification.upgrade;

import gov.nih.nci.cagrid.introduce.upgrade.common.UpgradeStatus;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.cagrid.grape.GridApplication;


public class UpgradeStatusView extends JDialog {

    public static final int PROCEED = 1;
    public static final int ROLL_BACK = 2;
    public static final int CANCEL = 3;

    private UpgradeStatus status = null;
    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;
    private JPanel statusPanel = null;
    private JTextArea statusTextArea = null;
    private JButton proceedButton = null;
    private JButton rollBackButton = null;
    private JButton editButton = null;
    private JScrollPane statusScrollPane = null;

    private int result = -1;


    /**
     * This method initializes
     */
    public UpgradeStatusView(UpgradeStatus status) {
        super(GridApplication.getContext().getApplication());
        this.status = status;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(new Dimension(500, 600));
        this.setTitle("Upgrade Status Report");
        this.setContentPane(getMainPanel());
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        GridApplication.getContext().centerDialog(this);
    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 0.0D;
            gridBagConstraints.gridx = 0;
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            this.mainPanel.add(getButtonPanel(), gridBagConstraints);
            this.mainPanel.add(getStatusPanel(), gridBagConstraints1);
        }
        return this.mainPanel;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (this.buttonPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 2;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 0;
            this.buttonPanel = new JPanel();
            this.buttonPanel.setLayout(new GridBagLayout());
            this.buttonPanel.add(getProceedButton(), gridBagConstraints3);
            this.buttonPanel.add(getRollBackButton(), gridBagConstraints4);
            this.buttonPanel.add(getEditButton(), gridBagConstraints5);
        }
        return this.buttonPanel;
    }


    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (this.statusPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.weighty = 1.0;
            gridBagConstraints6.weightx = 1.0;
            this.statusPanel = new JPanel();
            this.statusPanel.setLayout(new GridBagLayout());
            this.statusPanel.add(getStatusScrollPane(), gridBagConstraints6);
        }
        return this.statusPanel;
    }


    /**
     * This method initializes statusTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getStatusTextArea() {
        if (this.statusTextArea == null) {
            this.statusTextArea = new JTextArea();
            this.statusTextArea.setEditable(false);
            this.statusTextArea.setText(this.status.toString());
        }
        return this.statusTextArea;
    }


    /**
     * This method initializes proceedButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getProceedButton() {
        if (this.proceedButton == null) {
            this.proceedButton = new JButton();
            this.proceedButton.setText("Proceed");
            this.proceedButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    UpgradeStatusView.this.result = UpgradeStatusView.PROCEED;
                    dispose();

                }

            });
        }
        return this.proceedButton;
    }


    /**
     * This method initializes rollBackButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRollBackButton() {
        if (this.rollBackButton == null) {
            this.rollBackButton = new JButton();
            this.rollBackButton.setText("Roll Back");
            this.rollBackButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    UpgradeStatusView.this.result = UpgradeStatusView.ROLL_BACK;
                    dispose();

                }

            });
        }
        return this.rollBackButton;
    }


    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (this.editButton == null) {
            this.editButton = new JButton();
            this.editButton.setText("Edit");
            this.editButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    UpgradeStatusView.this.result = UpgradeStatusView.CANCEL;
                    dispose();

                }

            });
        }
        return this.editButton;
    }


    /**
     * This method initializes statusScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getStatusScrollPane() {
        if (this.statusScrollPane == null) {
            this.statusScrollPane = new JScrollPane();
            this.statusScrollPane.setViewportView(getStatusTextArea());
        }
        return this.statusScrollPane;
    }


    public int getResult() {
        return this.result;
    }


    public static int showUpgradeStatusView(UpgradeStatus status) {
        UpgradeStatusView view = new UpgradeStatusView(status);
        view.setVisible(true);
        return view.getResult();
    }


    public static void main(String[] args) {
        int result = UpgradeStatusView.showUpgradeStatusView(new UpgradeStatus());
        System.out.println("result: " + result);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
