package org.cagrid.gaards.ui.dorian.federation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import org.cagrid.gaards.dorian.federation.FederationAudit;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class FederationAuditComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;
	
	public static String ANY = "Any";

	private List<FederationAudit> list;

	public FederationAuditComboBox() {
		list = new ArrayList<FederationAudit>();
		Class c = FederationAudit.class;

		Field[] fields = FederationAudit.class.getFields();

		for (int i = 0; i < fields.length; i++) {
			if (FederationAudit.class.isAssignableFrom(fields[i].getType())) {
				try {
					FederationAudit o = (FederationAudit) fields[i]
							.get(null);
					list.add(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.addItem(ANY);
		for (int i = 0; i < list.size(); i++) {
			this.addItem(list.get(i));
		}
		this.setSelectedItem(ANY);
	}

	public FederationAuditComboBox(List<FederationAudit> list) {
		this.addItem(ANY);
		for (int i = 0; i < list.size(); i++) {
			this.addItem(list.get(i));
		}
		this.setSelectedItem(ANY);
	}

	public void setToAny() {
		setSelectedItem(ANY);
	}

	public FederationAudit getSelectedAuditType() {
		if (getSelectedItem().getClass().isAssignableFrom(
				FederationAudit.class)) {
			return (FederationAudit) getSelectedItem();
		} else {
			return null;
		}
	}
}
