package gov.nih.nci.cagrid.introduce.portal.updater.steps;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JTextPane;

import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;

public class FinishedStep extends PanelWizardStep {

	private JTextPane IntroTextPane = null;

	/**
	 * This method initializes
	 * 
	 */
	public FinishedStep() {
		super();
		initialize();
		this.setComplete(true);
	}

	public String getName() {
		return "Downloads Complete";
	}

	public String getSummary() {
		return "Introduce Downloads Complete";
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		this.setLayout(new GridBagLayout());
		this.setSize(new Dimension(263, 161));
		this.add(getIntroTextPane(), gridBagConstraints);

	}

	/**
	 * This method initializes IntroTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getIntroTextPane() {
		if (IntroTextPane == null) {
			IntroTextPane = new JTextPane();
			IntroTextPane.setFont(new Font("Sanserif", Font.PLAIN, 10));
			IntroTextPane
					.setText("Introduce needs to restart to complete the installation.  Click Finish to proceed.");
		}
		return IntroTextPane;
	}


	public void applyState() throws InvalidStateException {
		super.applyState();
		System.exit(0);
	}
	
	

} // @jve:decl-index=0:visual-constraint="10,10"
