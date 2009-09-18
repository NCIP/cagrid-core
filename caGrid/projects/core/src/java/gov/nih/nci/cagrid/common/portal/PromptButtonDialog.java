package gov.nih.nci.cagrid.common.portal;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** 
 *  PromptButtonDialog
 *  Dialog to prompt user to select an option button and return the selection
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jul 20, 2006 
 * @version $Id$ 
 */
public class PromptButtonDialog extends JDialog {
	public static final String DEFAULT_TITLE = "Select an option";
	public static final int MAX_BUTTON_WIDTH = 4;
	
	private String[] message;
	private String[] options;
	private String defOption;
	private String selection;
	
	private JPanel mainPanel = null;
	private JPanel textPanel = null;
	private JPanel buttonPanel = null;

	public PromptButtonDialog(Frame owner, String title, String[] message, String[] options, String def) {
		super(owner, title, true);
		this.message = message;
		this.options = options;
		this.defOption = def;
		this.selection = null;
		initialize();
	}
	
	
	public PromptButtonDialog(Dialog owner, String title, String[] message, String[] options, String def) {
		super(owner, title, true);
		this.message = message;
		this.options = options;
		this.defOption = def;
		this.selection = null;
		initialize();
	}
	
	
	public static String prompt(Frame owner, String title, String[] message, String[] options, String def) {
		PromptButtonDialog dialog = new PromptButtonDialog(owner, title, message, options, def);
		return displayDialog(dialog);
	}
	
	
	public static String prompt(Dialog owner, String title, String[] message, String[] options, String def) {
		PromptButtonDialog dialog = new PromptButtonDialog(owner, title, message, options, def);
        return displayDialog(dialog);
	}
    
    
    private static String displayDialog(PromptButtonDialog dialog) {
        dialog.setVisible(true);
        return dialog.getSelection();
    }
	
	
	private void initialize() {
		initButtons();
		initText();
		setContentPane(getMainPanel());
		pack();
	}
	
	
	public String getSelection() {
		return selection;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new java.awt.Insets(4,4,4,4);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.setSize(new java.awt.Dimension(262,157));
			mainPanel.add(getTextPanel(), gridBagConstraints);
			mainPanel.add(getButtonPanel(), gridBagConstraints1);
		}
		return mainPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTextPanel() {
		if (textPanel == null) {
			textPanel = new JPanel();
			textPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(
				javax.swing.border.EtchedBorder.LOWERED));
			textPanel.setLayout(new GridBagLayout());
		}
		return textPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
		}
		return buttonPanel;
	}
	
	
	private void initButtons() {
		ActionListener disposeListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selection = ((JButton) e.getSource()).getText();
				dispose();
			}
		};
		Insets insets = new Insets(2, 2, 2, 2);
		for (int i = 0; i < options.length; i++) {
			JButton button = new JButton(options[i]);
			if (options[i].equals(defOption)) {
				getRootPane().setDefaultButton(button);
			}
			button.addActionListener(disposeListener);
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridx = i % MAX_BUTTON_WIDTH;
			cons.gridy = (int) Math.floor(i / (double) MAX_BUTTON_WIDTH);
			cons.insets = insets;
			getButtonPanel().add(button, cons);
		}
		pack();
	}
	
	
	private void initText() {
		for (int i = 0; i < message.length; i++) {
			JTextField text = new JTextField(message[i]);
			text.setEditable(false);
			text.setBorder(BorderFactory.createEmptyBorder());
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridx = 0;
			cons.gridy = i;
			// TODO: I suppose this could be internationalized or something to default
			// to the EAST side for right to left languages or something
			cons.anchor = GridBagConstraints.WEST;
			cons.fill = GridBagConstraints.HORIZONTAL;
			// maybe use as option to center / right align
			cons.weightx = 1.0d;
			getTextPanel().add(text, cons);
		}
		pack();
	}
	
	
	public static void main(String[] args) {
		String[] choices = {"one", "two", "three", "four", "five"};
		String[] text = {"hello there", "please choose", "an option!"};
		String selection = prompt((Frame) null, "choices", text, choices, choices[3]);
		System.out.println(selection);
	}
}
