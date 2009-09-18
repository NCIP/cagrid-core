package gov.nih.nci.cagrid.introduce.portal.updater.steps;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jdom.Document;
import org.pietschy.wizard.PanelWizardStep;


public class SelectAndInstallStep extends PanelWizardStep {

	private JPanel descriptionPanel = null;

    private JPanel busyPanel = null;

	private JProgressBar busyProgressBar = null;

	private JButton startButton = null;
	
	private Document availablePackages;


	/**
	 * This method initializes
	 */
	public SelectAndInstallStep(Document availablePackages) {
		super();
		initialize();
		this.availablePackages = availablePackages;
	}


	protected void install() {

	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.weightx = 1.0D;
		gridBagConstraints2.weighty = 0.2D;
		gridBagConstraints2.gridx = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.weightx = 1.0D;
		gridBagConstraints1.weighty = 1.0D;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0.0D;
		gridBagConstraints.gridy = 1;
		this.setLayout(new GridBagLayout());
		this.setSize(new Dimension(263, 161));
		this.add(getDescriptionPanel(), gridBagConstraints1);

		this.add(getBusyPanel(), gridBagConstraints2);
	}


	/**
	 * This method initializes descriptionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDescriptionPanel() {
		if (descriptionPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 0;

			descriptionPanel = new JPanel();
			descriptionPanel.setLayout(new GridBagLayout());
			descriptionPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		}
		return descriptionPanel;
	}


	/**
	 * This method initializes busyPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getBusyPanel() {
		if (busyPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(0, 0, 0, 10);
			gridBagConstraints4.gridy = 0;
			busyPanel = new JPanel();
			busyPanel.setLayout(new GridBagLayout());
			busyPanel.add(getBusyProgressBar(), gridBagConstraints4);
			busyPanel.add(getStartButton(), gridBagConstraints5);
		}
		return busyPanel;
	}


	/**
	 * This method initializes busyProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getBusyProgressBar() {
		if (busyProgressBar == null) {
			busyProgressBar = new JProgressBar();
			busyProgressBar.setPreferredSize(new Dimension(148, 16));
		}
		return busyProgressBar;
	}


	/**
	 * This method initializes startButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStartButton() {
		if (startButton == null) {
			startButton = new JButton();
			startButton.setText("Start");
			startButton.setPreferredSize(new Dimension(57, 16));
			startButton.setFont(new Font("Dialog", Font.BOLD, 10));
			startButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getStartButton().setEnabled(false);
					Thread th = new Thread(new Runnable() {

						public void run() {
							getBusyProgressBar().setIndeterminate(true);
							install();
							getBusyProgressBar().setIndeterminate(false);
							setComplete(true);
						}

					});
					th.start();
				}
			});
		}
		return startButton;
	}
}
