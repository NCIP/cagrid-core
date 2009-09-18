package org.cagrid.gaards.ui.common;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;

import com.greef.ui.calendar.JCalendar;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: SelectDateDialog.java,v 1.2 2008-11-20 15:29:42 langella Exp $
 */
public class SelectDateDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;

	private JPanel jPanel = null;

	private JCalendar calendar = null;

	private JPanel timePanel = null;

	private JComboBox hour = null;

	private JLabel jLabel = null;

	private JComboBox minute = null;

	private JComboBox hourType = null;

	private boolean startOfDay;

	private JPanel jPanel1 = null;

	private JPanel buttonPanel = null;

	private JButton done = null;

	private JButton cancel = null;

	/**
	 * This is the default constructor
	 */
	public SelectDateDialog(boolean startOfDay) {
		super(GridApplication.getContext().getApplication());
		this.startOfDay = startOfDay;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Select Date");
		setSize(250, 320);

	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridy = 1;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getTimePanel(), gridBagConstraints1);
			jPanel.add(getJPanel1(), gridBagConstraints2);
			jPanel.add(getButtonPanel(), gridBagConstraints3);
		}
		return jPanel;
	}

	/**
	 * This method initializes calendar
	 * 
	 * @return javax.swing.JTextField
	 */
	private JCalendar getCalendar() {
		if (calendar == null) {
			calendar = new JCalendar();
			calendar.setTooltipDatePattern("MM/dd/yyyy");
			calendar.setShowVerticalLines(true);
			calendar.setShowHorizontalLines(true);
			calendar.setControlsPosition(JCalendar.TOP);
			calendar.setShowCellTooltips(true);
			calendar.setTodayDate();
		}
		return calendar;
	}

	public Calendar getDate() {
		Date d = getCalendar().getSelectedDate();
		if (d != null) {
			Calendar c = new GregorianCalendar();
			c.setTime(d);
			if (((String) getHourType().getSelectedItem()).equals("AM")) {
				c.set(Calendar.AM_PM, Calendar.AM);
			} else {
				c.set(Calendar.AM_PM, Calendar.PM);
			}
			int h = Integer.valueOf((String) getHour().getSelectedItem())
					.intValue();
			if (h == 12) {
				h = 0;
			}
			c.set(Calendar.HOUR, h);
			c.set(Calendar.MINUTE, Integer.valueOf(
					(String) getMinute().getSelectedItem()).intValue());
			return c;
		}
		return null;

	}

	/**
	 * This method initializes timePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTimePanel() {
		if (timePanel == null) {
			jLabel = new JLabel();
			jLabel.setText(":");
			jLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			timePanel = new JPanel();
			timePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Select Time", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			timePanel.setLayout(new FlowLayout());
			timePanel.add(getHour(), null);
			timePanel.add(jLabel, null);
			timePanel.add(getMinute(), null);
			timePanel.add(getHourType(), null);
		}
		return timePanel;
	}

	/**
	 * This method initializes hour
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getHour() {
		if (hour == null) {
			hour = new JComboBox();
			hour.addItem("12");
			hour.addItem("01");
			hour.addItem("02");
			hour.addItem("03");
			hour.addItem("04");
			hour.addItem("05");
			hour.addItem("06");
			hour.addItem("07");
			hour.addItem("08");
			hour.addItem("09");
			hour.addItem("10");
			hour.addItem("11");
			;
			if (startOfDay) {
				hour.setSelectedItem("12");
			} else {
				hour.setSelectedItem("11");
			}

		}
		return hour;
	}

	/**
	 * This method initializes minute
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getMinute() {
		if (minute == null) {
			minute = new JComboBox();
			for (int i = 0; i < 60; i++) {
				if (i < 10) {
					minute.addItem("0" + i);
				} else {
					minute.addItem(String.valueOf(i));
				}
			}

			if (startOfDay) {
				minute.setSelectedItem("00");
			} else {
				minute.setSelectedItem("59");
			}
		}
		return minute;
	}

	/**
	 * This method initializes hourType
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getHourType() {
		if (hourType == null) {
			hourType = new JComboBox();
			hourType.addItem("AM");
			hourType.addItem("PM");

			if (startOfDay) {
				hourType.setSelectedItem("AM");
			} else {
				hourType.setSelectedItem("PM");
			}
		}
		return hourType;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.setBorder(BorderFactory.createTitledBorder(null,
					"Select Date", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			jPanel1.add(getCalendar(), gridBagConstraints);
		}
		return jPanel1;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getDone(), gridBagConstraints5);
			buttonPanel.add(getCancel(), gridBagConstraints4);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes done
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDone() {
		if (done == null) {
			done = new JButton();
			done.setText("Select");
			done.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getDate() == null) {
						GridApplication.getContext().showMessage(
								"No date selected.");
					} else {
						dispose();
					}
				}
			});
		}
		return done;
	}

	/**
	 * This method initializes cancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancel() {
		if (cancel == null) {
			cancel = new JButton();
			cancel.setText("Cancel");
			cancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancel;
	}
}
