package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.utils.ErrorDialog;
import java.awt.Dimension;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TargetGridDisplayPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Grid grid;
	
	private JLabel jLabel = null;

	private JLabel logo = null;

	private JButton setGrid = null;

	private String GAARDS_CONFIGURATION_DIRECTORY = Utils.getCaGridUserHome()
			+ File.separator + "gaards";
	
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
			setGrid.setText("Make Default Grid");

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
			String targetGridDirectory = GAARDS_CONFIGURATION_DIRECTORY
					+ File.separator + grid.getSystemName();

			File gridSyncGTDCertsDir = null;
			FilenameFilter fileOnlyFilter = new FilenameFilter() {

				public boolean accept(File dir, String name) {
					File file = new File(dir + "/" + name);
					if (file.isFile() && !name.equals(".svn"))
						return true;
					else
						return false;
				}
			};
			File[] gridSyncGTSCertFiles = null;

			String gridSyncGTDCertsDirName = System.getProperty("user.home")
					+ File.separator + ".globus" + File.separator
					+ "certificates";
			gridSyncGTDCertsDir = new File(gridSyncGTDCertsDirName);
			if (gridSyncGTDCertsDir.exists()) {
				gridSyncGTSCertFiles = gridSyncGTDCertsDir
						.listFiles(fileOnlyFilter);

				for (int i = 0; i < gridSyncGTSCertFiles.length; i++) {
					gridSyncGTSCertFiles[i].delete();
				}
			} else {
				gridSyncGTDCertsDir.mkdirs();
			}

			File gridTargetCertsDir = new File(targetGridDirectory
					+ File.separator + "certificates");
			File[] gridCertFiles = gridTargetCertsDir.listFiles(fileOnlyFilter);
			for (int i = 0; i < gridCertFiles.length; i++) {
				Utils.copyFile(gridCertFiles[i], new File(gridSyncGTDCertsDir,
						gridCertFiles[i].getName()));
			}

			// sync with trust fabric
			SyncGTSDefault
					.setServiceSyncDescriptionLocation(targetGridDirectory
							+ File.separator + "sync-description.xml");

			SyncDescription description = SyncGTSDefault.getSyncDescription();

			SyncGTS sync = SyncGTS.getInstance();
			sync.syncOnce(description);

			// reload
			GAARDSApplication.getContext().getConfigurationManager()
					.setActiveConfiguration(grid.getSystemName());
			GAARDSApplication.getContext().getConfigurationManager().reload();
			ServicesManager.getInstance().syncWithUpdatedConfiguration();

			//saveTargetGridToPropertyFile();
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}

	}

	private void saveTargetGridToPropertyFile()
			throws Exception {
		
		
		
		String gaardsPropertyFileName = GAARDS_CONFIGURATION_DIRECTORY
				+ File.separator + "gaards.properties";
		Properties gaardsPropertyFile = null;
		gaardsPropertyFile = new Properties();
		gaardsPropertyFile.load(new FileInputStream(gaardsPropertyFileName));
		gaardsPropertyFile.setProperty("target.grid", grid.getSystemName());
		gaardsPropertyFile.store(new FileOutputStream(gaardsPropertyFileName),
				null);
	}

} 
