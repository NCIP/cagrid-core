package gov.nih.nci.cagrid.data.ui.auditors;

import gov.nih.nci.cagrid.data.auditing.MonitoredEvents;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/** 
 *  MonitoredEventsPanel
 *  Panel to configure / display which events an auditor will respond to
 * 
 * @author David Ervin
 * 
 * @created May 21, 2007 10:50:26 AM
 * @version $Id: MonitoredEventsPanel.java,v 1.4 2009-05-28 19:27:06 dervin Exp $ 
 */
public class MonitoredEventsPanel extends JPanel {

    private JCheckBox queryBeginsCheckBox = null;
    private JCheckBox validationFailureCheckBox = null;
    private JCheckBox queryProcessingFailureCheckBox = null;
    private JCheckBox queryResultsCheckBox = null;

    private List<MonitoredEventsChangeListener> changeListeners = null;
    private boolean changeNotificationEnabled = true;
    
    private ItemListener eventSelectionListener = null;

    public MonitoredEventsPanel() {
        changeListeners = new LinkedList<MonitoredEventsChangeListener>();
        changeNotificationEnabled = true;
        initialize();
    }
    
    
    public void addMonitoredEventsChangeListener(MonitoredEventsChangeListener listener) {
        changeListeners.add(listener);
    }
    
    
    public boolean removeMonitoredEventsChangeListener(MonitoredEventsChangeListener listener) {
        return changeListeners.remove(listener);
    }
    
    
    public MonitoredEventsChangeListener[] getMonitoredEventsChangeListeners() {
        MonitoredEventsChangeListener[] listeners = 
            new MonitoredEventsChangeListener[changeListeners.size()];
        changeListeners.toArray(listeners);
        return listeners;
    }
    
    
    private void initialize() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(2);
        gridLayout.setHgap(2);
        gridLayout.setVgap(2);
        gridLayout.setColumns(2);
        this.setLayout(gridLayout);
        this.add(getQueryBeginsCheckBox(), null);
        this.add(getValidationFailureCheckBox(), null);
        this.add(getQueryProcessingFailureCheckBox(), null);
        this.add(getQueryResultsCheckBox(), null);
    }
    
    
    public synchronized void setMonitoredEvents(MonitoredEvents events) {
        changeNotificationEnabled = false;
        getQueryBeginsCheckBox().setSelected(events.isQueryBegin());
        getValidationFailureCheckBox().setSelected(events.isValidationFailure());
        getQueryProcessingFailureCheckBox().setSelected(events.isQueryProcessingFailure());
        getQueryResultsCheckBox().setSelected(events.isQueryResults());
        changeNotificationEnabled = true;
    }
    
    
    public MonitoredEvents getMonitoredEvents() {
        MonitoredEvents events = new MonitoredEvents();
        events.setQueryBegin(getQueryBeginsCheckBox().isSelected());
        events.setValidationFailure(getValidationFailureCheckBox().isSelected());
        events.setQueryProcessingFailure(getQueryProcessingFailureCheckBox().isSelected());
        events.setQueryResults(getQueryResultsCheckBox().isSelected());
        return events;
    }


    /**
     * This method initializes queryBeginsCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getQueryBeginsCheckBox() {
        if (queryBeginsCheckBox == null) {
            queryBeginsCheckBox = new JCheckBox();
            queryBeginsCheckBox.setText("Query Begins");
            queryBeginsCheckBox.addItemListener(getEventSelectionListener());
        }
        return queryBeginsCheckBox;
    }


    /**
     * This method initializes validationFailureCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getValidationFailureCheckBox() {
        if (validationFailureCheckBox == null) {
            validationFailureCheckBox = new JCheckBox();
            validationFailureCheckBox.setText("Validation Failure");
            validationFailureCheckBox.addItemListener(getEventSelectionListener());
        }
        return validationFailureCheckBox;
    }


    /**
     * This method initializes queryProcessingFailureCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getQueryProcessingFailureCheckBox() {
        if (queryProcessingFailureCheckBox == null) {
            queryProcessingFailureCheckBox = new JCheckBox();
            queryProcessingFailureCheckBox.setText("Query Processing Failure");
            queryProcessingFailureCheckBox.addItemListener(getEventSelectionListener());
        }
        return queryProcessingFailureCheckBox;
    }


    /**
     * This method initializes queryResultsCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getQueryResultsCheckBox() {
        if (queryResultsCheckBox == null) {
            queryResultsCheckBox = new JCheckBox();
            queryResultsCheckBox.setText("Query Results");
            queryResultsCheckBox.addItemListener(getEventSelectionListener());
        }
        return queryResultsCheckBox;
    }
    
    
    private ItemListener getEventSelectionListener() {
        if (eventSelectionListener == null) {
            eventSelectionListener = new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    fireMonitoredEventsChanged();
                }
            };
        }
        return eventSelectionListener;
    }
    
    
    protected void fireMonitoredEventsChanged() {
        if (changeNotificationEnabled) {
            for (MonitoredEventsChangeListener listener : changeListeners) {
                listener.monitoredEventsChanged();
            }
        }
    }
}
