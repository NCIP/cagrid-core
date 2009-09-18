package org.cagrid.gaards.ui.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class AxisTypeComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;
	
	public static String ANY = "Any";

	private List list;
	private Class c;
	private boolean any;

	public AxisTypeComboBox(Class c) {
		this(c, false);
	}

	public AxisTypeComboBox(Class c,boolean any) {
		list = new ArrayList();
		this.c = c;
		this.any = any;

		if (any) {
			list.add(ANY);
		}

		Field[] fields = c.getFields();

		for (int i = 0; i < fields.length; i++) {
			if (c.isAssignableFrom(fields[i].getType())) {
				try {
					Object o = fields[i].get(null);
					list.add(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for (int i = 0; i < list.size(); i++) {
			this.addItem(list.get(i));
		}
	}
	
	public void setToAny(){
		if(any){
			setSelectedItem(ANY);
		}
		
	}

	public Object getSelectedObject() {
		if (getSelectedItem().getClass().isAssignableFrom(c)) {
			return getSelectedItem();
		} else {
			return null;
		}
	}


}
