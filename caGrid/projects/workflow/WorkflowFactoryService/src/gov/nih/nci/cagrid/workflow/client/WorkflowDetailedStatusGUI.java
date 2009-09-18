/**
 * 
 */
package gov.nih.nci.cagrid.workflow.client;

import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusEventType;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.ComponentOrientation;

/**
 * @author madduri
 *
 */
public class WorkflowDetailedStatusGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JLabel jLabel = null;

	private JTextArea jTextArea = null;

	private JButton jButton = null;

	private WorkflowStatusEventType[] eve = null;
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setPreferredSize(new Dimension(400, 400));
			jTextArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			jTextArea.setEditable(false);
			jTextArea.setLineWrap(true);
		}
		return jTextArea;
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
			jButton.setText("Get Detailed Status");
		}
		return jButton;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WorkflowDetailedStatusGUI thisClass = new WorkflowDetailedStatusGUI(null);
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 * @param eve 
	 */
	public WorkflowDetailedStatusGUI(WorkflowStatusEventType[] eve) {
		super();
		this.eve = eve;
		initialize();
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(558, 574);
		this.setContentPane(getJContentPane());
		this.setTitle("Workflow Detailed Status");
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < this.eve.length; i++ ) {
			buffer.append(this.eve[i].getCurrentOperation())
			.append("\t").append(this.eve[i].getState());
			buffer.append("\n");
		}
		this.getJTextArea().setText(buffer.toString());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Detailed Status");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(jLabel, gridBagConstraints);
			jContentPane.add(getJTextArea(), gridBagConstraints1);
			jContentPane.add(getJButton(), gridBagConstraints2);
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
