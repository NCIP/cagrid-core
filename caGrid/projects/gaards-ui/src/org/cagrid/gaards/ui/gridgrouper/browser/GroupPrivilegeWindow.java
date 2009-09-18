package org.cagrid.gaards.ui.gridgrouper.browser;

import edu.internet2.middleware.grouper.AccessPrivilege;
import edu.internet2.middleware.grouper.Privilege;
import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.client.Group;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GroupPrivilegeWindow extends ApplicationComponent {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private GroupPrivilegeCaddy caddy;

	private JLabel jLabel = null;

	private JTextField identity = null;

	private JPanel privsPanel = null;

	private JCheckBox admin = null;

	private JLabel jLabel1 = null;

	private JCheckBox update = null;

	private JLabel jLabel2 = null;

	private JButton remove = null;

	private GroupBrowser browser;

	private boolean isUpdate;

	private Group targetGroup;

	private JCheckBox view = null;

	private JLabel viewLabel = null;

	private JCheckBox read = null;

	private JLabel jLabel3 = null;

	private JCheckBox optin = null;

	private JLabel jLabel4 = null;

	private JCheckBox optout = null;

	private JLabel jLabel5 = null;

	private JButton find = null;

    private JPanel titlePanel = null;


	/**
	 * This is the default constructor
	 */

	public GroupPrivilegeWindow(GroupBrowser browser) {
		this(browser, null);
	}


	public GroupPrivilegeWindow(GroupBrowser browser, GroupPrivilegeCaddy caddy) {
		super();
		this.caddy = caddy;
		this.browser = browser;
		this.isUpdate = true;
		this.targetGroup = browser.getGroupNode().getGroup();
		if(caddy == null){
			isUpdate = false;
		}else{
			isUpdate = true;
		}
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(500, 250);
		this.setContentPane(getJContentPane());
		if (isUpdate) {
			this.setTitle("Update Group Privilege(s)");
		} else {
			this.setTitle("Add Group Privilege(s)");
		}
		this.setFrameIcon(GridGrouperLookAndFeel.getPrivilegesIcon());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints18.weightx = 1.0D;
			gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints18.gridy = 0;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.ipadx = 0;
			gridBagConstraints17.ipady = 0;
			gridBagConstraints17.fill = GridBagConstraints.BOTH;
			gridBagConstraints17.weightx = 1.0D;
			gridBagConstraints17.weighty = 1.0D;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.gridy = 1;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getMainPanel(), gridBagConstraints17);
			jContentPane.add(getTitlePanel(), gridBagConstraints18);
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
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 2;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.gridy = 0;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 0;
			gridBagConstraints61.gridwidth = 3;
			gridBagConstraints61.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints61.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints61.gridy = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridwidth = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 1.0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			jLabel = new JLabel();
			jLabel.setText("Identity");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridx = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(jLabel, gridBagConstraints1);
			mainPanel.add(getIdentity(), gridBagConstraints2);
			mainPanel.add(getPrivsPanel(), gridBagConstraints5);
			mainPanel.add(getRemove(), gridBagConstraints61);
			mainPanel.add(getFind(), gridBagConstraints16);
		}
		return mainPanel;
	}


	/**
	 * This method initializes identity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getIdentity() {
		if (identity == null) {
			identity = new JTextField();
			if (caddy != null) {
				identity.setText(caddy.getIdentity());
				identity.setEditable(false);
			}
		}
		return identity;
	}


	/**
	 * This method initializes privsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPrivsPanel() {
		if (privsPanel == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 5;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints15.gridy = 1;
			jLabel5 = new JLabel();
			jLabel5.setText("Optout");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 4;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.gridy = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 3;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridy = 1;
			jLabel4 = new JLabel();
			jLabel4.setText("Optin");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 2;
			gridBagConstraints12.gridy = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 1;
			jLabel3 = new JLabel();
			jLabel3.setText("Read");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridy = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 5;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridy = 0;
			viewLabel = new JLabel();
			viewLabel.setText("View");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 4;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridy = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 3;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridy = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Update");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.gridx = 0;
			jLabel1 = new JLabel();
			jLabel1.setText("Admin");
			privsPanel = new JPanel();
			privsPanel.setLayout(new GridBagLayout());
			privsPanel.setBorder(BorderFactory.createTitledBorder(null, "Privilege(s)",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
				new Color(62, 109, 181)));
			privsPanel.add(getAdmin(), gridBagConstraints3);
			privsPanel.add(jLabel1, gridBagConstraints4);
			privsPanel.add(getUpdate(), gridBagConstraints6);
			privsPanel.add(jLabel2, gridBagConstraints7);
			privsPanel.add(getView(), gridBagConstraints8);
			privsPanel.add(viewLabel, gridBagConstraints9);
			privsPanel.add(getRead(), gridBagConstraints10);
			privsPanel.add(jLabel3, gridBagConstraints11);
			privsPanel.add(getOptin(), gridBagConstraints12);
			privsPanel.add(jLabel4, gridBagConstraints13);
			privsPanel.add(getOptout(), gridBagConstraints14);
			privsPanel.add(jLabel5, gridBagConstraints15);
		}
		return privsPanel;
	}


	/**
	 * This method initializes admin
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getAdmin() {
		if (admin == null) {
			admin = new JCheckBox();
			if ((caddy != null) && (caddy.hasAdmin())) {
				admin.setSelected(true);
			}
		}
		return admin;
	}


	/**
	 * This method initializes update
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getUpdate() {
		if (update == null) {
			update = new JCheckBox();
			if ((caddy != null) && (caddy.hasUpdate())) {
				update.setSelected(true);
			}
		}
		return update;
	}


	/**
	 * This method initializes remove
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemove() {
		if (remove == null) {
			remove = new JButton();
			if (isUpdate) {
				remove.setText("Update Privilege(s)");
			} else {
				remove.setText("Grant Privilege(s)");
			}
			remove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							addUpdatePrivileges();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}

			});
		}
		return remove;
	}


	private void addUpdatePrivileges() {
		try {

			String id = Utils.clean(getIdentity().getText());
			if (id == null) {
				ErrorDialog.showError("Please enter a valid identity!!!");
			}
			boolean reload = false;
			StringBuffer sb = new StringBuffer();
			sb.append("Updating the privileges resulted in the following:\n");
			String s1 = addUpdateUpdate();
			if (s1 != null) {
				reload = true;
				sb.append(s1 + "\n");
			}
			String s2 = addUpdateAdmin();
			if (s2 != null) {
				reload = true;
				sb.append(s2 + "\n");
			}

			String s3 = addUpdateRead();
			if (s3 != null) {
				reload = true;
				sb.append(s3 + "\n");
			}
			String s4 = addUpdateView();
			if (s4 != null) {
				reload = true;
				sb.append(s4 + "\n");
			}

			String s5 = addUpdateOptin();
			if (s5 != null) {
				reload = true;
				sb.append(s5 + "\n");
			}

			String s6 = addUpdateOptout();
			if (s6 != null) {
				reload = true;
				sb.append(s6);
			}
			dispose();

			if (reload) {
				browser.loadPrivileges();
			}
			GridApplication.getContext().showMessage(sb.toString());
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}

	}


	private String addUpdateUpdate() throws Exception {
		boolean updateSelected = getUpdate().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasUpdate();
		}
		return addUpdatePrivilege(wasSelected, updateSelected, AccessPrivilege.UPDATE);
	}


	private String addUpdateView() throws Exception {
		boolean viewSelected = getView().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasView();
		}
		return addUpdatePrivilege(wasSelected, viewSelected, AccessPrivilege.VIEW);
	}


	private String addUpdateRead() throws Exception {
		boolean readSelected = getRead().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasRead();
		}
		return addUpdatePrivilege(wasSelected, readSelected, AccessPrivilege.READ);
	}


	private String addUpdateAdmin() throws Exception {
		boolean adminSelected = getAdmin().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasAdmin();
		}
		return addUpdatePrivilege(wasSelected, adminSelected, AccessPrivilege.ADMIN);
	}


	private String addUpdateOptin() throws Exception {
		boolean optinSelected = getOptin().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasOptin();
		}
		return addUpdatePrivilege(wasSelected, optinSelected, AccessPrivilege.OPTIN);
	}


	private String addUpdateOptout() throws Exception {
		boolean optoutSelected = getOptout().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasOptout();
		}
		return addUpdatePrivilege(wasSelected, optoutSelected, AccessPrivilege.OPTOUT);
	}


	private String addUpdatePrivilege(boolean wasSelected, boolean selectedNow, Privilege priv) throws Exception {
		String id = Utils.clean(getIdentity().getText());
		Subject subj = SubjectUtils.getSubject(id, true);
		if (isUpdate) {
			if (!wasSelected && selectedNow) {
				try {
					targetGroup.grantPriv(subj, priv);
					return "GRANTED " + priv.getName() + " privilege.";
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("ERROR granting " + priv.getName() + " privilege: " + e.getMessage());
				}

			} else if (wasSelected && !selectedNow) {
				try {
					targetGroup.revokePriv(subj, priv);
					return "REVOKED " + priv.getName() + " privilege.";
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("ERROR revoking " + priv.getName() + " privilege: " + e.getMessage());
				}
			}
		} else {
			if (selectedNow) {
				try {
					targetGroup.grantPriv(subj, priv);
					return "GRANTED " + priv.getName() + " privilege.";
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("ERROR granting " + priv.getName() + " privilege: " + e.getMessage());
				}
			}
		}
		return null;

	}


	/**
	 * This method initializes view
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getView() {
		if (view == null) {
			view = new JCheckBox();
			if ((caddy != null) && (caddy.hasView())) {
				view.setSelected(true);
			}
		}
		return view;
	}


	/**
	 * This method initializes read
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getRead() {
		if (read == null) {
			read = new JCheckBox();
			if ((caddy != null) && (caddy.hasRead())) {
				read.setSelected(true);
			}
		}
		return read;
	}


	/**
	 * This method initializes optin
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getOptin() {
		if (optin == null) {
			optin = new JCheckBox();
			if ((caddy != null) && (caddy.hasOptin())) {
				optin.setSelected(true);
			}
		}
		return optin;
	}


	/**
	 * This method initializes optout
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getOptout() {
		if (optout == null) {
			optout = new JCheckBox();
			if ((caddy != null) && (caddy.hasOptout())) {
				optout.setSelected(true);
			}
		}
		return optout;
	}


	/**
	 * This method initializes find	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFind() {
		if (find == null) {
			find = new JButton();
			find.setText("Find...");
			find.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UserSearchDialog dialog = new UserSearchDialog();
					dialog.setModal(true);
					GridApplication.getContext().showDialog(dialog);
					if (dialog.getSelectedUser() != null) {
						getIdentity().setText(dialog.getSelectedUser());
					}
				}
			});
		}
		return find;
	}


    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            String title="";
            String subTitle = "";
            
            if(this.isUpdate){
             title = "Update Privilege(s)";
             subTitle = "Update a users privilege(s) on the group "+browser.getGroupNode().getGroup().getDisplayExtension();
            }else{
                title = "Grant Privilege(s)";
                subTitle = "Grant a user privilege(s) on the group "+browser.getGroupNode().getGroup().getDisplayExtension();
            }
            
            titlePanel = new TitlePanel(title,subTitle);
        }
        return titlePanel;
    }

}
