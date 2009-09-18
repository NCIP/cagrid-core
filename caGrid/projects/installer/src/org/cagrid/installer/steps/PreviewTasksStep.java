/**
 * 
 */
package org.cagrid.installer.steps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JTextArea;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.tasks.Task;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.models.Condition;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class PreviewTasksStep extends PanelWizardStep {
    
    private RunTasksStep step = null;
    private JTextArea previewSummary = null;
    private CaGridInstallerModel model = null;
    
    public PreviewTasksStep(String name, String description,RunTasksStep step, CaGridInstallerModel model) {
        super(name,description);
		initialize();
        this.step = step;
        this.model = model;
    }
    

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(314, 185));
        this.add(getPreviewSummary(), gridBagConstraints);
    		
    }

    @Override
    public void prepare() {
        super.prepare();
        
        //need to view the tasks and load a panel with a description of what is going
        //to be ran
        StringBuffer sb = new StringBuffer();
        sb.append(model.getMessage("run.tasks.preview.prepared") + "\n");
        for (Task t : step.getTasks()) {
            if (t instanceof Condition) {
                if (((Condition) t).evaluate(model)) {
                    sb.append("\t" + t.getDescription() + "\n");
                }
            } else {
                sb.append("\t" + t.getDescription() + "\n");
            }
        }
        
        getPreviewSummary().setText(sb.toString());
        setComplete(true);
    }

    /**
     * This method initializes previewSummary	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getPreviewSummary() {
        if (previewSummary == null) {
            previewSummary = new JTextArea();
        }
        return previewSummary;
    }
    
    

}  //  @jve:decl-index=0:visual-constraint="10,10"
