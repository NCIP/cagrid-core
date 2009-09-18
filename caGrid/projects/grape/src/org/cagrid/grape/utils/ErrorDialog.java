package org.cagrid.grape.utils;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;


public class ErrorDialog extends JDialog {

	private final static int WIDTH_NO_DETAILS = 500;
	private final static int HEIGHT_NO_DETAILS = 200;
	private final static int WIDTH_DETAILS = 500;
	private final static int HEIGHT_DETAILS = 400;

	private static final long serialVersionUID = 1L;

	private static Frame ownerFrame = null;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel messagePanel = null;

	private JScrollPane jScrollPane = null;

	private JTextArea error = null;

	private String message;

	private JPanel detailsPanel = null;

	private JScrollPane jScrollPane1 = null;

	private JTextArea details = null;

	private JButton detailsButton = null;

	private boolean detailsShown = false;

	private Throwable exception;

	private String strDetails;
	private JButton close = null;


	public static void showError(String message, Throwable ex) {
		ErrorDialog window = new ErrorDialog(getOwnerFrame(), message, ex);
		centerDialog(window);
		window.setVisible(true);
	}


	public static void showError(String message, String details) {
		ErrorDialog window = new ErrorDialog(getOwnerFrame(), message, details);
		centerDialog(window);
		window.setVisible(true);
	}


	private static void centerDialog(ErrorDialog dialog) {
		// Determine the new location of the window
		Frame owner = getOwnerFrame();
		if (owner != null) {
			int w = owner.getSize().width;
			int h = owner.getSize().height;
			int x = owner.getLocationOnScreen().x;
			int y = owner.getLocationOnScreen().y;
			Dimension dim = dialog.getSize();
			dialog.setLocation(w / 2 + x - dim.width / 2, h / 2 + y - dim.height / 2);
		}
	}


	public static void showError(Throwable ex) {
		String message = Utils.getExceptionMessage(ex);
		if (message == null) {
			message = "Unknown Error";
		}
		showError(message, ex);
	}


	public static void showError(String error) {
		showError(error, (String) null);
	}


	public static void showError(String error, String[] detail) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < detail.length; i++) {
			builder.append(detail[i]).append("\n");
		}
		showError(error, builder.toString());
	}


	public static void setOwnerFrame(Frame frame) {
		ownerFrame = frame;
	}


	private static Frame getOwnerFrame() {
		return ownerFrame;
	}


	/**
	 * @param owner
	 */
	public ErrorDialog(Frame owner, String message, Throwable exception) {
		super(owner);
		this.message = message;
		this.exception = exception;
		this.setModal(true);
		initialize();
	}


	public ErrorDialog(Frame owner, String message, String strDetails) {
		super(owner);
		this.message = message;
		this.strDetails = strDetails;
		this.setModal(true);
		initialize();
	}


	/**
	 * This method initializes mainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridy = 3;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.EAST;
			gridBagConstraints12.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints12.gridy = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.weighty = 1.0D;
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getMessagePanel(), gridBagConstraints);
			mainPanel.add(getDetailsPanel(), gridBagConstraints11);
			mainPanel.add(getDetailsButton(), gridBagConstraints12);
			mainPanel.add(getClose(), gridBagConstraints13);
		}
		return mainPanel;
	}


	/**
	 * This method initializes messagePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0;
			messagePanel = new JPanel();
			messagePanel.setLayout(new GridBagLayout());
			messagePanel.add(getJScrollPane(), gridBagConstraints1);
			messagePanel.setBorder(BorderFactory.createTitledBorder(null, "Error Message",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
				new Color(62, 109, 181)));
		}
		return messagePanel;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getError());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes error
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getError() {
		if (error == null) {
			error = new JTextArea();
			error.setLineWrap(true);
			error.setEditable(false);
			error.setWrapStyleWord(true);
			error.setText(message);
		}
		return error;
	}


	/**
	 * This method initializes detailsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDetailsPanel() {
		if (detailsPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 1.0;
			detailsPanel = new JPanel();
			detailsPanel.setLayout(new GridBagLayout());
			detailsPanel.add(getJScrollPane1(), gridBagConstraints2);
			detailsPanel.setBorder(BorderFactory.createTitledBorder(null, "Error Details",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
				new Color(62, 109, 181)));
		}
		return detailsPanel;
	}


	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getDetails());
		}
		return jScrollPane1;
	}


	/**
	 * This method initializes details
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getDetails() {
		if (details == null) {
			details = new JTextArea();
			details.setEditable(false);
			if (strDetails != null) {
				details.setText(this.strDetails);
			} else if (exception != null) {
				details.setText(FaultUtil.printFaultToString(exception));
			}
		}
		return details;
	}


	private synchronized void hideDetails() {
		if (strDetails != null || exception != null) {
			detailsPanel.setVisible(false);
			this.setSize(WIDTH_NO_DETAILS, HEIGHT_NO_DETAILS);
			detailsButton.setText("Show Details");
			detailsShown = false;
		} else {
			disableDetails();
		}
		invalidate();
		validate();
		repaint();
	}


	private void disableDetails() {
		detailsPanel.setVisible(false);
		this.detailsButton.setVisible(false);
		this.setSize(WIDTH_NO_DETAILS, HEIGHT_NO_DETAILS);
	}


	private synchronized void showDetails() {
		if (strDetails != null || exception != null) {
			detailsPanel.setVisible(true);
			this.setSize(WIDTH_DETAILS, HEIGHT_DETAILS);
			detailsButton.setText("Hide Details");
			detailsShown = true;
		} else {
			disableDetails();
		}
		invalidate();
		validate();
		repaint();
	}


	/**
	 * This method initializes detailsButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDetailsButton() {
		if (detailsButton == null) {
			detailsButton = new JButton();
			detailsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (detailsShown) {
						hideDetails();
					} else {
						showDetails();
					}
				}
			});
		}
		return detailsButton;
	}

    /**
	 * This method initializes close	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClose() {
		if (close == null) {
			close = new JButton();
			close.setText("Close");
			close.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
			
		}
		return close;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ErrorDialog window = new ErrorDialog(
			null,
			"When the moon hits your eyes like a big pizza pie thats amore.   When the world seem to shine like you had too much whine, that amore.",
			"When the moon hits your eyes like a big pizza pie thats amore.   When the world seem to shine like you had too much whine, that amore.");
		window.setVisible(true);

	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Error");
		hideDetails();
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getMainPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

}
