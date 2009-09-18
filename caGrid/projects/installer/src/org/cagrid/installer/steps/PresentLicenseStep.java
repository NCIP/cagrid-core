/**
 * 
 */
package org.cagrid.installer.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.model.CaGridInstallerModel;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.WizardModel;

/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class PresentLicenseStep extends PanelWizardStep {

	private static final Log logger = LogFactory
			.getLog(PresentLicenseStep.class);

	private CaGridInstallerModel model;

	/**
	 * 
	 */
	public PresentLicenseStep() {

	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PresentLicenseStep(String name, String summary) {
		super(name, summary);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public PresentLicenseStep(String name, String summary, Icon icon) {
		super(name, summary, icon);
	}

	public void init(WizardModel m) {
		this.model = (CaGridInstallerModel) m;

		setLayout(new BorderLayout());
		setSize(new Dimension(475, 161));

		JPanel licensePanel = new JPanel();
		licensePanel.setBackground(Color.WHITE);
		JTextPane textPane = new JTextPane();
		licensePanel.add(textPane);
		JScrollPane scrollPane = new JScrollPane(licensePanel);
		scrollPane.setPreferredSize(new Dimension(475, 150));
		add(scrollPane, BorderLayout.CENTER);

		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/cagrid_license.txt")));
			String line = null;
			while ((line = r.readLine()) != null) {
				sb.append(line).append("\n");
			}
			textPane.setText(sb.toString());
			textPane.setFont(textPane.getFont().deriveFont((float)10));
		} catch (Exception ex) {
			String msg = "Error loading license: " + ex.getMessage();
			logger.error(msg, ex);
			JOptionPane.showMessageDialog(null, msg, this.model
					.getMessage("error"), JOptionPane.ERROR_MESSAGE);
		}

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridBagLayout());
		add(controlPanel, BorderLayout.SOUTH);
		
		JLabel label = new JLabel(this.model.getMessage("accept.license"));
		controlPanel.add(label);
		

		JCheckBox checkBox = new JCheckBox();
		checkBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				PresentLicenseStep.this
						.setComplete(evt.getStateChange() == ItemEvent.SELECTED);
			}
		});
		controlPanel.add(checkBox);
	}

}
