package org.cagrid.gaards.ui.dorian.federation;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.ui.common.GAARDSLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.LookAndFeel;

public class SuccessfulLoginWindow extends ApplicationComponent {
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel buttonPanel = null;

	private JButton close = null;

	private JPanel titlePanel = null;

	private JLabel icon = null;

	private JLabel jLabel = null;
	private String gridIdentity;

	private JPanel textPanel = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel2 = null;

	private JLabel jLabel3 = null;

	/**
	 * This is the default constructor
	 */
	public SuccessfulLoginWindow(String gridIdentity) {
		super();
		this.gridIdentity = gridIdentity;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setFrameIcon(DorianLookAndFeel.getCertificateIcon());
		this.setTitle("Login Successful");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.ipadx = 120;
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.weightx = 1.0D;
			gridBagConstraints15.gridy = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.ipadx = 222;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.weightx = 10.0D;
			gridBagConstraints8.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getTitlePanel(), gridBagConstraints8);
			jContentPane.add(getButtonPanel(), gridBagConstraints15);
			jContentPane.add(getTextPanel(), gridBagConstraints);
		}
		return jContentPane;
	}

	

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getClose(), null);
		}
		return buttonPanel;
	}

	


	private JButton getClose() {
		if (close == null) {
			close = new JButton();
			close.setText("Close");
			getRootPane().setDefaultButton(close);
			// close.setIcon(LookAndFeel.getCloseIcon());
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
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.anchor = GridBagConstraints.WEST;
			gridBagConstraints19.gridx = 1;
			gridBagConstraints19.gridy = 0;
			gridBagConstraints19.weightx = 1.0D;
			gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints18.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Login Successful");
			jLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
			icon = new JLabel(LookAndFeel.getLogoNoText22x22());
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.add(icon, gridBagConstraints18);
			titlePanel.add(jLabel, gridBagConstraints19);
		}
		return titlePanel;
	}

	/**
	 * This method initializes textPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTextPanel() {
		if (textPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.anchor = GridBagConstraints.CENTER;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints3.gridy = 2;
			jLabel3 = new JLabel();
			jLabel3.setText(this.gridIdentity);
			jLabel3.setForeground(GAARDSLookAndFeel.getPanelLabelColor());
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.CENTER;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.CENTER;
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			gridBagConstraints1.weightx = 0.0D;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints1.gridy = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Your Grid Identity is:");
			jLabel1 = new JLabel();
			jLabel1.setText("Congratulations you have successfully logged in.");
			textPanel = new JPanel();
			textPanel.setLayout(new GridBagLayout());
			textPanel.add(jLabel1, gridBagConstraints1);
			textPanel.add(jLabel2, gridBagConstraints2);
			textPanel.add(jLabel3, gridBagConstraints3);
		}
		return textPanel;
	}

}
