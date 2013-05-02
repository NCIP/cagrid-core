/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.ui.gridgrouper.browser;

import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.SchemaException;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gridgrouper.client.MembershipRequest;
import gov.nih.nci.cagrid.gridgrouper.client.MembershipRequestHistory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;

public class MembershipRequestWindow extends ApplicationComponent {
	private static Log log = LogFactory.getLog(MembershipRequestWindow.class);
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel detailsPanel = null;

	private JPanel notePanel = null;

	private MembershipRequest membershipRequest;

	private JLabel jLabel1 = null;

	private JTextField group = null;

	private JLabel jLabel2 = null;

	private JPanel mainPanel = null;

	private JPanel buttonPanel = null;

	private JButton approveButton = null;

	private JTextField member = null;

	private JTextArea publicNote = null;

	private JLabel publicNoteLabel = null;

	private JButton rejectButton = null;

	private JLabel adminNoteLabel = null;

	private JTextArea adminNote = null;

	private JPanel historyPanel = null;

	private JLabel historyLabel = null;

	private MembershipRequestHistoryTable requestHistoryTable = null;
	
    private JScrollPane requestHistoryJScrollPane = null;

	private JLabel currentStatusLabel = null;

	private JTextField currentStatus = null;
	
    private JPanel titlePanel = null;

	/**
	 * This is the default constructor
	 */
	public MembershipRequestWindow(MembershipRequest membershipRequest) {
		super();
		this.membershipRequest = membershipRequest;
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(500, 400);
		this.setContentPane(getJContentPane());
		this.setTitle("Review Membership Request");
		this.setFrameIcon(GridGrouperLookAndFeel.getMemberIcon22x22());
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
			jContentPane.add(getMainPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}


	/**
	 * This method initializes detailsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDetailsPanel() {
		if (detailsPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			currentStatusLabel = new JLabel();
			currentStatusLabel.setText("Current Status:");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			jLabel2 = new JLabel();
			jLabel2.setText("Member");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Group");
			detailsPanel = new JPanel();
			detailsPanel.setLayout(new GridBagLayout());
			detailsPanel.add(jLabel1, gridBagConstraints11);
			detailsPanel.add(getGroup(), gridBagConstraints2);
			detailsPanel.add(jLabel2, gridBagConstraints3);
			detailsPanel.add(getMember(), gridBagConstraints1);
			detailsPanel.add(currentStatusLabel, gridBagConstraints6);
			detailsPanel.add(getCurrentStatus(), gridBagConstraints7);
		}
		return detailsPanel;
	}


	/**
	 * This method initializes group
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGroup() {
		if (group == null) {
			group = new JTextField();
			group.setEditable(false);
			group.setText(membershipRequest.getGroupName());
		}
		return group;
	}

	/**
	 * This method initializes mainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.weightx = 1.0D;
            gridBagConstraints10.gridy = 0;

			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridy = 3;
			gridBagConstraints31.fill = GridBagConstraints.BOTH;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridx = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 4;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.weightx = 1.0D;
			gridBagConstraints4.weighty = 1.0D;
			gridBagConstraints4.gridy = 1;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getTitlePanel(), gridBagConstraints10);
			mainPanel.add(getDetailsPanel(), gridBagConstraints4);
			mainPanel.add(getButtonPanel(), gridBagConstraints9);
			mainPanel.add(getNotePanel(), gridBagConstraints);
			mainPanel.add(getHistoryPanel(), gridBagConstraints31);
		}
		return mainPanel;
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
			buttonPanel.add(getApproveButton(), null);
			buttonPanel.add(getRejectButton(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes addMember
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getApproveButton() {
		if (approveButton == null) {
			approveButton = new JButton();
			approveButton.setText("Approve");
			approveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							try {
								approveMembershipRequest();
							} catch (Exception e) {
								ErrorDialog.showError(e);
								FaultUtil.logFault(log, e);
							}
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						FaultUtil.logFault(log, t);
					}
				}

			});
		}
		return approveButton;
	}


	private void approveMembershipRequest() throws InsufficientPrivilegeException, SchemaException {
		membershipRequest.approve(getPublicNote().getText(), getAdminNote().getText());
		dispose();
	}

	private void rejectMembershipRequest() throws InsufficientPrivilegeException, SchemaException {
		membershipRequest.reject(getPublicNote().getText(), getAdminNote().getText());
		dispose();
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMember() {
		if (member == null) {
			member = new JTextField();
			member.setEditable(false);
			member.setText(membershipRequest.getRequestorId());
		}
		return member;
	}


	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JPanel getNotePanel() {
		if (notePanel == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.BOTH;
			gridBagConstraints22.gridy = 3;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.weighty = 1.0;
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 2;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			adminNoteLabel = new JLabel();
			adminNoteLabel.setText("Administrative Note:");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			publicNoteLabel = new JLabel();
			publicNoteLabel.setText("General Note:");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);

			notePanel = new JPanel();
			notePanel.setLayout(new GridBagLayout());
			notePanel.add(getPublicNote(), gridBagConstraints);
			notePanel.add(publicNoteLabel, gridBagConstraints21);
			notePanel.add(adminNoteLabel, gridBagConstraints12);
			notePanel.add(getAdminNote(), gridBagConstraints22);
		}
		return notePanel;
	}
	
	private JTextArea getPublicNote() {
		if (publicNote == null) {
			publicNote = new JTextArea();
			publicNote.setText(membershipRequest.getPublicNote());
		}
		return publicNote;
	}


	/**
	 * This method initializes rejectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRejectButton() {
		if (rejectButton == null) {
			rejectButton = new JButton();
			rejectButton.setText("Reject");
			rejectButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							try {
								rejectMembershipRequest();
							} catch (Exception e) {
								ErrorDialog.showError(e);
								FaultUtil.logFault(log, e);
							}
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						FaultUtil.logFault(log, t);
					}
				}

			});
		}
		return rejectButton;
	}


	/**
	 * This method initializes jAdminNoteTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getAdminNote() {
		if (adminNote == null) {
			adminNote = new JTextArea();
			adminNote.setText(membershipRequest.getAdminNote());
		}
		return adminNote;
	}


	/**
	 * This method initializes historyPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHistoryPanel() {
		if (historyPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.weighty = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.weightx = 1.0;
			
			historyPanel = new JPanel();
			historyPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			historyLabel = new JLabel();
			historyLabel.setText("Status History:");
			historyPanel.add(historyLabel, gridBagConstraints);

			historyPanel.add(getRequestHistoryJScrollPane(), gridBagConstraints5);
		}
		return historyPanel;
	}

	private JScrollPane getRequestHistoryJScrollPane() {
		if (requestHistoryJScrollPane == null) {
			requestHistoryJScrollPane = new JScrollPane();
			requestHistoryJScrollPane.setViewportView(getRequestHistoryTable());
		}
		return requestHistoryJScrollPane;
	}

	/**
	 * This method initializes requestHistoryTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getRequestHistoryTable() {
		if (requestHistoryTable == null) {
			requestHistoryTable = new MembershipRequestHistoryTable();
			try {
				Set i = membershipRequest.getHistory();
				Iterator itr = i.iterator();
				while (itr.hasNext()) {
					MembershipRequestHistory m = (MembershipRequestHistory) itr.next();
					requestHistoryTable.addMembershipRequestHistory(m);
				}
			} catch (Exception e) {
				ErrorDialog.showError(e);
				FaultUtil.logFault(log, e);

			}

		}
		return requestHistoryTable;
	}

	/**
	 * This method initializes currentStatus	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCurrentStatus() {
		if (currentStatus == null) {
			currentStatus = new JTextField();
			currentStatus.setEditable(false);
			currentStatus.setText(membershipRequest.getStatus().getValue());
		}
		return currentStatus;
	}
	
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Review Membership Requests Browser", "Browse and administrate membership requests.");
        }
        return titlePanel;
    }


}  //  @jve:decl-index=0:visual-constraint="10,10"
