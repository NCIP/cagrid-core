package gov.nih.nci.cagrid.introduce.portal.modification;

import gov.nih.nci.cagrid.introduce.common.ResourceManager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.BusyDialogRunnable;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.grape.utils.ErrorDialog;

public class RestoreDialog extends JDialog {

	private JPanel jContentPane = null;

	private JComboBox backupsComboBox = null;

	private JButton restoreButton = null;

	private String serviceName = null;

	private String baseDir = null;
	
	private boolean wasCanceled = false;

	private JLabel restoreLabel = null;

	public RestoreDialog(String serviceName, String baseDir) {
		this.serviceName = serviceName;
		this.baseDir = baseDir;
		initialize();

	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		setModal(true);
		this.setSize(new Dimension(300, 114));
		this.setTitle("Restore From Backup");
		this.setContentPane(getJContentPane());
		this.addWindowListener(new WindowAdapter() {
		
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				wasCanceled = true;
				setVisible(false);
                dispose();
			}
		
		});
		GridApplication.getContext().centerDialog(this);

	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 10, 2);
			gridBagConstraints11.gridy = 0;
			restoreLabel = new JLabel();
			restoreLabel.setText("Save Date/Time");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(2,2, 10, 2);
			gridBagConstraints.gridx = 1;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getBackupsComboBox(), gridBagConstraints);
			jContentPane.add(getRestoreButton(), gridBagConstraints1);
			jContentPane.add(restoreLabel, gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * This method initializes backupsComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getBackupsComboBox() {
		if (backupsComboBox == null) {
			backupsComboBox = new JComboBox();
			String[] files = ResourceManager.getBackups(serviceName);
			for (int i = 0; i < files.length; i++) {
				StringTokenizer strtok = new StringTokenizer(files[i], "_",
						false);
				strtok.nextToken();
				String timeS = strtok.nextToken();
				long time = Long.parseLong(timeS);
				Date date = new Date(time);
				backupsComboBox.addItem(date);
			}
		}
		return backupsComboBox;
	}

	/**
	 * This method initializes restoreButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRestoreButton() {
		if (restoreButton == null) {
			restoreButton = new JButton();
			restoreButton.setText("Restore");
			restoreButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							
							if(getBackupsComboBox().getItemCount()==0){
								ErrorDialog
								.showError("No selected or available services in cache");
								return;
							}
							
							int decision = JOptionPane
									.showConfirmDialog(
											RestoreDialog.this,
											"Are you sure you wish to roll back?\n" +
                                            "This will roll back to the last save point!\n" +
                                            "All current modifactions will be lost!\n" +
                                            "If you simply wish to throw away current modifications and reopen\n" +
                                            "the modification viewer to start again just close the window\n" +
                                            "and click Modify Service again.",
                                            "Are you sure", JOptionPane.YES_NO_OPTION);
							if (decision == JOptionPane.OK_OPTION) {
								BusyDialogRunnable r = new BusyDialogRunnable(
								    GridApplication.getContext().getApplication(), "Roll Back") {
									@Override
									public void process() {
										try {
											setProgressText("restoring from cache");
											ResourceManager
													.restoreSpecific(
															String
																	.valueOf(((Date) getBackupsComboBox()
																			.getSelectedItem())
																			.getTime()),
															serviceName,
															baseDir);
											dispose();
											
										} catch (Exception e1) {
											//e1.printStackTrace();
											CompositeErrorDialog
													.showErrorDialog("Unable to roll back: " + e1.getMessage());
											dispose();
											return;
										}
									}
								};
								Thread th = new Thread(r);
								th.start();
								
							} else {
								 wasCanceled = true;
							}
						}
					});
		}
		return restoreButton;
	}
	
	
	public boolean wasCanceled(){
		return wasCanceled;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
