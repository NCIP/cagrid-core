package org.cagrid.grape;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.configuration.TargetGridsConfiguration;
import org.cagrid.grape.model.Configuration;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.wsrf.encoding.ObjectDeserializer;

public class TargetGridBaseEditor extends ConfigurationBasePanel {
	private static Log log = LogFactory.getLog(TargetGridBaseEditor.class);
	
	private static final long serialVersionUID = 1L;

	private JPanel titlePanel = null;

	private JLabel titleLabel = null;

	private JLabel icon = null;

	private JPanel valuesPanel = null;

	private JPanel actionPanel = null;

	private JTextField repositoryName = null;

	private JButton addButton = null;

	private JButton removeButton = null;

	private JPanel priorityPanel = null;

	private JScrollPane jScrollPane1 = null;

	private GridTable grids = null;

	private JPanel buttonPanel = null;

	private JLabel repositoryNameLabel = null;
	
	private JLabel jLabel2 = null;

	private JTextField ivySettings = null;
	
	private JPanel ivySettingsPanel = null;

	private JButton browseIvySettings = null;
	
	private ConfigurationWindow window;
	private ConfigurationDescriptorTreeNode treeNode;
	
	public TargetGridBaseEditor(ConfigurationDescriptorTreeNode treeNode,
			Object conf) throws Exception {
		super(treeNode, conf);
		initialize();

		window = treeNode.getConfigurationWindow();
		this.treeNode = treeNode;
		
		loadGridTree();
		loadValues();
	}

	private void loadGridTree() throws Exception {
		Set<String> configurationNames = GAARDSApplication.getContext().getConfigurationManager().getConfigurationNames();
		for (String configurationName : configurationNames) {
			if ("default".equalsIgnoreCase(configurationName)) {
				continue;
			}
			Grid grid = GAARDSApplication.getContext()
					.getConfigurationManager().getConfigurationGrid(
							configurationName);

			TargetGridBaseTreeNode gridNode = new TargetGridBaseTreeNode(
					treeNode.getConfigurationWindow(), treeNode.getTree(), grid);
			treeNode.add(gridNode);
			gridNode.processConfigurationGroups();
		}
	}

	public TargetGridsConfiguration getTargetGridsConfiguration() {
		return (TargetGridsConfiguration) this.getConfigurationObject();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		jLabel2 = new JLabel();
		jLabel2.setText("Repository Settings");
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.weightx = 1.0D;
		gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints12.gridy = 2;
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.weighty = 1.0D;
		gridBagConstraints31.fill = GridBagConstraints.BOTH;
		gridBagConstraints31.gridy = 1;
		gridBagConstraints31.weightx = 1.0D;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		this.setSize(500, 400);
		this.setLayout(new GridBagLayout());
		this.add(getTitlePanel(), gridBagConstraints);
		this.add(getValuesPanel(), gridBagConstraints31);
		this.add(getActionPanel(), gridBagConstraints12);
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 0.0D;
			gridBagConstraints2.gridy = 0;
			icon = new JLabel(LookAndFeel.getLogoNoText22x22());
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridx = 1;
			titleLabel = new JLabel();
			titleLabel.setText("Grid Configurations");
			titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.add(icon, gridBagConstraints2);
			titlePanel.add(titleLabel, gridBagConstraints1);
		}
		return titlePanel;
	}

	private void loadValues() {
		this.grids.clearTable();
		TargetGridsConfiguration conf = getTargetGridsConfiguration();
		if (conf != null) {
			Grid[] list = conf.getGrid();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					getGrids().addGrid(list[i]);
				}
			}
		}
	}

	/**
	 * This method initializes valuesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getValuesPanel() {
		if (valuesPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			valuesPanel = new JPanel();
			valuesPanel.setLayout(new GridBagLayout());
			valuesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, "Target Grids",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					LookAndFeel.getPanelLabelColor()));
			valuesPanel.add(getPriorityPanel(), gridBagConstraints6);
			valuesPanel.add(getJScrollPane1(), gridBagConstraints3);
		}
		return valuesPanel;
	}

	/**
	 * This method initializes actionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridy = 2;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridx = 0;
			repositoryNameLabel = new JLabel();
			repositoryNameLabel.setText("Repository Name");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.gridy = 3;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.weightx = 1.0D;
			actionPanel = new JPanel();
			actionPanel.setLayout(new GridBagLayout());
			actionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, "Add Grid Repository",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					LookAndFeel.getPanelLabelColor()));
			actionPanel.add(getRepositoryName(), gridBagConstraints7);
			actionPanel.add(getButtonPanel(), gridBagConstraints9);
			actionPanel.add(repositoryNameLabel, gridBagConstraints4);
			actionPanel.add(jLabel2, gridBagConstraints11);
			actionPanel.add(getIvySettingsPanel(), gridBagConstraints13);
		}
		return actionPanel;
	}

	/**
	 * This method initializes displayName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRepositoryName() {
		if (repositoryName == null) {
			repositoryName = new JTextField();
		}
		return repositoryName;
	}

	/**
	 * This method initializes addButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.setIcon(LookAndFeel.getAddIcon());
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addValue();
				}
			});
		}
		return addButton;
	}

	/**
	 * This method initializes removeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove Repository");
			removeButton.setIcon(LookAndFeel.getRemoveIcon());
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {						
						if (removingRepositoryWithActiveGrid(getGrids().getSelectedGrid())) {
							ErrorDialog.showError("Cannot remove a repository if it contains the target grid.");
							log.error("Cannot remove a repository if it contains the target grid.");
							return;
						}
					
						removeValue();
						GridApplication.getContext().getConfigurationManager().saveAll();
						window.dispose();
						
					} catch (Exception ex) {
						log.error(ex, ex);
						GridApplication.getContext().showMessage(
								Utils.getExceptionMessage(ex));
					}
									
				}

			});
		}
		return removeButton;
	}
	
	private boolean removingRepositoryWithActiveGrid(Grid selectedGrid) {
		TargetGridsConfiguration conf = getTargetGridsConfiguration();
		Grid[] grids = conf.getGrid();
		if (grids != null) {
			for (int i = 0; i < grids.length; i++) {
				if (GAARDSApplication.getTargetGrid().equals(grids[i].getSystemName())) {
					if (grids[i].getIvySettings().equals(selectedGrid.getIvySettings())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private void addValue() {
		String repositoryName = Utils.clean(getRepositoryName().getText());
		String ivySettings = Utils.clean(getIvySettings().getText());

		if (repositoryName == null) {
			GridApplication.getContext().showMessage(
					"Please specify a repository name!!!");
			return;
		}
		if ("default".equalsIgnoreCase(repositoryName)) {
			GridApplication.getContext().showMessage(
					"Repository name cannot be default");
			return;
		}

		
		if (ivySettings == null) {
			GridApplication.getContext().showMessage(
					"Please specify the ivy configuration!!!");
			return;
		}

		File in = new File(ivySettings);
		File out = new File(GAARDSApplication.getGAARDSConfigurationDirectory(), "ivysettings-" + repositoryName + ".xml");
		try {
			Utils.copyFile(in, out);
		} catch (IOException e) {
			ErrorDialog.showError("Unable to copy the repository configuration file into gaards configuration directory.");
			log.error("Unable to copy the repository configuration file into gaards configuration directory.", e);
		}

		TargetGridsManager gridManager = new TargetGridsManager(GAARDSApplication.getGAARDSConfigurationDirectory(),
				GAARDSApplication.getContext().getConfigurationManager());
		TargetGridsConfiguration targetGridsConfiguration = getTargetGridsConfiguration();
		try {
			gridManager.getGridsFromRepository(out, targetGridsConfiguration);
		} catch (Exception e) {
			ErrorDialog.showError("Encountered an error while trying to obtain target grids from the repository.");
			log.error("Encountered an error while trying to obtain target grids from the repository.", e);
		}
		
		loadValues();
		getRepositoryName().setText("");
		getIvySettings().setText("");

	}
	
	private Configuration loadConfiguration() throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("/configuration.xml");
		
        org.w3c.dom.Document doc = XMLUtils.newDocument(inputStream);
        Object obj = ObjectDeserializer.toObject(doc.getDocumentElement(), Configuration.class);
        inputStream.close();
        return Configuration.class.cast(obj);

	}

	private void removeValue() {
		try {
			Grid grid = getGrids().getSelectedGrid();
			TargetGridsConfiguration conf = getTargetGridsConfiguration();
			if (conf != null) {
				Grid[] grids = conf.getGrid();
				if (grids != null) {
					List<Grid> newList = new ArrayList<Grid>();
					for (int i = 0; i < grids.length; i++) {
						if (!grid.getIvySettings().equals(grids[i].getIvySettings())) {
							newList.add(grids[i]);
						}
					}
					File settingsFile = new File(GAARDSApplication.getGAARDSConfigurationDirectory(), grid.getIvySettings());
					settingsFile.delete();
					getGrids().clearTable();
					Grid[] des = new Grid[newList.size()];
					for (int i = 0; i < newList.size(); i++) {
						des[i] = newList.get(i);
						getGrids().addGrid(des[i]);
					}
					conf.setGrid(des);
					loadValues();
				}				
			}

		} catch (Exception e) {
			FaultUtil.logFault(log, e);
			GridApplication.getContext().showMessage(
					Utils.getExceptionMessage(e));
		}
	}

	public void showErrorMessage(String title, String msg) {
		showErrorMessage(title, new String[] { msg });
	}

	public void showErrorMessage(String title, String[] msg) {
		JOptionPane.showMessageDialog(this, msg, title,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * This method initializes priorityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPriorityPanel() {
		if (priorityPanel == null) {
			priorityPanel = new JPanel();
			priorityPanel.setLayout(new FlowLayout());
			priorityPanel.add(getRemoveButton(), null);
		}
		return priorityPanel;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getGrids());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes services
	 * 
	 * @return javax.swing.JTable
	 */
	private GridTable getGrids() {
		if (grids == null) {
			grids = new GridTable();
		}
		return grids;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getAddButton(), null);
		}
		return buttonPanel;
	}

	private JPanel getIvySettingsPanel() {
		if (ivySettingsPanel == null) {
			ivySettingsPanel = new JPanel();
			ivySettingsPanel.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
			gridBagConstraints0.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints0.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints0.gridx = 0;
			gridBagConstraints0.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints0.gridy = 0;
			gridBagConstraints0.weighty = 1.0D;
			gridBagConstraints0.weightx = 1.0;
			gridBagConstraints0.gridwidth = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints1.gridwidth = 1;

			ivySettingsPanel.add(getIvySettings(), gridBagConstraints0);

			ivySettingsPanel.add(getBrowseIvySettings(), gridBagConstraints1);
		}

		return ivySettingsPanel;
	}

	private JTextField getIvySettings() {
		if (ivySettings == null) {
			ivySettings = new JTextField();
		}
		return ivySettings;
	}

	/**
	 * This method initializes browseIvySettings	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBrowseIvySettings() {
		if (browseIvySettings == null) {
			browseIvySettings = new JButton();
			browseIvySettings.setText("Browse");
			browseIvySettings.setIcon(LookAndFeel.getImportIcon());
			browseIvySettings.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						String previous = getIvySettings().getText();
						String location = promptDir(previous);
						if (location != null && location.length() > 0) {
							getIvySettings().setText(location);
						} else {
							getIvySettings().setText(previous);
						}
						// validateInput();
					} catch (Exception ex) {
						log.error(ex, ex);
					}
				}
			});
		}
		return browseIvySettings;
	}
	
    public static String promptDir(String defaultLocation) throws IOException {
        return promptDir(GridApplication.getContext().getApplication(), defaultLocation);
    }

    public static String promptDir(Component owner, String defaultLocation) throws IOException {
        JFileChooser chooser = new JFileChooser();
        
        class XMLFilter extends FileFilter {
            public boolean accept(File file) {
                String filename = file.getName();
                return filename.endsWith(".xml");
            }
            public String getDescription() {
                return "*.xml";
            }
        }
  
        chooser.setApproveButtonText("Open");
        chooser.setDialogTitle("Select Ivy Settings File");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileHidingEnabled(true);
        chooser.addChoosableFileFilter(new XMLFilter());
        chooser.setMultiSelectionEnabled(false);
        GridApplication.getContext().centerComponent(chooser);

        int returnVal = chooser.showOpenDialog(owner);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

}
