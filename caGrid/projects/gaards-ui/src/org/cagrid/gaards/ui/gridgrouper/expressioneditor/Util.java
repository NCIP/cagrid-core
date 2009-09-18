package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.ThreadManager;
import gov.nih.nci.cagrid.common.Utils;

import javax.swing.JOptionPane;


public class Util {

	private static ThreadManager threadManager = new ThreadManager();


	public static void executeInBackground(Runner r) throws Exception {
		threadManager.executeInBackground(r);
	}


	public static void showErrorMessage(String msg) {
		showErrorMessage("Portal Error", msg);
	}


	public static void showErrorMessage(Exception e) {
		showErrorMessage("Portal Error", e);
	}


	public static void showConfigurationErrorMessage(String msg) {
		showErrorMessage("Portal Configuration Error", new String[]{msg});
	}


	public static void showMessage(String msg) {
		showMessage(new String[]{msg});
	}


	public static void showMessage(String[] msg) {
		showMessage("Information", msg);
	}


	public static void showMessage(String title, String msg) {
		showMessage(title, new String[]{msg});
	}


	public static void showMessage(String title, String[] msg) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}


	public static void showErrorMessage(String title, Exception e) {
		String mess = Utils.getExceptionMessage(e);
		JOptionPane.showMessageDialog(null, mess, title, JOptionPane.ERROR_MESSAGE);
	}


	public static void showErrorMessage(String title, String msg) {
		showErrorMessage(title, new String[]{msg});
	}


	public static void showErrorMessage(String title, String[] msg) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
	}
}
