package org.cagrid.grape.utils;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.errors.ErrorContainer;
import org.cagrid.grape.utils.errors.ErrorDialogTable;
import org.cagrid.grape.utils.errors.ErrorDialogTableListener;

/** 
 *  PortalErrorDialog
 *  Dialog for displaying / queueing up errors and detail messages
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 2, 2006 
 * @version $Id: CompositeErrorDialog.java,v 1.1 2007-11-06 15:53:42 hastings Exp $ 
 */
public class CompositeErrorDialog extends JDialog {
	
	private static Frame ownerFrame = null;
	private static Vector<ErrorContainer> errors = null;
	private static CompositeErrorDialog dialog = null;
	private static String lastFileLocation = null;
    
    private static Object errorAdditionMutex = new Object();
    
    private ErrorContainer currentError = null;
    private boolean showingErrorDetails = false;
    private boolean showingErrorException = false;
	
    private ErrorDialogTable errorTable = null;
	private JScrollPane errorScrollPane = null;
	private JTextArea detailTextArea = null;
	private JScrollPane detailScrollPane = null;
	private JButton clearButton = null;
	private JPanel mainPanel = null;
	private JButton hideDialogButton = null;
	private JButton logErrorsButton = null;
	private JPanel buttonPanel = null;
    private JSplitPane errorsSplitPane = null;

	private CompositeErrorDialog(Frame parentFrame) {
		super(parentFrame);
		initialize();
	}
		

	private void initialize() {
		setTitle("Errors");
		this.setContentPane(getMainPanel());
		pack();
	}
	
	
	public static void setOwnerFrame(Frame frame) {
		ownerFrame = frame;
	}
	
	
	private static Frame getOwnerFrame() {
		if (ownerFrame == null) {
			return GridApplication.getContext().getApplication();
		}
		return ownerFrame;
	}
	
	
    /**
     * Only message is required.  Detail will be shown when asked for, exception shown
     * when asked for, each only if != null
     * 
     * @param message
     * @param detail
     * @param error
     */
	private static void addError(final String message, final String detail, final Throwable error) {
		if (dialog == null) {
			dialog = new CompositeErrorDialog(getOwnerFrame());	
		}
		Runnable r = new Runnable() {
			public void run() {
                synchronized (errorAdditionMutex) {
                    dialog.setAlwaysOnTop(true);
                    ErrorContainer container = new ErrorContainer(message, detail, error);
                    if (errors == null) {
                        errors = new Vector<ErrorContainer>();
                    }
                    errors.add(container);
                    dialog.getErrorTable().addError(container);
                    if (!dialog.isVisible()) {
                        dialog.setModal(true);
                        // dialog.pack();
                        dialog.setSize(500, 450);
                        // attempt to center the dialog
                        centerDialog();
                        dialog.setVisible(true);
                    }                    
                }
			}
		};
		SwingUtilities.invokeLater(r);
	}
	
	
    /**
     * Shows an error message from an exception.  The message presented will
     * be the exception's message, or the exception's class name
     * if no message is present
     * 
     * @param ex
     */
	public static void showErrorDialog(Throwable ex) {
		String message = ex.getMessage();
		if (message == null) {
			message = ex.getClass().getName();
		}
		addError(message, null, ex);
	}
	
	
    /**
     * Shows an error message with no detail or exception
     * 
     * @param error
     */
	public static void showErrorDialog(String error) {
		addError(error, null, null);
	}
	
	
    /**
     * Shows an error message with details
     * @param error
     * @param detail
     */
	public static void showErrorDialog(String error, String detail) {
		addError(error, detail, null);
	}
	
	
    /**
     * Shows an error message with multi-line details
     * 
     * @param error
     * @param detail
     */
	public static void showErrorDialog(String error, String[] detail) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < detail.length; i++) {
			builder.append(detail[i]).append("\n");
		}
		addError(error, builder.toString(), null);
	}
	
	
    /**
     * Shows an error message with an exception
     * 
     * @param message
     * @param ex
     */
	public static void showErrorDialog(String message, Throwable ex) {
		addError(message, null, ex);
	}
    
    
    /**
     * Shows an error message with a multi-line detail message and exception
     * 
     * @param message
     * @param details
     * @param ex
     */
	public static void showErrorDialog(String message, String[] details, Throwable ex) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; details != null && i < details.length; i++) {
            builder.append(details[i]).append("\n");
        }
        addError(message, builder.toString(), ex);
    }

    
    /**
     * Shows an error message with a detail message and exception
     * 
     * @param message
     * @param details
     * @param ex
     */
    public static void showErrorDialog(String message, String details, Throwable ex) {
        addError(message, details, ex);
    }

	
	private ErrorDialogTable getErrorTable() {
	    if (errorTable == null) {
	        errorTable = new ErrorDialogTable();
            errorTable.addErrorTableListener(new ErrorDialogTableListener() {
                public void showDetailsClicked(ErrorContainer container) {
                    if (container == currentError && showingErrorDetails) {
                        getErrorsSplitPane().setDividerLocation(1.0D);
                        getDetailTextArea().setText("");
                        showingErrorDetails = false;
                        showingErrorException = false;
                    } else {
                        currentError = container;
                        showingErrorDetails = true;
                        showingErrorException = false;
                        getErrorsSplitPane().setDividerLocation(0.5D);
                        getDetailTextArea().setText(container.getDetail());
                        getDetailTextArea().setCaretPosition(0);
                    }
                }
                
                
                public void showErrorClicked(ErrorContainer container) {
                    if (container == currentError && showingErrorException) {
                        getErrorsSplitPane().setDividerLocation(1.0D);
                        getDetailTextArea().setText("");
                        showingErrorDetails = false;
                        showingErrorException = false;
                    } else {
                        currentError = container;
                        showingErrorException = true;
                        showingErrorDetails = false;
                        StringWriter writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        container.getError().printStackTrace(printWriter);
                        getErrorsSplitPane().setDividerLocation(0.5D);
                        getDetailTextArea().setText(writer.getBuffer().toString());
                        getDetailTextArea().setCaretPosition(0);
                    }
                }
            });
        }
        return errorTable;
    }

	
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getErrorScrollPane() {
		if (errorScrollPane == null) {
			errorScrollPane = new JScrollPane();
			errorScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Errors", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			errorScrollPane.setViewportView(getErrorTable());
			errorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return errorScrollPane;
	}
	

	/**
	 * This method initializes jTextArea   
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getDetailTextArea() {
		if (detailTextArea == null) {
			detailTextArea = new JTextArea();
			detailTextArea.setEditable(false);
			detailTextArea.setWrapStyleWord(true);
			detailTextArea.setLineWrap(true);
		}
		return detailTextArea;
	}
	

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getDetailScrollPane() {
		if (detailScrollPane == null) {
			detailScrollPane = new JScrollPane();
			detailScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			detailScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			detailScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Detail", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			detailScrollPane.setViewportView(getDetailTextArea());			
		}
		return detailScrollPane;
	}
	

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Clear");
			clearButton.setToolTipText("Clears the dialog of any errors and closes it");
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    errors.clear();
                    getErrorTable().clearTable();
                    getDetailTextArea().setText("");
                    getErrorsSplitPane().setDividerLocation(1.0D);
					dispose();
				}
			});
		}
		return clearButton;
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
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.gridx = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getErrorsSplitPane(), gridBagConstraints);
			mainPanel.add(getButtonPanel(), gridBagConstraints1);
		}
		return mainPanel;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getHideDialogButton() {
		if (hideDialogButton == null) {
			hideDialogButton = new JButton();
			hideDialogButton.setToolTipText(
                "Simply hides the dialog, preserving all displayed errors");
			hideDialogButton.setText("Hide");
			hideDialogButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    getDetailTextArea().setText("");
                    getErrorsSplitPane().setDividerLocation(1.0D);
					dispose();
				}
			});
		}
		return hideDialogButton;
	}
	
	
	private JButton getLogErrorsButton() {
		if (logErrorsButton == null) {
			logErrorsButton = new JButton();
			logErrorsButton.setText("Log Errors");
			logErrorsButton.setToolTipText("Allows saving the error dialog's contents to disk");
			logErrorsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveLogFile();
				}
			});
		}
		return logErrorsButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getLogErrorsButton(), gridBagConstraints1);
			buttonPanel.add(getHideDialogButton(), gridBagConstraints2);
			buttonPanel.add(getClearButton(), gridBagConstraints3);
		}
		return buttonPanel;
	}
	
	
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getErrorsSplitPane() {
		if (errorsSplitPane == null) {
			errorsSplitPane = new JSplitPane();
			errorsSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			errorsSplitPane.setResizeWeight(1.0D);
			errorsSplitPane.setTopComponent(getErrorScrollPane());
			errorsSplitPane.setBottomComponent(getDetailScrollPane());
			errorsSplitPane.setOneTouchExpandable(true);
		}
		return errorsSplitPane;
	}
	
	
	private static void centerDialog() {
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
	
	
	private void saveLogFile() {
		String nl = System.getProperty("line.separator");
		JFileChooser chooser = new JFileChooser(lastFileLocation);
		int choice = chooser.showSaveDialog(dialog);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			lastFileLocation = file.getAbsolutePath();
			StringBuilder text = new StringBuilder();
			synchronized (errors) {
                for (ErrorContainer container : errors) {
                    text.append(container.getMessage()).append(" -- ")
                        .append(DateFormat.getDateTimeInstance().format(
                            container.getErrorDate())).append(nl);
                    if (container.getDetail() != null) {
                        text.append("DETAILS:").append(nl);
                        String[] details = container.getDetail().split("\n");
                        for (String detail : details) {
                            text.append(detail).append(nl);
                        }
                    }
                    if (container.getError() != null) {
                        text.append("EXCEPTION:").append(nl);
                        StringWriter writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        container.getError().printStackTrace(printWriter);
                        String[] lines = writer.getBuffer().toString().split("\n");
                        for (String line : lines) {
                            text.append(line).append(nl);
                        }
                    }
                    text.append("---- ---- ---- ----").append(nl);
                }
			}
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(text.toString());
				writer.flush();
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				showErrorDialog(ex);
			}
		}
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("HELLO THERE");
		frame.setSize(new Dimension(400,400));
		frame.setVisible(true);
		CompositeErrorDialog.setOwnerFrame(frame);
        String message = "";
        for (int i = 0; i < 30; i++) {
            message += "This is line " + i + "\n";
        }
        CompositeErrorDialog.showErrorDialog("This is an error");
        CompositeErrorDialog.showErrorDialog(new Exception("This is an exception with a short message"));
        CompositeErrorDialog.showErrorDialog(new Exception(message));
        CompositeErrorDialog.showErrorDialog("This is an error with a long message", message.split("\n"));
        CompositeErrorDialog.showErrorDialog("This is an error with a null exception", message.split("\n"), null);
        CompositeErrorDialog.showErrorDialog("This is an error with null message", (Exception) null);
        CompositeErrorDialog.showErrorDialog("This is an error with null message and exception", (String) null, null);
        
        /*
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			ErrorDialog.showErrorDialog("Test error", message,
                new Exception(message));		
		}
        */
	}
}
