package org.cagrid.grape.utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import org.cagrid.grape.GridApplication;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;


public class BusyDialog extends JDialog {

    private JPanel mainPanel = null;
    private JProgressBar progress = null;
    private JLabel infoLabel = null;


    /**
     * This method initializes
     */
    public BusyDialog(JFrame owner, String title) {
        super(owner, title, true);
        initialize();
    }


    /**
     * This method initializes
     */
    public BusyDialog(JFrame owner, String title, JProgressBar progressBar) {
        super(owner, title, true);
        this.progress = progressBar;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getMainPanel());
        this.setSize(new java.awt.Dimension(400, 100));
        GridApplication.getContext().centerDialog(this);
    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 1;
            infoLabel = new JLabel();
            infoLabel.setText(" ");
            infoLabel.setFont(new Font("Lucida Grande", Font.ITALIC, 12));
            infoLabel.setHorizontalAlignment(SwingConstants.TRAILING);
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.weighty = 0.0D;
            gridBagConstraints4.weightx = 1.0D;
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getProgress(), gridBagConstraints4);
            mainPanel.add(infoLabel, gridBagConstraints);
        }
        return this.mainPanel;
    }


    /**
     * This method initializes progress
     * 
     * @return javax.swing.JProgressBar
     */
    public JProgressBar getProgress() {
        if (this.progress == null) {
            this.progress = new JProgressBar();
            this.progress.setStringPainted(true);
            this.progress.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            this.progress.setForeground(new java.awt.Color(153, 153, 255));
            this.progress.setString("");
        }
        return this.progress;
    }
    
    
    public void setProgressText(String progressText){
        //this.getProgress().setString(progressText);
        this.infoLabel.setText(progressText + "\t");
    }


    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(1600, 800));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        BusyDialog d = new BusyDialog(frame, "test");

        d.setVisible(true);
        Thread.sleep(5000);
        d.getProgress().setString("foo");

    }
}
