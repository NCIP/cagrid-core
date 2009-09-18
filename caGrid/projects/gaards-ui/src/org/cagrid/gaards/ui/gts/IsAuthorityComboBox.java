package org.cagrid.gaards.ui.gts;

import javax.swing.JComboBox;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class IsAuthorityComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;
	
	private static final String ANY = "Any";


	public IsAuthorityComboBox() {
		this.addItem(ANY);
		this.addItem(Boolean.TRUE);
		this.addItem(Boolean.FALSE);
		this.setEditable(false);
	}


	public Boolean getIsAuthority() {
		if (getSelectedItem().equals(ANY)) {
			return null;
		} else {
			return (Boolean) getSelectedItem();
		}
	}

}
