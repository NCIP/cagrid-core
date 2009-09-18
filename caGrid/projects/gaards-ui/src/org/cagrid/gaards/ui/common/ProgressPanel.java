package org.cagrid.gaards.ui.common;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel display = null;
	private JProgressBar progress = null;
	private JLabel title = null;

	/**
	 * This is the default constructor
	 */
	public ProgressPanel() {
		super();
		initialize();
		stopProgress();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.anchor = GridBagConstraints.WEST;
		gridBagConstraints11.weightx = 1.0D;
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.gridy = 0;
		title = new JLabel();
		title.setText("  ");
		title.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.anchor = GridBagConstraints.EAST;
		gridBagConstraints1.insets = new Insets(1, 1, 1, 20);
		gridBagConstraints1.gridy = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.gridy = 0;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getDisplay(), gridBagConstraints);
		this.add(getProgress(), gridBagConstraints1);
		this.add(title, gridBagConstraints11);
	}

	/**
	 * This method initializes display
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getDisplay() {
		if (display == null) {
			display = new JLabel();
			display.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		}
		return display;
	}

	/**
	 * This method initializes progress
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getProgress() {
		if (progress == null) {
			progress = new JProgressBar();
			progress.setMaximum(100);
			progress.setPreferredSize(new Dimension(100, 8));
		}
		return progress;
	}

	public void showProgress(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				title.setText("    ");
				getDisplay().setVisible(true);
				getProgress().setVisible(true);
				getDisplay().setText(s);
				getProgress().setIndeterminate(true);
			}
		});

	}

	public void stopProgress() {
		stopProgress(null);
	}

	public void stopProgress(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (s == null) {
					getDisplay().setVisible(false);
					getDisplay().setText("");
				} else {
					getDisplay().setText(s+"   ");
					getDisplay().setVisible(true);
				}
				getProgress().setVisible(false);
				getProgress().setIndeterminate(false);
			}
		});

	}

}
