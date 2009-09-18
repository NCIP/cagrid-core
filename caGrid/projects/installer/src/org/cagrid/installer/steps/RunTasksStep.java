/**
 * 
 */
package org.cagrid.installer.steps;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.tasks.Task;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class RunTasksStep extends PanelWizardStep implements PropertyChangeListener {

    private CaGridInstallerModel model; // @jve:decl-index=0:

    private JLabel busyLabel;

    private JPanel descriptionPanel;

    private JPanel busyPanel;

    private JProgressBar busyProgressBar;

    private List<Task> tasks = new ArrayList<Task>();

    private Exception exception;

    private static final Log logger = LogFactory.getLog(RunTasksStep.class); // @jve:decl-index=0:

    private static final int PROGRESS_SCALE = 100;

    private PrintStream out;

    private boolean executed = false;


    /**
     * 
     */
    public RunTasksStep() {
        initialize();
    }


    /**
     * @param arg0
     * @param arg1
     */
    public RunTasksStep(String name, String description) {
        super(name, description);
        initialize();
    }


    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public RunTasksStep(String name, String description, Icon icon) {
        super(name, description, icon);
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.BOTH;
        gridBagConstraints3.gridy = 2;
        gridBagConstraints3.weightx = 1.0D;
        gridBagConstraints3.weighty = 1.0D;
        gridBagConstraints3.gridx = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.weighty = 0.0D;
        gridBagConstraints2.insets = new Insets(10, 2, 10, 2);
        gridBagConstraints2.gridx = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 0.0D;
        gridBagConstraints1.fill = GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints1.gridy = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.0D;
        gridBagConstraints.gridy = 1;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(263, 161));
        JTextArea taskOutput = new JTextArea(5, 20);
        // taskOutput.setAutoscrolls(true);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);
        this.add(getDescriptionPanel(), gridBagConstraints1);
        this.add(getBusyPanel(), gridBagConstraints2);
        this.add(new JScrollPane(taskOutput), gridBagConstraints3);
        this.out = new PrintStream(new TextAreaOutputStream(taskOutput));

    }


    public void init(WizardModel m) {
        if (!(m instanceof CaGridInstallerModel)) {
            throw new IllegalStateException("This step requires a StatefulWizardModel instance.");
        }
        this.model = (CaGridInstallerModel) m;

    }


    public void applyState() throws InvalidStateException {
        if (this.exception != null) {

            throw new InvalidStateException("This operation did not complete successfully: "
                + this.exception.getMessage(), this.exception);
        }
    }


    public void prepare() {
        if (!isExecuted()) {
            getBusyProgressBar().setMaximum(getTasksCount(model));
            System.setOut(this.out);
            System.setErr(this.out);

            Worker w = new Worker(RunTasksStep.this.getTasks(), RunTasksStep.this.model);
            w.addPropertyChangeListener(RunTasksStep.this);
            w.start();
        }

    }


    private JPanel getDescriptionPanel() {
        if (descriptionPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            busyLabel = new JLabel(" ");

            descriptionPanel = new JPanel();
            descriptionPanel.setLayout(new GridBagLayout());
            descriptionPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
            descriptionPanel.add(busyLabel, gridBagConstraints3);
        }
        return descriptionPanel;
    }


    public void setBusyLabel(String description) {
        this.busyLabel.setText(description);
    }


    /**
     * This method initializes busyPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBusyPanel() {
        if (busyPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 0;
            busyPanel = new JPanel();
            busyPanel.setLayout(new GridBagLayout());
            busyPanel.add(getBusyProgressBar(), gridBagConstraints4);
        }
        return busyPanel;
    }


    /**
     * This method initializes busyProgressBar
     * 
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getBusyProgressBar() {

        if (busyProgressBar == null) {
            busyProgressBar = new JProgressBar(0, 1);
            busyProgressBar.setStringPainted(true);
            busyProgressBar.setValue(0);
            busyProgressBar.setPreferredSize(new Dimension(148, 16));
        }
        return busyProgressBar;
    }


    public void propertyChange(PropertyChangeEvent evt) {

        if ("progress" == evt.getPropertyName()) {

            int progress = (Integer) evt.getNewValue();
            getBusyProgressBar().setValue(progress);
            if (progress == getTasksCount(model)) {
                getBusyProgressBar().setValue(getBusyProgressBar().getMaximum());
                setBusyLabel(this.model.getMessage("finished"));
                setSummary(this.model.getMessage("finished"));
                setComplete(true);
                setExecuted(true);
            }
        } else if ("currentTask" == evt.getPropertyName()) {
            Task currentTask = (Task) evt.getNewValue();
            setBusyLabel(model.getMessage("processing") + " "  + currentTask.getDescription());
        } else if ("exception" == evt.getPropertyName()) {
            this.exception = (Exception) evt.getNewValue();
            setSummary(this.model.getMessage("error"));
            setBusyLabel(this.model.getMessage("error"));
            String msg = this.exception.getMessage();
            logger.error(msg, this.exception);
            setComplete(false);
            JOptionPane.showMessageDialog(null, msg, this.model.getMessage("error"), JOptionPane.ERROR_MESSAGE);
        }
    }


    public List<Task> getTasks() {
        return tasks;
    }


    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }


    public int getTasksCount(CaGridInstallerModel model) {
        int count = 0;
        for (Task t : getTasks()) {
            if (t instanceof Condition) {
                if (((Condition) t).evaluate(model)) {
                    count++;
                }
            } else {
                count++;
            }
        }
        return count;
    }


    class Worker extends Thread {

        private PropertyChangeSupport psc;

        private Exception exception;

        private int progress;

        private Task currentTask;

        private CaGridInstallerModel model;

        private List<Task> tasks;


        Worker(List<Task> tasks, CaGridInstallerModel model) {
            this.tasks = tasks;
            this.psc = new PropertyChangeSupport(this);
            this.model = model;
            this.setDaemon(true);
        }


        public void run() {

            setProgress(0);
            setBusy(true);
            for (int i = 0; i < tasks.size(); i++) {
                Task task = this.tasks.get(i);
                boolean runTask = true;
                if (task instanceof Condition) {
                    runTask = ((Condition) task).evaluate(this.model);
                }
                if (runTask) {
                    logger.info("Running task " + task.getName());
                    setCurrentTask(task);
                    try {
                        task.execute(this.model);
                        setProgress(getProgress() + 1);
                    } catch (Exception ex) {
                        if (task.isAbortOnError()) {
                            setException(ex);
                            break;
                        }
                    }
                } else {
                    logger.info("Skipping task " + task.getName());
                }

            }

            setExecuted(true);
            setComplete(true);
            setBusy(false);
        }


        public void addPropertyChangeListener(PropertyChangeListener l) {
            this.psc.addPropertyChangeListener(l);
        }


        public Exception getException() {
            return exception;
        }


        public void setException(Exception exception) {
            Exception oldValue = this.exception;
            this.exception = exception;
            this.psc.firePropertyChange("exception", oldValue, this.exception);
        }


        public int getProgress() {
            return progress;
        }


        public void setProgress(int progress) {
            int oldValue = this.progress;
            this.progress = progress;
            this.psc.firePropertyChange("progress", oldValue, this.progress);
        }


        private void setCurrentTask(Task task) {
            Task oldValue = this.currentTask;
            this.currentTask = task;
            this.psc.firePropertyChange("currentTask", oldValue, this.currentTask);
        }

    }


    class TextAreaOutputStream extends OutputStream {

        private JTextArea textControl;

        private StringBuilder buf = new StringBuilder();


        public TextAreaOutputStream(JTextArea control) {
            textControl = control;
        }


        public void write(final int b) throws IOException {

            textControl.append(String.valueOf((char) b));
            int len = textControl.getDocument().getLength();
            textControl.setCaretPosition(len);

            if ('\n' == (char) b) {
                logger.info(buf.toString());
                buf = new StringBuilder();
            } else {
                buf.append((char) b);
            }
        }
    }


    public boolean isExecuted() {
        return executed;
    }


    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
