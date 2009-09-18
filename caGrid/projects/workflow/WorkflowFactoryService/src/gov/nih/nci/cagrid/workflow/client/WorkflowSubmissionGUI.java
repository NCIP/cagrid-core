/**
 * 
 */
package gov.nih.nci.cagrid.workflow.client;

import gov.nih.nci.cagrid.workflow.context.client.WorkflowServiceImplClient;
import gov.nih.nci.cagrid.workflow.stubs.types.StartInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WMSInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WMSOutputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WSDLReferences;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowOutputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusEventType;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.grape.ApplicationComponent;

import javax.swing.JTextField;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Insets;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.xml.namespace.QName;

import org.cagrid.grape.utils.ErrorDialog;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.utils.AnyHelper;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @author madduri
 * 
 */
public class WorkflowSubmissionGUI extends ApplicationComponent {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null; // @jve:decl-index=0:visual-constraint="10,10"

	private JLabel jLabel = null;

	private JTextField bpelTextField = null;

	private JButton jButton = null;

	private JTextArea inputTextArea = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel2 = null;

	private JButton submitButton = null;

	private JLabel jLabel3 = null;

	private File bpelFile = null;

	private JButton startButton = null;

	private WorkflowFactoryServiceClient factoryClient = null;

	private JButton getStatusButton = null;

	private String workflowFactoryURL = "http://localhost:8080/wsrf/services/cagrid/WorkflowFactoryService";  //  @jve:decl-index=0:

	private WorkflowServiceImplClient wclient = null;

	private EndpointReferenceType epr = null;

	private JLabel jLabel4 = null;

	private JTextField nameTextField = null;

	private JButton partnerLinkButton = null;

	private JTextArea jTextArea = null;

	private JLabel jLabel5 = null;
	
	private String status = "Pending";

	private JButton getDetailedStatusButton = null;
	
	private WSDLReferences[] wsdlReferences = null;
	
	private Vector wsdlReferencesVector = new Vector();
	
	PartnerLinkFrame fr = null;

	/**
	 * This is the default constructor
	 */
	public WorkflowSubmissionGUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(629, 700);
		this.setTitle("WorkflowSubmissionGUI");
		this.setContentPane(getJContentPane());
		this.setEnabled(true);
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridy = 4;
			gridBagConstraints14.fill = GridBagConstraints.BOTH;
			gridBagConstraints14.gridx = 2;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridy = 2;
			gridBagConstraints61.gridx = 0;
			jLabel5 = new JLabel();
			jLabel5.setText("Output  :");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.weightx = 1.0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.anchor = GridBagConstraints.NORTH;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.anchor = GridBagConstraints.NORTH;
			gridBagConstraints3.weightx = 1.0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.anchor = GridBagConstraints.NORTH;
			gridBagConstraints2.gridx = 0;
			jLabel4 = new JLabel();
			jLabel4.setText("Workflow Name:");
			jLabel4.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 2;
			gridBagConstraints13.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(1, 1, 1, 1);
			gridBagConstraints.ipadx = 1;
			gridBagConstraints.ipady = 1;
			gridBagConstraints.weightx = 0.0D;
			gridBagConstraints.weighty = 0.0D;
			gridBagConstraints.gridy = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("Pending");
			jLabel3.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabel3.setForeground(new Color(250, 0, 0));
			jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 2;
			gridBagConstraints12.gridy = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridx = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Workflow Status:");
			jLabel2.setBackground(new Color(238, 0, 6));
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Input XML");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.weighty = 1.0;
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.insets = new Insets(1, 1, 1, 1);
			gridBagConstraints9.ipadx = 1;
			gridBagConstraints9.ipady = 2;
			gridBagConstraints9.anchor = GridBagConstraints.CENTER;
			gridBagConstraints9.weightx = 1.0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.NORTHWEST;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints7.gridx = -1;
			gridBagConstraints7.gridy = -1;
			gridBagConstraints7.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.ipadx = 20;
			jLabel = new JLabel();
			jLabel.setText("BPEL File:");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setSize(new Dimension(449, 259));
			jContentPane.add(jLabel, gridBagConstraints6);
			jContentPane.add(getBpelTextField(), gridBagConstraints7);
			jContentPane.add(getJButton(), gridBagConstraints8);
			jContentPane.add(getInputTextArea(), gridBagConstraints9);
			jContentPane.add(jLabel1, gridBagConstraints10);
			jContentPane.add(getSubmitButton(), gridBagConstraints12);
			jContentPane.setEnabled(true);
			jContentPane.setVisible(true);
			jContentPane.add(jLabel2, gridBagConstraints11);
			jContentPane.add(jLabel3, gridBagConstraints);
			jContentPane.add(getStartButton(), gridBagConstraints13);
			jContentPane.add(getGetStatusButton(), gridBagConstraints1);
			jContentPane.add(jLabel4, gridBagConstraints2);
			jContentPane.add(getNameTextField(), gridBagConstraints3);
			jContentPane.add(getPartnerLinkButton(), gridBagConstraints4);
			jContentPane.add(getJTextArea(), gridBagConstraints5);
			jContentPane.add(jLabel5, gridBagConstraints61);
			jContentPane.add(getGetDetailedStatusButton(), gridBagConstraints14);
		}
		return jContentPane;
	}

	/**
	 * This method initializes bpelTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getBpelTextField() {
		if (bpelTextField == null) {
			bpelTextField = new JTextField();
			bpelTextField
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			bpelTextField.setPreferredSize(new Dimension(200, 20));
		}
		return bpelTextField;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setPreferredSize(new Dimension(100, 20));
			jButton.setText("Browse");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO
																// Auto-generated
																// Event stub
																// actionPerformed()
					JFrame tempFrame = new JFrame(); // temp frame to open
														// file
					// chooser from
					JFileChooser chooser = new JFileChooser(System
							.getProperty("user.dir"));
					chooser.setDialogTitle("Select a BPEL file");
					chooser.setDialogType(JFileChooser.OPEN_DIALOG);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setMultiSelectionEnabled(false);
					// TODO: FIX THIS
					// chooser.setFileFilter(new XMLFileFilter());
					int choice = chooser.showOpenDialog(tempFrame);
					if (choice == JFileChooser.APPROVE_OPTION) {
						try {
							bpelFile = new File(chooser.getSelectedFile()
									.getAbsolutePath());
							getBpelTextField().setText(
									bpelFile.getAbsolutePath());
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null,
									ex.getMessage(), "Error loading file",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						System.err
								.println("No configuration file passed in or selected... exiting.");
						//System.exit(1);
					}
					// destroy the temp frame
					tempFrame.dispose();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes inputTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getInputTextArea() {
		if (inputTextArea == null) {
			inputTextArea = new JTextArea();
			inputTextArea.setPreferredSize(new Dimension(200, 200));
			inputTextArea.setLineWrap(true);
			inputTextArea.setWrapStyleWord(true);
			inputTextArea
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		}
		return inputTextArea;
	}

	/**
	 * This method initializes submitButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSubmitButton() {
		if (submitButton == null) {
			submitButton = new JButton();
			submitButton.setPreferredSize(new Dimension(100, 20));
			submitButton.setText("Submit");
			submitButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getBpelTextField().getText().trim().equals("")) {
						ErrorDialog.showError("BPEL File cannot be empty");
					}
					try {
						wsdlReferencesVector = fr.getWSDLReferences();
						wsdlReferences = new WSDLReferences[wsdlReferencesVector.size()];
						for(int i=0;i<wsdlReferencesVector.size();i++) {
							wsdlReferences[i] = (WSDLReferences) wsdlReferencesVector.elementAt(i);
							
						}
						workflowFactoryURL = (String)WorkflowUIUtils.getWorkflowFactoryURL().get(0);
						System.out.println("Connecting to " + workflowFactoryURL);
						factoryClient = new WorkflowFactoryServiceClient(
								workflowFactoryURL);
						WMSInputType input = factoryClient.createInput(bpelFile
								.getAbsolutePath(), nameTextField.getText(), wsdlReferences);
						WMSOutputType output = factoryClient
								.createWorkflow(input);
						epr = output.getWorkflowEPR();
						FileWriter writer = new FileWriter("workflow_"
								+ input.getWorkflowName() + "_epr");
						writer.write(ObjectSerializer.toString(epr, new QName(
								"", "WMS_EPR")));
						jLabel3.setText("Submitted");
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null,
								e1.getMessage(), "Error Submitting workflow",
								JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			});
		}
		return submitButton;
	}

	/**
	 * This method initializes startButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStartButton() {
		if (startButton == null) {
			startButton = new JButton();
			startButton.setPreferredSize(new Dimension(100, 20));
			startButton.setText("Start");
			startButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						wclient = new WorkflowServiceImplClient(epr);
						StartInputType startInput = new StartInputType();
						WorkflowInputType inputArgs = new WorkflowInputType();
						String inputDoc = inputTextArea.getText();
						if (inputDoc.trim().equals("")) {
							JOptionPane.showMessageDialog(null,
									"Input cannot be empty", "Input cannot be empty",
									JOptionPane.ERROR_MESSAGE);
						} else {
							System.out.println("Input:" + inputDoc);
							StringReader reader = new StringReader(inputDoc);
							Element e2 = XmlUtils.newDocument(new InputSource(reader))
							.getDocumentElement();
							
							MessageElement anyContent = AnyHelper
							.toAny(new MessageElement(e2));
							inputArgs.set_any(new MessageElement[] { anyContent });
							startInput.setInputArgs(inputArgs);
							wclient.start(startInput);
							jLabel3.setText("Active");
							System.out.println("actionPerformed()");
						}
					} catch (Exception ex) {
						jLabel3.setText("Failed");
						JOptionPane.showMessageDialog(null,
								ex.getMessage(), "Error starting the workflow",
								JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}
			});
		}
		return startButton;
	}

	/**
	 * This method initializes getStatusButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getGetStatusButton() {
		if (getStatusButton == null) {
			getStatusButton = new JButton();
			getStatusButton.setPreferredSize(new Dimension(100, 20));
			getStatusButton.setText("Get Status");
			getStatusButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								status = wclient.getStatus().getValue();
								if (!status.equals("Pending")) {
									jLabel3.setText(status);
								}
								if (status.equalsIgnoreCase("Done")) {
									jLabel3.setText("Done");
									WorkflowOutputType output = wclient.getWorkflowOutput();
									jTextArea.setText(
											AnyHelper.toSingleString(output.get_any()));
								}
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(null,
										ex.getMessage(), "Error getting status of " +
												" the workflow",
										JOptionPane.ERROR_MESSAGE);
								ex.printStackTrace();
							}
							
						}
					});
		}
		return getStatusButton;
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
		}
		return nameTextField;
	}

	/**
	 * This method initializes partnerLinkButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPartnerLinkButton() {
		if (partnerLinkButton == null) {
			partnerLinkButton = new JButton();
			partnerLinkButton.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			partnerLinkButton.setText("Add Partners..");
			partnerLinkButton.setPreferredSize(new Dimension(100, 20));
			partnerLinkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fr = new PartnerLinkFrame();
					fr.show();
					
				}
			});
		}
		return partnerLinkButton;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			jTextArea.setLineWrap(true);
			jTextArea.setPreferredSize(new Dimension(200, 200));
		}
		return jTextArea;
	}

	/**
	 * This method initializes getDetailedStatusButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGetDetailedStatusButton() {
		if (getDetailedStatusButton == null) {
			getDetailedStatusButton = new JButton();
			getDetailedStatusButton.setPreferredSize(new Dimension(50, 20));
			getDetailedStatusButton.setText("Get Details");
			getDetailedStatusButton.setEnabled(true);
			getDetailedStatusButton.setName("getDetailedStatusButton");
			getDetailedStatusButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						WorkflowStatusEventType[] eve = wclient.getDetailedStatus();
						if(eve != null) {
							WorkflowDetailedStatusGUI detGUI = new WorkflowDetailedStatusGUI(eve);
							detGUI.show();
						} else {
							JOptionPane.showMessageDialog(null,
									null, "Error starting the workflow",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					} catch (WorkflowException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return getDetailedStatusButton;
	}

	public static void main(String args[]) {
		WorkflowSubmissionGUI gui = new WorkflowSubmissionGUI();
		gui.setEnabled(true);
		gui.setVisible(true);
		gui.show();
	}
} // @jve:decl-index=0:visual-constraint="181,6"

