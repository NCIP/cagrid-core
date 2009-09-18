package org.cagrid.grape.samples;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.grape.ApplicationComponent;


public class SampleWindow extends ApplicationComponent {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JLabel label = null;


	/**
	 * This is the default constructor
	 */
	public SampleWindow() {
		super();
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.gridy = 0;
			label = new JLabel();
			label.setText("Hello World");
			label.setFont(new Font("Dialog", Font.BOLD, 36));
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(label, gridBagConstraints);
		}
		return jContentPane;
	}

}
