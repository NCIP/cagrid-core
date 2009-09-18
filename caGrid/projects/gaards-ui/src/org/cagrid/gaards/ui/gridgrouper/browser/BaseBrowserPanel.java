package org.cagrid.gaards.ui.gridgrouper.browser;

import javax.swing.JPanel;

import org.cagrid.grape.utils.MultiEventProgressBar;

public class BaseBrowserPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private MultiEventProgressBar progress;

	public int startEvent(String message) {
		if (this.progress != null) {
			return this.progress.startEvent(message);
		} else {
			return -1;
		}
	}

	public void stopEvent(int eventId, String message) {
		if (this.progress != null) {
			this.progress.stopEvent(eventId, message);
		}
	}

	public void setProgress(MultiEventProgressBar progess) {
		this.progress = progess;
	}
}
