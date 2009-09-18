package org.cagrid.gaards.ui.gridgrouper.browser;

import edu.internet2.middleware.grouper.Privilege;
import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.client.NamingPrivilege;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;

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
public class StemPrivilegeWindow extends ApplicationComponent {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private StemPrivilegeCaddy caddy;

	private JLabel jLabel = null;

	private JTextField identity = null;

	private JPanel privsPanel = null;

	private JCheckBox stem = null;

	private JLabel jLabel1 = null;

	private JCheckBox create = null;

	private JLabel jLabel2 = null;

	private JButton remove = null;

	private StemBrowser browser;

	private boolean update;

	private StemI targetStem;

	private JButton find = null;

    private JPanel titlePanel = null;


	/**
	 * This is the default constructor
	 */

	public StemPrivilegeWindow(StemBrowser browser) {
		this(browser, null);
	}


	public StemPrivilegeWindow(StemBrowser browser, StemPrivilegeCaddy caddy) {
		super();
		this.caddy = caddy;
		this.browser = browser;
		this.update = true;
		this.targetStem = browser.getStem();
		if(caddy==null){
			update = false;
		}else{
			update = true;
		}
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(500, 300);
		this.setContentPane(getJContentPane());
		if (update) {
			this.setTitle("Update Stem Privilege(s)");
		} else {
			this.setTitle("Grant Stem Privilege(s)");
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
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridheight = 1;
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.ipadx = 0;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridx = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.ipadx = 0;
			gridBagConstraints8.ipady = 0;
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.weighty = 1.0D;
			gridBagConstraints8.gridy = 1;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getMainPanel(), gridBagConstraints8);
			jContentPane.add(getTitlePanel(), gridBagConstraints9);
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
			mainPanel.add(getFind(), new GridBagConstraints());
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
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 3;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridy = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Create");
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
			jLabel1.setText("Stem");
			privsPanel = new JPanel();
			privsPanel.setLayout(new GridBagLayout());
			privsPanel.setBorder(BorderFactory.createTitledBorder(null, "Privilege(s)",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
				new Color(62, 109, 181)));
			privsPanel.add(getStem(), gridBagConstraints3);
			privsPanel.add(jLabel1, gridBagConstraints4);
			privsPanel.add(getCreate(), gridBagConstraints6);
			privsPanel.add(jLabel2, gridBagConstraints7);
		}
		return privsPanel;
	}


	/**
	 * This method initializes stem
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getStem() {
		if (stem == null) {
			stem = new JCheckBox();
			if ((caddy != null) && (caddy.hasStem())) {
				stem.setSelected(true);
			}
		}
		return stem;
	}


	/**
	 * This method initializes create
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCreate() {
		if (create == null) {
			create = new JCheckBox();
			if ((caddy != null) && (caddy.hasCreate())) {
				create.setSelected(true);
			}
		}
		return create;
	}


	/**
	 * This method initializes remove
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemove() {
		if (remove == null) {
			remove = new JButton();
			if (update) {
				remove.setText("Update Privilege(s)");
			} else {
				remove.setText("Add Privilege(s)");
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
			String s1 = addUpdateCreate();
			if (s1 != null) {
				reload = true;
				sb.append(s1 + "\n");
			}
			String s2 = addUpdateStem();
			if (s2 != null) {
				reload = true;
				sb.append(s2);
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


	private String addUpdateCreate() throws Exception {
		boolean createSelected = getCreate().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasCreate();
		}
		return addUpdatePrivilege(wasSelected, createSelected, NamingPrivilege.CREATE);
	}


	private String addUpdateStem() throws Exception {
		boolean stemSelected = getStem().isSelected();
		boolean wasSelected = false;
		if (caddy != null) {
			wasSelected = caddy.hasStem();
		}
		return addUpdatePrivilege(wasSelected, stemSelected, NamingPrivilege.STEM);
	}


	private String addUpdatePrivilege(boolean wasSelected, boolean selectedNow, Privilege priv) throws Exception {
		String id = Utils.clean(getIdentity().getText());
		Subject subj = SubjectUtils.getSubject(id);
		if (update) {
			if (!wasSelected && selectedNow) {
				try {
					targetStem.grantPriv(subj, priv);
					return "GRANTED " + priv.getName() + " privilege.";
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("ERROR granting " + priv.getName() + " privilege: " + e.getMessage());
				}

			} else if (wasSelected && !selectedNow) {
				try {
					targetStem.revokePriv(subj, priv);
					return "REVOKED " + priv.getName() + " privilege.";
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("ERROR revoking " + priv.getName() + " privilege: " + e.getMessage());
				}
			}
		} else {
			if (selectedNow) {
				try {
					targetStem.grantPriv(subj, priv);
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
            
            if(this.update){
             title = "Update Privilege(s)";
             subTitle = "Update a users privilege(s) on the stem "+browser.getStem().getDisplayExtension();
            }else{
                title = "Grant Privilege(s)";
                subTitle = "Grant a user privilege(s) on the stem "+browser.getStem().getDisplayExtension();
            }
            
            titlePanel = new TitlePanel(title,subTitle);
        }
        return titlePanel;
    }

}
