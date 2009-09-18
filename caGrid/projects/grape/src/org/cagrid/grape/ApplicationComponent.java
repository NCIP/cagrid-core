package org.cagrid.grape;

import javax.swing.JInternalFrame;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class ApplicationComponent extends JInternalFrame {

	public ApplicationComponent() {
		setSize(200, 300);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setResizable(true);
	}

}