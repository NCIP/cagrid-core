package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class ReloadConfigWindow extends ApplicationComponent {
	
	private String GAARDS_CONFIGURATION_DIRECTORY = Utils.getCaGridUserHome() + File.separator + "gaards";

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JPanel mainPanel = null;
	private JPanel servicePanel = null;
	private JPanel buttonPanel = null;
	private JLabel gridComboLabel = null;
	private JComboBox gridCombo = null;
	private JButton setTargetGridButton = null;
	private JButton close = null;


	/**
	 * This is the default constructor
	 */
	public ReloadConfigWindow() {
		super();
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setFrameIcon(DorianLookAndFeel.getCertificateIcon());
		this.setTitle("Select Target Grid");
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
			jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
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
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getServicePanel(), gridBagConstraints);
			mainPanel.add(getButtonPanel(), gridBagConstraints3);
		}
		return mainPanel;
	}


	/**
	 * This method initializes idpPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getServicePanel() {
		if (servicePanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 0;
			gridComboLabel = new JLabel();
			gridComboLabel.setText("Target Grid:");
			servicePanel = new JPanel();
			servicePanel.setLayout(new GridBagLayout());
			servicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Target Grid",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
			servicePanel.add(gridComboLabel, gridBagConstraints5);
			servicePanel.add(getGridCombo(), gridBagConstraints6);
		}
		return servicePanel;
	}


	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getSetTargetButton(), null);
			buttonPanel.add(getClose(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes ifs
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getGridCombo() {
		if (gridCombo == null) {
			gridCombo = new JComboBox();
			
			File gridTargets = new File(GAARDS_CONFIGURATION_DIRECTORY);
			FilenameFilter filter = new FilenameFilter() {
				
				public boolean accept(File dir, String name) {
					File file = new File(dir + "/" + name);
					if (file.isDirectory() && !name.equals(".svn"))
						return true;
					else
						return false;
				}
			};
			String[] gridTargetDirs = gridTargets.list(filter);
			for (int i = 0; i < gridTargetDirs.length; i++) {
				gridCombo.addItem(gridTargetDirs[i]);
			}
		}
		return gridCombo;
	}


	/**
	 * This method initializes authenticateButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSetTargetButton() {
		if (setTargetGridButton == null) {
			setTargetGridButton = new JButton();
			setTargetGridButton.setText("Set Target Grid");
			setTargetGridButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							setTargetGrids();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}
			});
			setTargetGridButton.setIcon(DorianLookAndFeel.getCertificateIcon());
		}
		return setTargetGridButton;
	}


	private void setTargetGrids() {
		getSetTargetButton().setEnabled(false);
		
		try {
			String selectedTargetGrid = (String) gridCombo.getSelectedItem();
			String targetGridDirectory = GAARDS_CONFIGURATION_DIRECTORY + File.separator + selectedTargetGrid;
			
			File gridSyncGTDCertsDir = null; //new File(GAARDS_CONFIGURATION_DIRECTORY + "/projects/syncgts/ext/target_grid/certificates");
			FilenameFilter fileOnlyFilter = new FilenameFilter() {
				
				public boolean accept(File dir, String name) {
					File file = new File(dir + "/" + name);
					if (file.isFile() && !name.equals(".svn"))
						return true;
					else
						return false;
				}
			};
			File[] gridSyncGTSCertFiles = null; //gridSyncGTDCertsDir.listFiles(fileOnlyFilter);

//			for (int i = 0; i < gridSyncGTSCertFiles.length; i++) {
//				gridSyncGTSCertFiles[i].delete();
//			}

			String gridSyncGTDCertsDirName = System.getProperty("user.home") + File.separator + ".globus" + File.separator + "certificates";
			gridSyncGTDCertsDir = new File(gridSyncGTDCertsDirName);
			if (gridSyncGTDCertsDir.exists()) {
				gridSyncGTSCertFiles = gridSyncGTDCertsDir.listFiles(fileOnlyFilter);

				for (int i = 0; i < gridSyncGTSCertFiles.length; i++) {
					gridSyncGTSCertFiles[i].delete();
				}
			} else {
				gridSyncGTDCertsDir.mkdirs();
			}
			
			File gridTargetCertsDir = new File(targetGridDirectory + File.separator + "certificates");
			File[] gridCertFiles = gridTargetCertsDir.listFiles(fileOnlyFilter);
			for (int i = 0; i < gridCertFiles.length; i++) {
//				copy(gridCertFiles[i], new File(GAARDS_CONFIGURATION_DIRECTORY + "/projects/syncgts/ext/target_grid/certificates", gridCertFiles[i].getName()));
				copy(gridCertFiles[i], new File(gridSyncGTDCertsDir, gridCertFiles[i].getName()));
			}
			
			// sync with trust fabric
			SyncGTSDefault.setServiceSyncDescriptionLocation(targetGridDirectory + File.separator + "sync-description.xml");
		
			SyncDescription description = SyncGTSDefault.getSyncDescription();

			SyncGTS sync = SyncGTS.getInstance();
			sync.syncOnce(description);
			
			// reload
			GridApplication.getContext().getConfigurationManager().setConfigurationDirectory(targetGridDirectory);
			GridApplication.getContext().getConfigurationManager().reload();
			ServicesManager.getInstance().syncWithUpdatedConfiguration();
			
			saveTargetGridToPropertyFile(selectedTargetGrid);
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}
		getSetTargetButton().setEnabled(true);
		getClose().doClick();
	}


	private JButton getClose() {
		if (close == null) {
			close = new JButton();
			close.setText("Close");
			close.setIcon(LookAndFeel.getCloseIcon());
			close.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return close;
	}
	
	private void copy(File source, File dest) throws IOException {
		FileChannel in = null, out = null;
		try {
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();

			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0,
					size);

			out.write(buf);

		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}
	
	private void saveTargetGridToPropertyFile(String targetGrid)
			throws Exception {
		String gaardsPropertyFileName = GAARDS_CONFIGURATION_DIRECTORY + File.separator + "gaards.properties";
		Properties gaardsPropertyFile = null;
		gaardsPropertyFile = new Properties();
		gaardsPropertyFile.load(new FileInputStream(gaardsPropertyFileName));
		gaardsPropertyFile.setProperty("target.grid", targetGrid);
		gaardsPropertyFile.store(new FileOutputStream(gaardsPropertyFileName), null);
	}

}
