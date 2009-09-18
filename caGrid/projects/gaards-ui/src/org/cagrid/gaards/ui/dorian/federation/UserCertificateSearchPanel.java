package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.DateRange;
import org.cagrid.gaards.dorian.federation.UserCertificateFilter;
import org.cagrid.gaards.dorian.federation.UserCertificateRecord;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.SelectDateDialog;
import org.cagrid.gaards.ui.dorian.DorianSession;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

public class UserCertificateSearchPanel extends JPanel implements DorianSessionProvider{

	private static final long serialVersionUID = 1L;

	private JPanel searchCriteriaPanel = null;

	private JPanel datePanel = null;

	private JLabel jLabel2 = null;

	private JTextField startDate = null;

	private JLabel jLabel3 = null;

	private JButton startDateSelector = null;

	private JTextField endDate = null;

	private JButton endDateSelector = null;

	private JPanel otherCriteria = null;

	private JLabel jLabel4 = null;

	private JTextField userCertificateSerialNumber = null;

	private Calendar searchStartDate = null;

	private Calendar searchEndDate = null;

	private JLabel statusLabel = null;

	private UserCertificateStatusComboBox status = null;

	private JPanel gridIdentityPanel = null;

	private JLabel gridIdentityLabel = null;

	private JTextField gridIdentity = null;

	private JButton findUser = null;

	private JPanel notePanel = null;

	private JLabel jLabel1 = null;

	private JTextField notes = null;

	private JPanel buttonPanel = null;

	private JButton search = null;

	private JScrollPane jScrollPane = null;

	private UserCertificatesTable userCertificates = null;

	private DorianSession session;

	private boolean allowGridIdentitySearch;

	private JButton clearButton = null;

	private JPanel actionPanel = null;

	private JButton viewCertificate = null;

	private JButton remove = null;

	private JButton removeAll = null;

	private ProgressPanel progess;

	/**
	 * This is the default constructor
	 */
	public UserCertificateSearchPanel(DorianSession session,
			boolean administratorMode) {
		this(session, null, administratorMode);
	}

	public UserCertificateSearchPanel(DorianSession session, String gridId,
			boolean administratorMode) {
		super();
		this.session = session;
		if (gridId == null) {
			allowGridIdentitySearch = true;
		} else {
			allowGridIdentitySearch = false;
		}
		initialize();
		if (gridId != null) {
			getGridIdentity().setText(gridId);
		}

		if (!administratorMode) {
			getViewCertificate().setVisible(false);
			getRemove().setVisible(false);
			getRemoveAll().setVisible(false);
		}
	}

	
	public DorianSession getSession() throws Exception {
		return this.session;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		gridBagConstraints14.gridx = 0;
		gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints14.weightx = 1.0D;
		gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints14.gridy = 2;
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.fill = GridBagConstraints.BOTH;
		gridBagConstraints12.weighty = 1.0;
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.gridy = 1;
		gridBagConstraints12.weightx = 1.0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.gridy = 0;

		this.setLayout(new GridBagLayout());
		this.add(getSearchCriteriaPanel(), gridBagConstraints);
		this.add(getJScrollPane(), gridBagConstraints12);
		this.add(getActionPanel(), gridBagConstraints14);
	}

	/**
	 * This method initializes searchCriteriaPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSearchCriteriaPanel() {
		if (searchCriteriaPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.weightx = 1.0D;
			gridBagConstraints10.gridy = 4;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.gridx = 0;
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints30.gridx = 0;
			gridBagConstraints30.gridy = 1;
			gridBagConstraints30.weightx = 1.0D;
			gridBagConstraints30.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.gridy = 2;
			gridBagConstraints17.weightx = 1.0D;
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			searchCriteriaPanel = new JPanel();
			searchCriteriaPanel.setLayout(new GridBagLayout());
			searchCriteriaPanel.add(getOtherCriteria(), gridBagConstraints30);
			if (allowGridIdentitySearch) {
				searchCriteriaPanel.add(getGridIdentityPanel(),
						gridBagConstraints3);
			}
			searchCriteriaPanel.add(getButtonPanel(), gridBagConstraints10);
			searchCriteriaPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Search Criteria",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			searchCriteriaPanel.add(getDatePanel(), gridBagConstraints17);
			searchCriteriaPanel.add(getNotePanel(), gridBagConstraints7);
		}
		return searchCriteriaPanel;
	}

	/**
	 * This method initializes datePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDatePanel() {
		if (datePanel == null) {
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.anchor = GridBagConstraints.WEST;
			gridBagConstraints29.gridx = 5;
			gridBagConstraints29.gridy = 0;
			gridBagConstraints29.weighty = 0.0D;
			gridBagConstraints29.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.anchor = GridBagConstraints.WEST;
			gridBagConstraints27.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints27.gridx = 4;
			gridBagConstraints27.gridy = 0;
			gridBagConstraints27.weightx = 1.0;
			gridBagConstraints27.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.anchor = GridBagConstraints.WEST;
			gridBagConstraints24.gridx = 2;
			gridBagConstraints24.gridy = 0;
			gridBagConstraints24.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.anchor = GridBagConstraints.WEST;
			gridBagConstraints26.gridx = 3;
			gridBagConstraints26.gridy = 0;
			gridBagConstraints26.insets = new Insets(2, 2, 2, 2);
			jLabel3 = new JLabel();
			jLabel3.setText("End Date");
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.gridx = 1;
			gridBagConstraints23.gridy = 0;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.anchor = GridBagConstraints.WEST;
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.gridy = 0;
			gridBagConstraints25.insets = new Insets(2, 2, 2, 2);
			jLabel2 = new JLabel();
			jLabel2.setText("Start Date");
			datePanel = new JPanel();
			datePanel.setLayout(new GridBagLayout());
			datePanel.add(jLabel2, gridBagConstraints25);
			datePanel.add(getStartDate(), gridBagConstraints23);
			datePanel.add(jLabel3, gridBagConstraints26);
			datePanel.add(getStartDateSelector(), gridBagConstraints24);
			datePanel.add(getEndDate(), gridBagConstraints27);
			datePanel.add(getEndDateSelector(), gridBagConstraints29);
		}
		return datePanel;
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
	 * This method initializes startDateSelector
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStartDateSelector() {
		if (startDateSelector == null) {
			startDateSelector = new JButton();
			startDateSelector.setText("Select");
			startDateSelector
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							SelectDateDialog dialog = new SelectDateDialog(true);
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
		return startDateSelector;
	}

	/**
	 * This method initializes endDate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getEndDate() {
		if (endDate == null) {
			endDate = new JTextField();
		}
		return endDate;
	}

	/**
	 * This method initializes endDateSelector
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getEndDateSelector() {
		if (endDateSelector == null) {
			endDateSelector = new JButton();
			endDateSelector.setText("Select");
			endDateSelector
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
		return endDateSelector;
	}

	/**
	 * This method initializes otherCriteria
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOtherCriteria() {
		if (otherCriteria == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.weightx = 0.0D;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 3;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.weightx = 1.0;
			statusLabel = new JLabel();
			statusLabel.setText("Status");
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints31.gridx = 1;
			gridBagConstraints31.gridy = 0;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.weightx = 1.0;
			jLabel4 = new JLabel();
			jLabel4.setText("Serial Number");
			otherCriteria = new JPanel();
			otherCriteria.setLayout(new GridBagLayout());
			otherCriteria.add(jLabel4, new GridBagConstraints());
			otherCriteria.add(getUserCertificateSerialNumber(),
					gridBagConstraints31);
			otherCriteria.add(statusLabel, gridBagConstraints2);
			otherCriteria.add(getStatus(), gridBagConstraints1);
		}
		return otherCriteria;
	}

	/**
	 * This method initializes userCertificateSerialNumber
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUserCertificateSerialNumber() {
		if (userCertificateSerialNumber == null) {
			userCertificateSerialNumber = new JTextField();
		}
		return userCertificateSerialNumber;
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
	 * This method initializes status
	 * 
	 * @return javax.swing.JComboBox
	 */
	private UserCertificateStatusComboBox getStatus() {
		if (status == null) {
			status = new UserCertificateStatusComboBox(true);
		}
		return status;
	}

	/**
	 * This method initializes gridIdentityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGridIdentityPanel() {
		if (gridIdentityPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.weightx = 1.0;
			gridIdentityLabel = new JLabel();
			gridIdentityLabel.setText("Grid Identity");
			gridIdentityPanel = new JPanel();
			gridIdentityPanel.setLayout(new GridBagLayout());
			gridIdentityPanel.add(gridIdentityLabel, gridBagConstraints5);
			gridIdentityPanel.add(getGridIdentity(), gridBagConstraints4);
			gridIdentityPanel.add(getFindUser(), gridBagConstraints6);
		}
		return gridIdentityPanel;
	}

	/**
	 * This method initializes gridIdentity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridIdentity() {
		if (gridIdentity == null) {
			gridIdentity = new JTextField();
		}
		return gridIdentity;
	}

	/**
	 * This method initializes findUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFindUser() {
		if (findUser == null) {
			findUser = new JButton();
			findUser.setText("Find");
			final UserCertificateSearchPanel thisPanel = this;
			findUser.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UserSearchDialog dialog = new UserSearchDialog(thisPanel);
					dialog.setModal(true);
					GridApplication.getContext().showDialog(dialog);
					if (dialog.getSelectedUser() != null) {
						getGridIdentity().setText(dialog.getSelectedUser());
					}
				}
			});
		}
		return findUser;
	}

	/**
	 * This method initializes notePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getNotePanel() {
		if (notePanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridx = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Notes");
			notePanel = new JPanel();
			notePanel.setLayout(new GridBagLayout());
			notePanel.add(jLabel1, gridBagConstraints9);
			notePanel.add(getNotes(), gridBagConstraints8);
		}
		return notePanel;
	}

	/**
	 * This method initializes notes
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNotes() {
		if (notes == null) {
			notes = new JTextField();
		}
		return notes;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridy = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getSearch(), gridBagConstraints11);
			buttonPanel.add(getClearButton(), gridBagConstraints13);
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
					disableButtons();
					Runner runner = new Runner() {
						public void execute() {
							findCertificates();
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

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getUserCertificates());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes userCertificates
	 * 
	 * @return javax.swing.JTable
	 */
	private UserCertificatesTable getUserCertificates() {
		if (userCertificates == null) {
			userCertificates = new UserCertificatesTable(this.session);
		}
		return userCertificates;
	}

	private void findCertificates() {
		try {
			showProgess("Searching...");
			getUserCertificates().clearTable();
			UserCertificateFilter f = new UserCertificateFilter();
			f.setGridIdentity(Utils.clean(getGridIdentity().getText()));
			if (Utils.clean(getUserCertificateSerialNumber().getText()) != null) {
				try {
					f
							.setSerialNumber(Long.valueOf(Utils
									.clean(getUserCertificateSerialNumber()
											.getText())));
				} catch (NumberFormatException e) {
					stopProgess("Error");
					enableButtons();
					ErrorDialog
							.showError("The serial number must be an integer.");
					return;
				}
			}
			if ((searchStartDate != null) && (searchEndDate != null)) {
				if (searchStartDate.after(searchEndDate)) {
					stopProgess("Error");
					enableButtons();
					ErrorDialog
							.showError("The start date cannot be after the end date.");
					return;
				} else {
					DateRange r = new DateRange();
					r.setStartDate(searchStartDate);
					r.setEndDate(searchEndDate);
					f.setDateRange(r);
				}
			} else if ((searchStartDate == null) && (searchEndDate != null)) {
				stopProgess("Error");
				enableButtons();
				ErrorDialog.showError("You must specify a start date!!!");
				return;
			} else if ((searchStartDate != null) && (searchEndDate == null)) {
				stopProgess("Error");
				enableButtons();
				ErrorDialog.showError("You must specify an end date!!!");
				return;
			}

			f.setNotes(Utils.clean(getNotes().getText()));
			if (getStatus().getSelectedUserStatus() != null) {
				f.setStatus(getStatus().getSelectedUserStatus());
			}
			GridAdministrationClient client = this.session.getAdminClient();
			List<UserCertificateRecord> records = client
					.findUserCertificateRecords(f);
			getUserCertificates().addUserCertificates(records);
			stopProgess(records.size() + " user certificate(s) found.");
		} catch (Exception e) {
			stopProgess("Error");
			ErrorDialog.showError(Utils.getExceptionMessage(e), e);
		}
		enableButtons();
	}

	/**
	 * This method initializes clearButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Clear");
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					clearSearchCriteria();
				}
			});
		}
		return clearButton;
	}

	private void clearSearchCriteria() {
		getGridIdentity().setText("");
		getUserCertificateSerialNumber().setText("");
		getStatus().setToAny();
		this.searchStartDate = null;
		this.searchEndDate = null;
		this.getStartDate().setText("");
		this.getEndDate().setText("");
		this.getNotes().setText("");
	}

	/**
	 * This method initializes actionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			actionPanel = new JPanel();
			actionPanel.setLayout(new FlowLayout());
			actionPanel.add(getViewCertificate(), null);
			actionPanel.add(getRemove(), null);
			actionPanel.add(getRemoveAll(), null);
		}
		return actionPanel;
	}

	/**
	 * This method initializes viewCertificate
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getViewCertificate() {
		if (viewCertificate == null) {
			viewCertificate = new JButton();
			viewCertificate.setText("View");
			viewCertificate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getUserCertificates().doubleClick();
						}
					});
		}
		return viewCertificate;
	}

	/**
	 * This method initializes remove
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemove() {
		if (remove == null) {
			remove = new JButton();
			remove.setText("Delete");
			remove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					disableButtons();
					Runner runner = new Runner() {
						public void execute() {
							removeCertificate();
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
		return remove;
	}

	/**
	 * This method initializes removeAll
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveAll() {
		if (removeAll == null) {
			removeAll = new JButton();
			removeAll.setText("Delete All");
			removeAll.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					disableButtons();
					Runner runner = new Runner() {
						public void execute() {
							removeAllCertificates();
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
		return removeAll;
	}

	private void removeCertificate() {
		try {

			int row = getUserCertificates().getSelectedRow();

			if ((row >= 0) && (row < getUserCertificates().getRowCount())) {
				UserCertificateRecord record = (UserCertificateRecord) getUserCertificates()
						.getValueAt(row, 0);

				int status = JOptionPane
						.showConfirmDialog(this,
								"Are you sure you want to remove the selected certificate?");
				if (status == JOptionPane.YES_OPTION) {
					showProgess("Removing certificate...");
					GridAdministrationClient client = session.getAdminClient();
					client.removeUserCertificate(record.getSerialNumber());
					getUserCertificates().removeRow(row);
					GridApplication
							.getContext()
							.showMessage(
									"The selected certificates was successfully removed!!!");
					stopProgess("Certificate successfully removed.");
				}
			} else {
				throw new Exception("Please select a certificate!!!");
			}
		} catch (Exception e) {
			ErrorDialog.showError(e);
			stopProgess("Error");
		}
		enableButtons();
	}

	private void removeAllCertificates() {
		try {

			int status = JOptionPane
					.showConfirmDialog(this,
							"Are you sure you want to remove all of the listed certificates?");
			if (status == JOptionPane.YES_OPTION) {
				GridAdministrationClient client = session.getAdminClient();
				int rowCount = getUserCertificates().getRowCount();
				for (int i = 0; i < rowCount; i++) {
					showProgess("Removing certificates...");
					UserCertificateRecord record = (UserCertificateRecord) getUserCertificates()
							.getValueAt(0, 0);
					client.removeUserCertificate(record.getSerialNumber());
					getUserCertificates().removeRow(0);
				}
				stopProgess(rowCount + " certificate(s) removed.");
				GridApplication.getContext().showMessage(
						"The listed certificates were successfully removed!!!");
			}
		} catch (Exception e) {
			stopProgess("Error");
			ErrorDialog.showError(e);
		}
		enableButtons();
	}

	private void disableButtons() {
		getClearButton().setEnabled(false);
		getRemove().setEnabled(false);
		getRemoveAll().setEnabled(false);
		getSearch().setEnabled(false);
	}

	private void enableButtons() {
		getClearButton().setEnabled(true);
		getRemove().setEnabled(true);
		getRemoveAll().setEnabled(true);
		getSearch().setEnabled(true);
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

	public UserCertificateRecord getSelectedCertificate() throws Exception {
		return getUserCertificates().getSelectedCertificate();
	}
}
