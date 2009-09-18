package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.FederationAudit;
import org.cagrid.gaards.dorian.federation.FederationAuditFilter;
import org.cagrid.gaards.dorian.federation.FederationAuditRecord;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.SelectDateDialog;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

public class FederationAuditPanel extends JPanel {


	public static int FULL_MODE = 1;

	public static int GRID_ACCOUNT_MODE = 2;

	public static int IDP_MODE = 3;

	public static int HOST_MODE = 4;

	public static int USER_CERTIFICATE_MODE = 5;

	private static final long serialVersionUID = 1L;

	private JPanel searchCriteria = null;

	private JPanel targetPanel = null;

	private JPanel datePanel = null;

	private JTextField target = null;

	private JLabel jLabel = null;

	private JButton findTarget = null;

	private JLabel jLabel1 = null;

	private JTextField reportingParty = null;

	private JButton findReportingParty = null;

	private JLabel jLabel2 = null;

	private JTextField startDate = null;

	private JButton selectStartDate = null;

	private JLabel jLabel3 = null;

	private JTextField endDate = null;

	private JButton selectEndDate = null;

	private Calendar searchStartDate = null;

	private Calendar searchEndDate = null;

	private FederationAuditComboBox auditType = null;

	private JTextField message = null;

	private JPanel buttonPanel = null;

	private JButton search = null;

	private JButton clear = null;

	private JLabel jLabel41 = null;

	private JScrollPane tablePanel = null;

	private FederationAuditRecordTable auditRecords = null;

	private DorianSessionProvider session;

	private List<FederationAudit> auditTypes; // @jve:decl-index=0:

	private JPanel messagePanel = null;

	private JLabel jLabel4 = null;

	private ProgressPanel progess;

	/**
	 * This is the default constructor
	 */
	public FederationAuditPanel(DorianSessionProvider session) {
		this(session, FULL_MODE, null);
	}

	public FederationAuditPanel(DorianSessionProvider session, int mode,
			String target) {
		super();
		this.session = session;
		this.setupAuditTypes(mode);
		initialize();
		if (target != null) {
			this.target.setEditable(false);
			this.target.setVisible(false);
			this.target.setEnabled(false);
			this.findTarget.setVisible(false);
			this.findTarget.setEnabled(false);
			this.jLabel.setEnabled(false);
			this.jLabel.setVisible(false);
			this.target.setText(target);
		}

	}

	private void performAudit() {
		getSearch().setEnabled(false);
		getClear().setEnabled(false);
		getAuditRecords().clearTable();
		try {
			if ((searchStartDate != null) && (searchEndDate != null)) {
				if (searchStartDate.after(searchEndDate)) {
					ErrorDialog
							.showError("The start date cannot be after the end date.");
					return;
				}
			}
			showProgess("Searching...");
			FederationAuditFilter f = new FederationAuditFilter();
			f.setTargetId(Utils.clean(getTarget().getText()));
			f.setReportingPartyId(Utils.clean(getReportingParty().getText()));
			f.setAuditMessage(Utils.clean(getMessage().getText()));
			f.setAuditType(getAuditType().getSelectedAuditType());

			if (searchStartDate != null) {
				f.setStartDate(searchStartDate);
			}

			if (searchEndDate != null) {
				f.setEndDate(searchEndDate);
			}

			GridAdministrationClient client = this.session.getSession()
					.getAdminClient();
			List<FederationAuditRecord> records = client.performAudit(f);
			this.getAuditRecords().addRecords(records);
			stopProgess(records.size() + " audit record(s) found.");
		} catch (Exception f) {
			stopProgess("Error");
			ErrorDialog.showError(f);
			return;
		} finally {
			getSearch().setEnabled(true);
			getClear().setEnabled(true);
		}
	}

	private void clearEntries() {
		if (target.isVisible()) {
			target.setText("");
		}
		reportingParty.setText("");
		auditType.setToAny();
		this.message.setText("");
		this.startDate.setText("");
		this.endDate.setText("");
		this.searchStartDate = null;
		this.searchEndDate = null;
	}

	private void setupAuditTypes(int mode) {
		this.auditTypes = new ArrayList<FederationAudit>();
		if (mode == GRID_ACCOUNT_MODE) {
			this.auditTypes.add(FederationAudit.AccountCreated);
			this.auditTypes.add(FederationAudit.AccountRemoved);
			this.auditTypes.add(FederationAudit.AccountUpdated);
			this.auditTypes.add(FederationAudit.AdminAdded);
			this.auditTypes.add(FederationAudit.AdminRemoved);
			this.auditTypes.add(FederationAudit.AccessDenied);
			this.auditTypes
					.add(FederationAudit.SuccessfulUserCertificateRequest);
			this.auditTypes
					.add(FederationAudit.InvalidUserCertificateRequest);

		} else if (mode == IDP_MODE) {
			this.auditTypes.add(FederationAudit.IdPAdded);
			this.auditTypes.add(FederationAudit.IdPRemoved);
			this.auditTypes.add(FederationAudit.IdPUpdated);
		} else if (mode == HOST_MODE) {
			this.auditTypes.add(FederationAudit.HostCertificateRequested);
			this.auditTypes.add(FederationAudit.HostCertificateApproved);
			this.auditTypes.add(FederationAudit.HostCertificateRenewed);
			this.auditTypes.add(FederationAudit.HostCertificateUpdated);
		} else if (mode == USER_CERTIFICATE_MODE) {
			this.auditTypes.add(FederationAudit.UserCertificateUpdated);
			this.auditTypes.add(FederationAudit.UserCertificateRemoved);
		} else {
			Class c = FederationAudit.class;
			Field[] fields = FederationAudit.class.getFields();
			for (int i = 0; i < fields.length; i++) {
				if (FederationAudit.class.isAssignableFrom(fields[i]
						.getType())) {
					try {
						FederationAudit o = (FederationAudit) fields[i]
								.get(null);
						this.auditTypes.add(o);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
		gridBagConstraints110.fill = GridBagConstraints.BOTH;
		gridBagConstraints110.weighty = 1.0;
		gridBagConstraints110.gridx = 0;
		gridBagConstraints110.gridy = 1;
		gridBagConstraints110.weightx = 1.0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		this.setSize(500, 400);
		this.setLayout(new GridBagLayout());
		this.add(getSearchCriteria(), gridBagConstraints);
		this.add(getTablePanel(), gridBagConstraints110);
	}

	/**
	 * This method initializes searchCriteria
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSearchCriteria() {
		if (searchCriteria == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.weightx = 1.0D;
			gridBagConstraints22.gridy = 2;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridx = 0;
			searchCriteria = new JPanel();
			searchCriteria.setLayout(new GridBagLayout());
			searchCriteria.add(getTargetPanel(), gridBagConstraints1);
			searchCriteria.setBorder(BorderFactory.createTitledBorder(null,
					"Search Criteria", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			searchCriteria.add(getDatePanel(), gridBagConstraints2);
			searchCriteria.add(getButtonPanel(), gridBagConstraints19);
			searchCriteria.add(getMessagePanel(), gridBagConstraints22);
		}
		return searchCriteria;
	}

	/**
	 * This method initializes targetPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTargetPanel() {
		if (targetPanel == null) {
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.gridx = 0;
			gridBagConstraints20.anchor = GridBagConstraints.WEST;
			gridBagConstraints20.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints20.gridy = 2;
			jLabel41 = new JLabel();
			jLabel41.setText("Audit Type");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridy = 2;
			gridBagConstraints17.weightx = 1.0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.gridwidth = 2;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 3;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.gridy = 2;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridx = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Target");
			jLabel1 = new JLabel();
			jLabel1.setText("Reporting Party");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.weightx = 1.0;
			targetPanel = new JPanel();
			targetPanel.setLayout(new GridBagLayout());
			targetPanel.add(getTarget(), gridBagConstraints3);
			targetPanel.add(jLabel, gridBagConstraints4);
			targetPanel.add(getFindTarget(), gridBagConstraints5);
			targetPanel.add(jLabel1, gridBagConstraints6);
			targetPanel.add(getReportingParty(), gridBagConstraints7);
			targetPanel.add(getFindReportingParty(), gridBagConstraints8);
			targetPanel.add(getAuditType(), gridBagConstraints17);
			targetPanel.add(jLabel41, gridBagConstraints20);
		}
		return targetPanel;
	}

	/**
	 * This method initializes datePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDatePanel() {
		if (datePanel == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 5;
			gridBagConstraints14.gridy = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridx = 4;
			gridBagConstraints13.gridy = 1;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.weightx = 1.0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 3;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 1;
			jLabel3 = new JLabel();
			jLabel3.setText("End Date");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.weightx = 1.0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridy = 1;
			jLabel2 = new JLabel();
			jLabel2.setText("Start Date");
			datePanel = new JPanel();
			datePanel.setLayout(new GridBagLayout());
			datePanel.add(jLabel2, gridBagConstraints9);
			datePanel.add(getStartDate(), gridBagConstraints10);
			datePanel.add(getSelectStartDate(), gridBagConstraints11);
			datePanel.add(jLabel3, gridBagConstraints12);
			datePanel.add(getEndDate(), gridBagConstraints13);
			datePanel.add(getSelectEndDate(), gridBagConstraints14);
		}
		return datePanel;
	}

	/**
	 * This method initializes target
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTarget() {
		if (target == null) {
			target = new JTextField();
		}
		return target;
	}

	/**
	 * This method initializes findTarget
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFindTarget() {
		if (findTarget == null) {
			findTarget = new JButton();
			findTarget.setText("Find");
			findTarget.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String id = findIdentity();
					if (id != null) {
						getTarget().setText(id);
					}

				}
			});
		}
		return findTarget;
	}

	private String findIdentity() {
		IdentityFinderDialog ifd = new IdentityFinderDialog(this.session,
				GridApplication.getContext().getApplication());
		GridApplication.getContext().showDialog(ifd);
		return ifd.getIdentity();
	}

	/**
	 * This method initializes reportingParty
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getReportingParty() {
		if (reportingParty == null) {
			reportingParty = new JTextField();
		}
		return reportingParty;
	}

	/**
	 * This method initializes findReportingParty
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFindReportingParty() {
		if (findReportingParty == null) {
			findReportingParty = new JButton();
			findReportingParty.setText("Find");
			findReportingParty
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							String id = findIdentity();
							if (id != null) {
								getReportingParty().setText(id);
							}

						}
					});
		}
		return findReportingParty;
	}

	/**
	 * This method initializes startDate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getStartDate() {
		if (startDate == null) {
			startDate = new JTextField();
			startDate.setEditable(false);
		}
		return startDate;
	}

	/**
	 * This method initializes selectStartDate
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSelectStartDate() {
		if (selectStartDate == null) {
			selectStartDate = new JButton();
			selectStartDate.setText("Select");
			selectStartDate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							SelectDateDialog dialog = new SelectDateDialog(
									false);
							dialog.setModal(true);
							GridApplication.getContext().showDialog(dialog);
							Calendar c = dialog.getDate();
							if (c != null) {
								searchStartDate = c;
								getStartDate().setText(
										formatDate(searchStartDate));
							}
						}
					});
		}
		return selectStartDate;
	}

	/**
	 * This method initializes endDate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getEndDate() {
		if (endDate == null) {
			endDate = new JTextField();
			endDate.setEditable(false);
		}
		return endDate;
	}

	/**
	 * This method initializes selectEndDate
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSelectEndDate() {
		if (selectEndDate == null) {
			selectEndDate = new JButton();
			selectEndDate.setText("Select");
			selectEndDate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							SelectDateDialog dialog = new SelectDateDialog(
									false);
							dialog.setModal(true);
							GridApplication.getContext().showDialog(dialog);
							Calendar c = dialog.getDate();
							if (c != null) {
								searchEndDate = c;
								getEndDate().setText(formatDate(searchEndDate));
							}
						}
					});
		}
		return selectEndDate;
	}

	private String formatDate(Calendar c) {
		StringBuffer sb = new StringBuffer();
		int month = c.get(Calendar.MONTH) + 1;
		if (month < 10) {
			sb.append("0" + month);
		} else {
			sb.append(month);
		}

		sb.append("/");

		int day = c.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			sb.append("0" + day);
		} else {
			sb.append(day);
		}

		sb.append("/");

		int year = c.get(Calendar.YEAR);
		sb.append(year);

		sb.append("  @  ");

		int hour = c.get(Calendar.HOUR);
		if (hour == 0) {
			hour = 12;
		}
		if (hour < 10) {
			sb.append("0" + hour);
		} else {
			sb.append(hour);
		}

		sb.append(":");

		int minute = c.get(Calendar.MINUTE);
		if (minute < 10) {
			sb.append("0" + minute);
		} else {
			sb.append(minute);
		}
		if (c.get(Calendar.AM_PM) == Calendar.AM) {
			sb.append(" AM");
		} else {
			sb.append(" PM");
		}
		return sb.toString();
	}

	/**
	 * This method initializes auditType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private FederationAuditComboBox getAuditType() {
		if (auditType == null) {
			auditType = new FederationAuditComboBox(this.auditTypes);
		}
		return auditType;
	}

	/**
	 * This method initializes message
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getMessage() {
		if (message == null) {
			message = new JTextField();
		}
		return message;
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
			buttonPanel.add(getSearch(), null);
			buttonPanel.add(getClear(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes search
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSearch() {
		if (search == null) {
			search = new JButton();
			search.setText("Search");

			search.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							performAudit();
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}

		return search;
	}

	public void setSearchButtonAsDefault(JRootPane root) {
		root.setDefaultButton(getSearch());
	}

	/**
	 * This method initializes clear
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getClear() {
		if (clear == null) {
			clear = new JButton();
			clear.setText("Clear");
			clear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					clearEntries();
				}
			});
		}
		return clear;
	}

	/**
	 * This method initializes tablePanel
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getTablePanel() {
		if (tablePanel == null) {
			tablePanel = new JScrollPane();
			tablePanel.setViewportView(getAuditRecords());
			tablePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Audit Results", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
		}
		return tablePanel;
	}

	/**
	 * This method initializes auditRecords
	 * 
	 * @return javax.swing.JTable
	 */
	private FederationAuditRecordTable getAuditRecords() {
		if (auditRecords == null) {
			auditRecords = new FederationAuditRecordTable();
		}
		return auditRecords;
	}

	/**
	 * This method initializes messagePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints18.gridwidth = 2;
			gridBagConstraints18.gridx = 1;
			gridBagConstraints18.gridy = 0;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 0;
			gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.gridy = 0;
			jLabel4 = new JLabel();
			jLabel4.setText("Message");
			messagePanel = new JPanel();
			messagePanel.setLayout(new GridBagLayout());
			messagePanel.add(jLabel4, gridBagConstraints23);
			messagePanel.add(getMessage(), gridBagConstraints18);
		}
		return messagePanel;
	}

	private void showProgess(String s) {
		if (this.progess != null) {
			this.progess.showProgress(s);
		}
	}

	private void stopProgess(String s) {
		if (this.progess != null) {
			this.progess.stopProgress(s);
		}
	}

	public void setProgess(ProgressPanel progess) {
		this.progess = progess;
	}

}
