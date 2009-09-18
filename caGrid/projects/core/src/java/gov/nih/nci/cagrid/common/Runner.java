package gov.nih.nci.cagrid.common;

import EDU.oswego.cs.dl.util.concurrent.Sync;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin </A>
 * @created Dec 18, 2003
 * @version $Id: Runner.java,v 1.2 2007-04-05 16:42:58 langella Exp $
 */

public abstract class Runner implements java.lang.Runnable {
	private Sync sync;


	public abstract void execute();


	public final void run() {
		this.execute();
		if (sync != null) {
			sync.release();
		}
	}


	protected final void setSync(Sync counter) {
		this.sync = counter;
	}
}
