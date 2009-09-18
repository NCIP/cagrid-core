package gov.nih.nci.cagrid.introduce.portal.updater.steps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JRadioButton;

import org.pietschy.wizard.PanelWizardStep;

public class InstallOrUpdateChoiceStep extends PanelWizardStep {

	private JRadioButton checkForUpdatesButton = null;
	private JRadioButton findNewFeaturesButton = null;

	/**
	 * This method initializes 
	 * 
	 */
	public InstallOrUpdateChoiceStep() {
		super();
		initialize();
		this.setComplete(true);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(263, 161));
        this.add(getCheckForUpdatesButton(), gridBagConstraints1);
        this.add(getFindNewFeaturesButton(), gridBagConstraints2);
			
	}

	/**
	 * This method initializes checkForUpdatesButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getCheckForUpdatesButton() {
		if (checkForUpdatesButton == null) {
			checkForUpdatesButton = new JRadioButton();
			checkForUpdatesButton.setText("Check for upgrades to current extensions or load new extensions");
			checkForUpdatesButton.setSelected(true);
			checkForUpdatesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(checkForUpdatesButton.isSelected()){
						findNewFeaturesButton.setSelected(false);
					}
				}
			});
		}
		return checkForUpdatesButton;
	}
	
	public boolean isCheckForUpdateOption(){
		return this.getCheckForUpdatesButton().isSelected();
	}

	/**
	 * This method initializes findNewFeaturesButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getFindNewFeaturesButton() {
		if (findNewFeaturesButton == null) {
			findNewFeaturesButton = new JRadioButton();
			findNewFeaturesButton.setText("Upgrade Introduce");
			findNewFeaturesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(findNewFeaturesButton.isSelected()){
						checkForUpdatesButton.setSelected(false);
					}
				}
			});
		}
		return findNewFeaturesButton;
	}
	
	public boolean isFindNewFeaturesOption(){
		return getFindNewFeaturesButton().isSelected();
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
