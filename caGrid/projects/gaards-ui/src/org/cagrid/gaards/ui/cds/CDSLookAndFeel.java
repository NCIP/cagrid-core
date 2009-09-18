package org.cagrid.gaards.ui.cds;

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
public class CDSLookAndFeel extends GAARDSLookAndFeel {
	
	
	
	public final static ImageIcon getDelegateCredentialIcon() {
		return IconUtils.loadIcon("/delegate-credential.png");
	}
	
	public final static ImageIcon getDelegatedCredentialIcon() {
		return IconUtils.loadIcon("/delegated-credential.png");
	}
	
	public final static ImageIcon getDelegateCredentialsIcon() {
		return IconUtils.loadIcon("/delegate-credentials.png");
	}
	
	public final static ImageIcon getDelegationPolicyIcon() {
		return IconUtils.loadIcon("/delegation-policy.png");
	}
	
	public final static ImageIcon getAudtingIcon() {
		return IconUtils.loadIcon("/auditing.png");
	}
	
	public final static ImageIcon getCalendarIcon() {
		return IconUtils.loadIcon("/calendar.png");
	}
	
	public final static ImageIcon getClearIcon() {
		return IconUtils.loadIcon("/clear.png");
	}


}
