package gov.nih.nci.cagrid.common.portal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class MultiEventProgressBar extends JProgressBar {

	private Map<Integer, String> events;
	private int id;
	private boolean hideWhenComplete;


	public MultiEventProgressBar(boolean onlyShowWhenWorking) {
		this.events = new HashMap<Integer, String>();
		this.id = 0;
		this.hideWhenComplete = onlyShowWhenWorking;
		this.setVisible(!onlyShowWhenWorking);
		setString("");
		setStringPainted(true);
	}


	public synchronized int startEvent(String message) {
		this.setVisible(true);
		id = id + 1;
		Integer bid = new Integer(id);
		events.put(bid, message);
		if (events.size() == 1) {
			updateProgress(true, message);
		}
		return id;
	}


	public void updateProgress(final String message, final int min, final int max, final int current) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MultiEventProgressBar.this.setIndeterminate(false);
				MultiEventProgressBar.this.setString(message);
				MultiEventProgressBar.this.setStringPainted(true);
				MultiEventProgressBar.this.setMinimum(min);
				MultiEventProgressBar.this.setMaximum(max);
				MultiEventProgressBar.this.setValue(current);
			}
		});

	}


	public synchronized void stopEvent(int eventID, String message) {
		Integer bid = new Integer(eventID);
		events.remove(bid);
		if (events.size() == 0) {
			updateProgress(false, message);
			this.setVisible(!this.hideWhenComplete);
		} else {
			Integer min = null;
			Iterator itr = events.keySet().iterator();
			while (itr.hasNext()) {
				Integer num = (Integer) itr.next();
				if ((min == null) || (num.intValue() < min.intValue())) {
					min = num;
				}
			}
			String s = events.get(min);
			updateProgress(true, s);
		}
	}


	public synchronized void stopAll(String message) {
		events.clear();
		updateProgress(false, message);
		this.setVisible(!this.hideWhenComplete);
	}


	private void updateProgress(final boolean working, final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (s != null && !s.trim().equals("")) {
					MultiEventProgressBar.this.setString(s);
					MultiEventProgressBar.this.setStringPainted(true);
				} else {
					MultiEventProgressBar.this.setStringPainted(false);
				}
				setIndeterminate(working);
			}
		});

	}
}