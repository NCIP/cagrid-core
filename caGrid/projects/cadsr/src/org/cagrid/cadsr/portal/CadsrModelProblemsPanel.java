package org.cagrid.cadsr.portal;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.cagrid.cadsr.util.ModelProblem;
import org.cagrid.cadsr.util.ModelProblemException;
import org.cagrid.cadsr.util.ModelProblemsUtil;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * CadsrModelProblemsPanel
 * 
 * @author David Ervin
 * @created Feb 8, 2008 1:42:07 PM
 * @version $Id: CadsrModelProblemsPanel.java,v 1.1 2008/02/11 16:23:20 dervin
 *          Exp $
 */
public class CadsrModelProblemsPanel extends JPanel {

    private JLabel cadsrApplicationUrlLabel = null;
    private JTextField cadsrApplicationUrlTextField = null;
    private JButton reloadButton = null;
    private JLabel projectsLabel = null;
    private JComboBox projectsComboBox = null;
    private JButton validateProjectButton = null;
    private JTextArea validationResultsTextArea = null;
    private JScrollPane validationResultsScrollPane = null;

    private ModelProblemsUtil modelUtil = null;


    public CadsrModelProblemsPanel(String cadsrApplicationUrl) {
        super();
        initialize();
        getCadsrApplicationUrlTextField().setText(cadsrApplicationUrl);
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = GridBagConstraints.BOTH;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.weighty = 1.0;
        gridBagConstraints6.gridwidth = 3;
        gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints6.gridx = 0;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 2;
        gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints5.gridy = 1;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints4.gridx = 1;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridy = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.gridy = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints1.gridx = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(385, 174));
        this.add(getCadsrApplicationUrlLabel(), gridBagConstraints);
        this.add(getCadsrApplicationUrlTextField(), gridBagConstraints1);
        this.add(getReloadButton(), gridBagConstraints2);
        this.add(getProjectsLabel(), gridBagConstraints3);
        this.add(getProjectsComboBox(), gridBagConstraints4);
        this.add(getValidateProjectButton(), gridBagConstraints5);
        this.add(getValidationResultsScrollPane(), gridBagConstraints6);
    }


    /**
     * This method initializes cadsrApplicationUrlLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getCadsrApplicationUrlLabel() {
        if (cadsrApplicationUrlLabel == null) {
            cadsrApplicationUrlLabel = new JLabel();
            cadsrApplicationUrlLabel.setText("caDSR Application URL:");
        }
        return cadsrApplicationUrlLabel;
    }


    /**
     * This method initializes cadsrApplicationUrlTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCadsrApplicationUrlTextField() {
        if (cadsrApplicationUrlTextField == null) {
            cadsrApplicationUrlTextField = new JTextField();
            cadsrApplicationUrlTextField.setEditable(false);
            // non-editable because the application service is a singleton
            // in caCORE 3.1, so changing the URL once it's been set
            // is irrelevant
        }
        return cadsrApplicationUrlTextField;
    }


    /**
     * This method initializes reloadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReloadButton() {
        if (reloadButton == null) {
            reloadButton = new JButton();
            reloadButton.setText("Reload");
            reloadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        reloadProjects();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error reloading projects from caDSR application", 
                            ex.getMessage(), ex);
                    }
                }
            });
        }
        return reloadButton;
    }


    /**
     * This method initializes projectsLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getProjectsLabel() {
        if (projectsLabel == null) {
            projectsLabel = new JLabel();
            projectsLabel.setText("Projects:");
        }
        return projectsLabel;
    }


    /**
     * This method initializes projectsComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProjectsComboBox() {
        if (projectsComboBox == null) {
            projectsComboBox = new JComboBox();
            projectsComboBox.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                    Object renderValue = value;
                    if (value instanceof Project) {
                        Project p = (Project) value;
                        renderValue = projectAsString(p);
                    }
                    // let the base class get highlights and fonts right
                    return super.getListCellRendererComponent(list, renderValue, index, isSelected, cellHasFocus);
                }
            });
        }
        return projectsComboBox;
    }


    /**
     * This method initializes validateProjectButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getValidateProjectButton() {
        if (validateProjectButton == null) {
            validateProjectButton = new JButton();
            validateProjectButton.setText("Validate");
            validateProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Project p = getSelectedProject();
                    if (p != null) {
                        try {
                            validateProject(p);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error validating project:", e1.getMessage(), e1);

                        }
                    }
                }
            });
        }
        return validateProjectButton;
    }


    /**
     * This method initializes validationResultsTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getValidationResultsTextArea() {
        if (validationResultsTextArea == null) {
            validationResultsTextArea = new JTextArea();
            validationResultsTextArea.setWrapStyleWord(true);
            validationResultsTextArea.setLineWrap(false);
        }
        return validationResultsTextArea;
    }


    /**
     * This method initializes validationResultsScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getValidationResultsScrollPane() {
        if (validationResultsScrollPane == null) {
            validationResultsScrollPane = new JScrollPane();
            validationResultsScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Validation Results",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            validationResultsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            validationResultsScrollPane.setViewportView(getValidationResultsTextArea());
        }
        return validationResultsScrollPane;
    }


    private void validateProject(Project proj) throws Exception {
        List<ModelProblem> problems = null;
        try {
            problems = getModelUtil().findProblems(proj.getShortName(), proj.getVersion());
        } catch (ModelProblemException ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error resolving problems in model", ex.getMessage(), ex);
        }

        StringBuffer messages = new StringBuffer();
        if (problems.size() == 0) {
            messages.append("No problems found in model ").append(projectAsString(proj)).append("\n");
        } else {
            messages.append("Model ").append(projectAsString(proj)).append(" contains errors:\n");
            for (ModelProblem problem : problems) {
                problem.writeToBuffer(messages);
                messages.append("\n");
            }
        }

        getValidationResultsTextArea().setText(messages.toString());
    }


    private ModelProblemsUtil getModelUtil() throws Exception {
        if (modelUtil == null) {
            ApplicationService appservice = ApplicationServiceProvider
                .getApplicationServiceFromUrl(getCadsrApplicationUrl());
            modelUtil = new ModelProblemsUtil(appservice);
        }
        return modelUtil;
    }


    private String projectAsString(Project p) {
        return new String(p.getShortName() + " : " + p.getVersion());
    }


    public String getCadsrApplicationUrl() {
        return getCadsrApplicationUrlTextField().getText();
    }


    public void reloadProjects() throws Exception {
        getProjectsComboBox().removeAllItems();
        ApplicationService appservice = ApplicationServiceProvider
            .getApplicationServiceFromUrl(getCadsrApplicationUrl());
        Project proj = new Project();
        List projects = appservice.search(Project.class, proj);
        Comparator projectSorter = new Comparator() {
            public int compare(Object o1, Object o2) {
                String val1 = null;
                String val2 = null;
                if (o1 instanceof Project) {
                    val1 = projectAsString((Project) o1).toLowerCase();
                } else {
                    val1 = o1.toString();
                }
                if (o2 instanceof Project) {
                    val2 = projectAsString((Project) o2).toLowerCase();
                } else {
                    val2 = o2.toString();
                }
                return val1.compareTo(val2);
            }
        };
        Collections.sort(projects, projectSorter);
        for (Object p : projects) {
            getProjectsComboBox().addItem(p);
        }
    }


    public Project getSelectedProject() {
        return (Project) getProjectsComboBox().getSelectedItem();
    }


    public String getModelProblems() {
        return getValidationResultsTextArea().getText();
    }
}
