package gov.nih.nci.cagrid.validator.builder;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.tests.core.beans.validation.Interval;
import gov.nih.nci.cagrid.tests.core.beans.validation.Schedule;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.AbstractSpinnerModel;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.apache.axis.types.Time;


/**
 * SchedulePanel 
 * Panel to handle rendering / editing of a validation schedule
 * 
 * @author David Ervin
 * 
 * @created Aug 28, 2007 12:57:46 PM
 * @version $Id: SchedulePanel.java,v 1.1 2008-03-25 14:20:30 dervin Exp $
 */
public class SchedulePanel extends JPanel {

    private JLabel taskNameLabel = null;
    private JTextField taskNameTextField = null;
    private JLabel startTimeLabel = null;
    private JPanel startSpinnerPanel = null;
    private JSpinner startHrSpinner = null;
    private JSpinner startMinSpinner = null;
    private JSpinner startAPSpinner = null;
    private JLabel hLabel = null;
    private JLabel mLabel = null;
    private JLabel apLabel = null;
    private JPanel taskPanel = null;
    private JLabel hoursLabel = null;
    private JLabel minutesLabel = null;
    private JLabel secondsLabel = null;
    private JSpinner hoursSpinner = null;
    private JSpinner minutesSpinner = null;
    private JSpinner secondsSpinner = null;
    private JPanel intervalPanel = null;
    private JCheckBox startImmediatelyCheckBox = null;


    public SchedulePanel() {
        super();
        initialize();
    }
    
    
    public void setSchedule(Schedule schedule) {
        getTaskNameTextField().setText(schedule.getTaskName());
        if (schedule.getStart() != null) {
            getStartImmediatelyCheckBox().setSelected(false);
            Calendar start = schedule.getStart().getAsCalendar();
            boolean startAM = start.get(Calendar.AM_PM) == Calendar.AM;
            int startHour = start.get(Calendar.HOUR) + 1;
            int startMin = start.get(Calendar.MINUTE);
            getStartHrSpinner().setValue(Integer.valueOf(startHour));
            getStartMinSpinner().setValue(Integer.valueOf(startMin));
            getStartAPSpinner().setValue(startAM ? APSpinModel.AM : APSpinModel.PM);
        } else {
            getStartImmediatelyCheckBox().setSelected(true);
        }
        Interval interval = schedule.getInterval();
        getHoursSpinner().setValue(Integer.valueOf(interval.getHours()));
        getMinutesSpinner().setValue(Integer.valueOf(interval.getMinutes()));
        getSecondsSpinner().setValue(Integer.valueOf(interval.getSeconds()));
    }
    
    
    public Schedule getSchedule() {
        Schedule schedule = new Schedule();
        schedule.setTaskName(getTaskNameTextField().getText());
        if (!getStartImmediatelyCheckBox().isSelected()) {
            Calendar start = new GregorianCalendar();
            boolean startAM = getStartAPSpinner().getValue().equals(APSpinModel.AM);
            start.set(Calendar.AM_PM, startAM ? Calendar.AM : Calendar.PM);
            // hours from 0 to 11
            start.set(Calendar.HOUR, Integer.parseInt(getStartHrSpinner().getValue().toString()) - 1);
            start.set(Calendar.MINUTE, Integer.parseInt(getMinutesSpinner().getValue().toString()));
            schedule.setStart(new Time(start));
        }
        Interval interval = new Interval();
        interval.setHours(Integer.parseInt(getHoursSpinner().getValue().toString()));
        interval.setMinutes(Integer.parseInt(getMinutesSpinner().getValue().toString()));
        interval.setSeconds(Integer.parseInt(getSecondsSpinner().getValue().toString()));
        schedule.setInterval(interval);
        return schedule;
    }


    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
        gridBagConstraints17.gridx = 0;
        gridBagConstraints17.weightx = 1.0D;
        gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints17.gridy = 1;
        GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
        gridBagConstraints16.gridx = 0;
        gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints16.weightx = 1.0D;
        gridBagConstraints16.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(472, 168));
        this.add(getTaskPanel(), gridBagConstraints16);
        this.add(getIntervalPanel(), gridBagConstraints17);
    }


    /**
     * This method initializes taskNameLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getTaskNameLabel() {
        if (taskNameLabel == null) {
            taskNameLabel = new JLabel();
            taskNameLabel.setText("Task Name:");
        }
        return taskNameLabel;
    }


    /**
     * This method initializes taskNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTaskNameTextField() {
        if (taskNameTextField == null) {
            taskNameTextField = new JTextField();
        }
        return taskNameTextField;
    }


    /**
     * This method initializes startTimeLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getStartTimeLabel() {
        if (startTimeLabel == null) {
            startTimeLabel = new JLabel();
            startTimeLabel.setText("Start Time:");
        }
        return startTimeLabel;
    }


    /**
     * This method initializes startSpinnerPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStartSpinnerPanel() {
        if (startSpinnerPanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 5;
            gridBagConstraints8.gridy = 0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 4;
            gridBagConstraints7.gridy = 0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 3;
            gridBagConstraints6.gridy = 0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 2;
            gridBagConstraints5.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.gridy = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            apLabel = new JLabel();
            apLabel.setText("AM/PM");
            mLabel = new JLabel();
            mLabel.setText("M:");
            hLabel = new JLabel();
            hLabel.setText("H:");
            startSpinnerPanel = new JPanel(new GridBagLayout());
            startSpinnerPanel.add(hLabel, gridBagConstraints3);
            startSpinnerPanel.add(getStartHrSpinner(), gridBagConstraints4);
            startSpinnerPanel.add(mLabel, gridBagConstraints5);
            startSpinnerPanel.add(getStartMinSpinner(), gridBagConstraints6);
            startSpinnerPanel.add(apLabel, gridBagConstraints7);
            startSpinnerPanel.add(getStartAPSpinner(), gridBagConstraints8);
        }
        return startSpinnerPanel;
    }


    private JSpinner getStartHrSpinner() {
        if (startHrSpinner == null) {
            SpinnerNumberModel hourModel = new SpinnerNumberModel(1, 0, 12, 1);
            startHrSpinner = new JSpinner(hourModel);
        }
        return startHrSpinner;
    }


    private JSpinner getStartMinSpinner() {
        if (startMinSpinner == null) {
            SpinnerNumberModel minModel = new SpinnerNumberModel(0, 0, 59, 1);
            startMinSpinner = new JSpinner(minModel);
        }
        return startMinSpinner;
    }


    private JSpinner getStartAPSpinner() {
        if (startAPSpinner == null) {
            startAPSpinner = new JSpinner(new APSpinModel());
        }
        return startAPSpinner;
    }


    /**
     * This method initializes taskPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTaskPanel() {
        if (taskPanel == null) {
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 1;
            gridBagConstraints18.gridy = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 2;
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            taskPanel = new JPanel();
            taskPanel.setLayout(new GridBagLayout());
            taskPanel.setBorder(BorderFactory.createTitledBorder(null, "Task", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                null, PortalLookAndFeel.getPanelLabelColor()));
            taskPanel.add(getTaskNameLabel(), gridBagConstraints);
            taskPanel.add(getTaskNameTextField(), gridBagConstraints1);
            taskPanel.add(getStartTimeLabel(), gridBagConstraints2);
            taskPanel.add(getStartSpinnerPanel(), gridBagConstraints11);
            taskPanel.add(getStartImmediatelyCheckBox(), gridBagConstraints18);
        }
        return taskPanel;
    }


    /**
     * This method initializes hoursLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getHoursLabel() {
        if (hoursLabel == null) {
            hoursLabel = new JLabel();
            hoursLabel.setText("Hours:");
        }
        return hoursLabel;
    }


    /**
     * This method initializes minutesLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getMinutesLabel() {
        if (minutesLabel == null) {
            minutesLabel = new JLabel();
            minutesLabel.setText("Minutes:");
        }
        return minutesLabel;
    }


    /**
     * This method initializes secondsLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getSecondsLabel() {
        if (secondsLabel == null) {
            secondsLabel = new JLabel();
            secondsLabel.setText("Seconds");
        }
        return secondsLabel;
    }


    private JSpinner getHoursSpinner() {
        if (hoursSpinner == null) {
            // any number of hours, up to Integer.MAX_VALUE
            SpinnerNumberModel hourModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
            hoursSpinner = new JSpinner(hourModel);
            // hoursSpinner.setPreferredSize(getMinutesSpinner().getPreferredSize());
        }
        return hoursSpinner;
    }


    private JSpinner getMinutesSpinner() {
        if (minutesSpinner == null) {
            SpinnerNumberModel minModel = new SpinnerNumberModel(0, 0, 59, 1);
            minutesSpinner = new JSpinner(minModel);
        }
        return minutesSpinner;
    }


    private JSpinner getSecondsSpinner() {
        if (secondsSpinner == null) {
            SpinnerNumberModel secModel = new SpinnerNumberModel(0, 0, 59, 1);
            secondsSpinner = new JSpinner(secModel);
        }
        return secondsSpinner;
    }


    /**
     * This method initializes intervalPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getIntervalPanel() {
        if (intervalPanel == null) {
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 5;
            gridBagConstraints15.gridy = 0;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 3;
            gridBagConstraints14.gridy = 0;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 4;
            gridBagConstraints13.gridy = 0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 2;
            gridBagConstraints12.gridy = 0;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 1;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 0;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 0;
            intervalPanel = new JPanel();
            intervalPanel.setLayout(new GridBagLayout());
            intervalPanel.setBorder(BorderFactory.createTitledBorder(null, "Interval",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                null, PortalLookAndFeel.getPanelLabelColor()));
            intervalPanel.add(getHoursLabel(), gridBagConstraints9);
            intervalPanel.add(getHoursSpinner(), gridBagConstraints10);
            intervalPanel.add(getMinutesLabel(), gridBagConstraints12);
            intervalPanel.add(getSecondsLabel(), gridBagConstraints13);
            intervalPanel.add(getSecondsSpinner(), gridBagConstraints14);
            intervalPanel.add(getMinutesSpinner(), gridBagConstraints15);
        }
        return intervalPanel;
    }


    /**
     * This method initializes startImmediatlyCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getStartImmediatelyCheckBox() {
        if (startImmediatelyCheckBox == null) {
            startImmediatelyCheckBox = new JCheckBox();
            startImmediatelyCheckBox.setText("Start Immediately");
            startImmediatelyCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    PortalUtils.setContainerEnabled(
                        getStartSpinnerPanel(), !getStartImmediatelyCheckBox().isSelected());
                }
            });
            startImmediatelyCheckBox.setSelected(true);
        }
        return startImmediatelyCheckBox;
    }
    
    
    private static class APSpinModel extends AbstractSpinnerModel {
        public static final String AM = "AM";
        public static final String PM = "PM";

        private String current;


        public APSpinModel() {
            super();
            current = AM;
        }


        public Object getNextValue() {
            current = current.equals(AM) ? PM : AM;
            fireStateChanged();
            return current;
        }


        public Object getPreviousValue() {
            return getNextValue();
        }


        public Object getValue() {
            return current;
        }


        public void setValue(Object value) {
            if (AM.equals(value)) {
                current = AM;
            } else if (PM.equals(value)) {
                current = PM;
            } else {
                throw new IllegalArgumentException(String.valueOf(value) + " is not a valid value");
            }
        }
    }
}
