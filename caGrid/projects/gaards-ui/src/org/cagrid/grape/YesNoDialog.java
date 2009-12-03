package org.cagrid.grape;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.configuration.ServiceConfiguration;


public class YesNoDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static Frame ownerFrame = null;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel messagePanel = null;

	private JScrollPane jScrollPane = null;

	private JTextArea error = null;

	private String message;
	private String dir;

	private JButton keep = null;
	private JButton delete = null;

	public static void showChoice(String choice, String dir) {
		YesNoDialog window = new YesNoDialog(getOwnerFrame(), choice, dir);
		centerDialog(window);
		window.setVisible(true);
	}


	private static void centerDialog(YesNoDialog dialog) {
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

	public static void setOwnerFrame(Frame frame) {
		ownerFrame = frame;
	}

	private static Frame getOwnerFrame() {
		return ownerFrame;
	}


	/**
	 * @param owner
	 */
	public YesNoDialog(Frame owner, String choice, String dir) {
		super(owner);
		this.message = choice;
		this.dir = dir;
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
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.gridy = 0;
			
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.fill = GridBagConstraints.BOTH;
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
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(getKeep(), gridBagConstraints13);
			buttonPanel.add(getDelete(), gridBagConstraints14);
			mainPanel.add(buttonPanel, gridBagConstraints12);
			
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
			messagePanel.setBorder(BorderFactory.createTitledBorder(null, "",
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
	 * This method initializes close	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getKeep() {
		if (keep == null) {
			keep = new JButton();
			keep.setText("Keep");
			keep.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
			
		}
		return keep;
	}

	private JButton getDelete() {
		if (delete == null) {
			delete = new JButton();
			delete.setText("Delete");
			delete.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					deleteFiles();
					dispose();
				}
			});
			
		}
		return delete;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		YesNoDialog window = new YesNoDialog(
			null,
			"When the moon hits your eyes like a big pizza pie thats amore.   When the world seem to shine like you had too much whine, that amore.",
			"");
		window.setVisible(true);

	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Conflicting Configuration File");
		this.setSize(500, 150);
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
	
	private void deleteFiles() {
		File configurationDir = new File(dir);
		FilenameFilter conf = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith("conf.xml"))
					return true;
				else
					return false;
			}
		};
		File[] confFiles = configurationDir.listFiles(conf);
				
		for (int i = 0; i < confFiles.length; i++) {
					confFiles[i].delete();
		}

	}

}
