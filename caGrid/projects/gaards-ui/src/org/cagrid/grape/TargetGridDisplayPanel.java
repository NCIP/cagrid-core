package org.cagrid.grape;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.utils.ErrorDialog;

public class TargetGridDisplayPanel extends JPanel {
	private static Log log = LogFactory.getLog(TargetGridDisplayPanel.class);
	
	private static final long serialVersionUID = 1L;

	private Grid grid;
	
	private JLabel jLabel = null;

	private JLabel logo = null;

	private JButton setGrid = null;
	
	private ConfigurationWindow window;

	/**
	 * This is the default constructor
	 */
	public TargetGridDisplayPanel(ConfigurationWindow window, Grid grid) {
		super();
		this.grid = grid;
		this.window = window;
		initialize();

	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.gridy = 2;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.gridy = 0;
		logo = new JLabel(LookAndFeel.getApplicationLogo());
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.anchor = GridBagConstraints.CENTER;
		gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints1.gridy = 1;
		jLabel = new JLabel();
		jLabel.setText(grid.getDisplayName());
		jLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		this.setLayout(new GridBagLayout());
		this.setSize(new Dimension(332, 235));
		this.add(jLabel, gridBagConstraints1);
		this.add(logo, gridBagConstraints);
		this.add(getSetGrid(), gridBagConstraints11);
	}

	/**
	 * This method initializes setGrid
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSetGrid() {
		if (setGrid == null) {
			setGrid = new JButton();
			setGrid.setText("Set Target Grid");

			setGrid.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setDefaultGrid();
					window.dispose();
				}
			});

		}
		return setGrid;
	}

	private void setDefaultGrid() {
		try {
			File targetGridDirectory = new File(GAARDSApplication.getGAARDSConfigurationDirectory(), grid.getSystemName());

			File gridSyncGTDCertsDir = Utils.getTrustedCerificatesDirectory();			

			File gridTargetCertsDir = new File(targetGridDirectory, "certificates");
			Utils.copyDirectory(gridTargetCertsDir, gridSyncGTDCertsDir);


			// sync with trust fabric
			SyncGTSDefault
					.setServiceSyncDescriptionLocation(targetGridDirectory.getAbsolutePath()
							+ File.separator + "sync-description.xml");

			SyncDescription description = SyncGTSDefault.getSyncDescription();

			SyncGTS sync = SyncGTS.getInstance();
			sync.syncOnce(description);

			// reload
			GAARDSApplication.getContext().getConfigurationManager()
					.setActiveConfiguration(grid.getSystemName());
			GAARDSApplication.getContext().getConfigurationManager().reload();
			ServicesManager.getInstance().syncWithUpdatedConfiguration();

			GAARDSApplication.setTargetGrid(grid.getSystemName());
		} catch (Exception e) {
			ErrorDialog.showError(e);
			FaultUtil.logFault(log, e);
		}
	}

} 
