package org.cagrid.gaards.ui.dorian.idp;

import org.cagrid.gaards.dorian.idp.LocalUserRole;
import org.cagrid.gaards.ui.common.AxisTypeComboBox;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class UserRolesComboBox extends AxisTypeComboBox {
	
	private static final long serialVersionUID = 1L;

	public UserRolesComboBox() {
		this(false);
	}

	public UserRolesComboBox(boolean anyState) {
		super(LocalUserRole.class, anyState);
	}

	public LocalUserRole getSelectedUserRole() {
		return (LocalUserRole) getSelectedObject();
	}

}
