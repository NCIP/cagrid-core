package org.cagrid.gaards.ui.gts;

import javax.swing.ImageIcon;

import org.cagrid.gaards.ui.common.GAARDSLookAndFeel;
import org.cagrid.grape.utils.IconUtils;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class GTSLookAndFeel extends GAARDSLookAndFeel{
	public final static ImageIcon getGTSIcon() {
		return IconUtils.loadIcon("/system-software-update.png");
	}


	public final static ImageIcon getTrustLevelIcon() {
		return IconUtils.loadIcon("/trust_level.png");
	}


	public final static ImageIcon getTrustedAuthorityIcon() {
		return IconUtils.loadIcon("/certificate-authority.png");
	}


	public final static ImageIcon getCRLIcon() {
		return IconUtils.loadIcon("/contact-delete.png");
	}
	

	public final static ImageIcon getAuthorityIcon() {
		return IconUtils.loadIcon("/trust-fabric.png");
	}


	public final static ImageIcon getIncreasePriorityIcon() {
		return IconUtils.loadIcon("/priority-up.png");
	}


	public final static ImageIcon getDecresePriorityIcon() {
		return IconUtils.loadIcon("/priority-down.png");
	}

}
