package gov.nih.nci.cagrid.data.ui.wizard;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.ApplicationContext;
import org.cagrid.grape.GridApplication;

/** 
 *  ServiceWizard
 *  A wizard to simplify creation of a grid service model, which
 *  will later be processed by the Introduce toolkit
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 25, 2006 
 * @version $Id: ServiceWizard.java,v 1.5 2009-01-29 21:37:12 dervin Exp $ 
 */
public class ServiceWizard extends JDialog {

	public static final String DONE_BUTTON_TEXT = "Done";
    
    public static final Dimension STANDARD_SIZE = new java.awt.Dimension(640, 325);
    public static final Dimension MAX_SIZE = new java.awt.Dimension(
        (int) Math.floor(STANDARD_SIZE.width * 1.5D),
        (int) Math.floor(STANDARD_SIZE.height * 1.5D));
	
	private List<AbstractWizardPanel> panelSequence;
	private int currentPanelIndex;
	private String baseTitle;
	private boolean wizardDone;
    private boolean alreadyShown;
	
	private Font stepFont = null;
	private JLabel stepLabel = null;
	private JLabel stepCurrentLabel = null;
	private JLabel stepOfLabel = null;
	private JLabel stepTotalLabel = null;
	private JPanel stepPanel = null;
	private JButton previousPanelButton = null;
	private JButton nextPanelButton = null;
	private JPanel buttonPanel = null;
	private JPanel controlPanel = null;
	private JPanel mainPanel = null;
	private JPanel wizardPanel = null;
	
	private ActionListener nextButtonListener = null;
	private ActionListener doneButtonListener = null;
	
	public ServiceWizard(Frame owner, String baseTitle) {
		super(owner);
		setModal(true);
		this.panelSequence = new ArrayList<AbstractWizardPanel>();
		this.currentPanelIndex = 0;
		this.baseTitle = baseTitle;
        this.alreadyShown = false;
		this.wizardDone = false;
		this.stepFont = new Font("Dialog", java.awt.Font.ITALIC, 10);
		initialize();
	}
	
	
	public void addWizardPanel(AbstractWizardPanel panel) {
		if (isVisible()) {
			throw new IllegalStateException("Cannot add panels while wizard is showing!");
		}
		panel.addButtonEnableListener(new ButtonEnableListener() {
			public void setNextEnabled(boolean enable) {
				// only allow changing this if we're NOT done with the wizard
				if (!wizardDone) {
					getNextPanelButton().setEnabled(enable);
				}
			}
			
			
			public void setPrevEnabled(boolean enable) {
				getPreviousPanelButton().setEnabled(enable);
			}
			
			
			public void setWizardDone(boolean done) {
				ServiceWizard.this.setWizardDone(done);
			}
		});
		panelSequence.add(panel);
	}
	
	
	public void insertWizardPanel(AbstractWizardPanel panel, int index) {
		if (isVisible()) {
			throw new IllegalStateException("Cannot add panels while showing wizard!");
		}
		panelSequence.add(index, panel);
	}
	
	
	public AbstractWizardPanel getPanelAt(int index) {
		return panelSequence.get(index);
	}
	
	
	public AbstractWizardPanel[] getWizardPanels() {
		AbstractWizardPanel[] arr = new AbstractWizardPanel[panelSequence.size()];
		panelSequence.toArray(arr);
		return arr;
	}
	
	
	public void setVisible(boolean visible) {
	    if (visible && !alreadyShown) {
	        // load panels into the UI
	        initPanelLayout();
	        // load the first panel
	        loadWizardPanel(panelSequence.get(0));
	        // adjust size of the wizard dialog
	        Dimension preferred = getPreferredSize();
	        int width = Math.min(MAX_SIZE.width, Math.max(STANDARD_SIZE.width, preferred.width));
	        int height = Math.min(MAX_SIZE.height, Math.max(STANDARD_SIZE.height, preferred.height));
            System.out.println("Setting wizard size to " + width + ", " + height);
	        setSize(width, height);
            ApplicationContext appContext = GridApplication.getContext();
            if (appContext != null) {
                appContext.centerDialog(this);
            }
            alreadyShown = true;
        }
        super.setVisible(visible);
	}
	
	
	private void setWizardDone(boolean done) {
		if (done && !wizardDone) {
			// change the text of the next button
			getNextPanelButton().setText(DONE_BUTTON_TEXT);
			getNextPanelButton().setEnabled(true);
			// remove 'next' action listener from next button
			getNextPanelButton().removeActionListener(getNextButtonActionListener());
			// add the 'done' action listener
			getNextPanelButton().addActionListener(getDoneButtonActionListener());
		} else if (!done && wizardDone) {
			getNextPanelButton().removeActionListener(getDoneButtonActionListener());
			getNextPanelButton().addActionListener(getNextButtonActionListener());
		}
		wizardDone = done;
	}
	
	
	private void initPanelLayout() {
		for (int i = 0; i < panelSequence.size(); i++) {
			getWizardPanel().add(panelSequence.get(i), String.valueOf(i));
		}
	}
	
	
	private void initialize() {
        // start with the standard wizard size.
        // This can grow up to MAXIMUM_SIZE based on what panels are added to it
        this.setSize(STANDARD_SIZE);
        this.setContentPane(getMainPanel());
        this.setTitle(baseTitle);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		String[] message = {
        			"Closing this wizard before completing all steps may",
        			"have unexpected concequences, such as errors editing",
        			"the service and incomplete or incorrect functionality.\n",
        			"Close wizard anyway?"
        		};
        		int choice = JOptionPane.showConfirmDialog(
        			ServiceWizard.this, message, "Question", JOptionPane.YES_NO_OPTION);
        		if (choice == JOptionPane.YES_OPTION) {
        			// ok then...
        			setVisible(false);
        			dispose();
        		}
        	}
        });
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getStepLabel() {
		if (stepLabel == null) {
			stepLabel = new JLabel();
			stepLabel.setText("Step:");
			stepLabel.setFont(stepFont);
		}
		return stepLabel;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getStepCurrentLabel() {
		if (stepCurrentLabel == null) {
			stepCurrentLabel = new JLabel();
			stepCurrentLabel.setText("x");
			stepCurrentLabel.setFont(stepFont);
		}
		return stepCurrentLabel;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getStepOfLabel() {
		if (stepOfLabel == null) {
			stepOfLabel = new JLabel();
			stepOfLabel.setText("of");
			stepOfLabel.setFont(stepFont);
		}
		return stepOfLabel;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getStepTotalLabel() {
		if (stepTotalLabel == null) {
			stepTotalLabel = new JLabel();
			stepTotalLabel.setText("y");
			stepTotalLabel.setFont(stepFont);
		}
		return stepTotalLabel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStepPanel() {
		if (stepPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 3;
			gridBagConstraints3.insets = new java.awt.Insets(2,0,2,2);
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.insets = new java.awt.Insets(2,0,2,2);
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new java.awt.Insets(2,0,2,2);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.gridy = 0;
			stepPanel = new JPanel();
			stepPanel.setLayout(new GridBagLayout());
			stepPanel.add(getStepLabel(), gridBagConstraints);
			stepPanel.add(getStepCurrentLabel(), gridBagConstraints1);
			stepPanel.add(getStepOfLabel(), gridBagConstraints2);
			stepPanel.add(getStepTotalLabel(), gridBagConstraints3);
		}
		return stepPanel;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPreviousPanelButton() {
		if (previousPanelButton == null) {
			previousPanelButton = new JButton();
			previousPanelButton.setText("Prev: ");
			previousPanelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (currentPanelIndex != 0) {
						// wizard is clearly not done if previous is hit...
						setWizardDone(false);
						currentPanelIndex--;
						AbstractWizardPanel prevPanel = panelSequence.get(currentPanelIndex);
						loadWizardPanel(prevPanel);
					}
				}
			});
		}
		return previousPanelButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNextPanelButton() {
		if (nextPanelButton == null) {
			nextPanelButton = new JButton();
			nextPanelButton.setText("Next: ");
			nextPanelButton.addActionListener(getNextButtonActionListener());
		}
		return nextPanelButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setHgap(4);
			gridLayout.setColumns(2);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(gridLayout);
			buttonPanel.add(getPreviousPanelButton(), null);
			buttonPanel.add(getNextPanelButton(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getControlPanel() {
		if (controlPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints5.ipadx = 160;
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.weightx = 1.0D;
			gridBagConstraints4.gridy = 0;
			controlPanel = new JPanel();
			controlPanel.setLayout(new GridBagLayout());
			controlPanel.add(getStepPanel(), gridBagConstraints4);
			controlPanel.add(getButtonPanel(), gridBagConstraints5);
		}
		return controlPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.weighty = 1.0D;
			gridBagConstraints6.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getWizardPanel(), gridBagConstraints6);
			mainPanel.add(getControlPanel(), gridBagConstraints7);
		}
		return mainPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getWizardPanel() {
		if (wizardPanel == null) {
			wizardPanel = new JPanel(new CardLayout());
			wizardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Panel Title", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
		}
		return wizardPanel;
	}
	
	
	private void loadWizardPanel(AbstractWizardPanel panel) {
        // set the current step
		getStepCurrentLabel().setText(String.valueOf(currentPanelIndex + 1));
		getStepTotalLabel().setText(String.valueOf(panelSequence.size()));
		
		// enable / disable previous and next buttons as needed
		getPreviousPanelButton().setEnabled(currentPanelIndex != 0);
		getNextPanelButton().setEnabled(currentPanelIndex != panelSequence.size() - 1);
		
		// set the text of the prev / next buttons
		if (currentPanelIndex < panelSequence.size() - 1) {
			AbstractWizardPanel nextPanel = panelSequence.get(currentPanelIndex + 1);
			getNextPanelButton().setText("Next: " + nextPanel.getPanelShortName());
		} else {
			getNextPanelButton().setText("Next: ");
		}
		if (currentPanelIndex != 0) {
			AbstractWizardPanel prevPanel = panelSequence.get(currentPanelIndex - 1);
			getPreviousPanelButton().setText("Prev: " + prevPanel.getPanelShortName());
		} else {
			getPreviousPanelButton().setText("Prev: ");
		}
		
		// set the border text for the wizard component
		TitledBorder border = (TitledBorder) getWizardPanel().getBorder();
		border.setTitle(panel.getPanelShortName());
		
		// set the title of the dialog
		setTitle(baseTitle + ": " + panel.getPanelTitle());
		
		// have the panel refresh itself
		panel.update();
		
		// load the panel into the wizard panel
		((CardLayout) getWizardPanel().getLayout())
			.show(getWizardPanel(), String.valueOf(currentPanelIndex));
	}
	
	
	private ActionListener getNextButtonActionListener() {
		if (nextButtonListener == null) {
			nextButtonListener = new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (currentPanelIndex != panelSequence.size() - 1) {
                        // tell the current panel to save its state
                        panelSequence.get(currentPanelIndex).movingNext();
                        currentPanelIndex++;
						AbstractWizardPanel nextPanel = panelSequence.get(currentPanelIndex);
						loadWizardPanel(nextPanel);
					}
				}
			};
		}
		return nextButtonListener;
	}
	
	
	private ActionListener getDoneButtonActionListener() {
		if (doneButtonListener == null) {
			doneButtonListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    // make the last panel save its state
                    panelSequence.get(currentPanelIndex).movingNext();
					setVisible(false);
					dispose();
				}
			};
		}
		return doneButtonListener;
	}
}
