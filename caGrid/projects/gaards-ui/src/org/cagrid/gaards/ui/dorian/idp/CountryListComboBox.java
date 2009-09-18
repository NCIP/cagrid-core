package org.cagrid.gaards.ui.dorian.idp;

import org.cagrid.gaards.dorian.idp.CountryCode;
import org.cagrid.gaards.ui.common.AxisTypeComboBox;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CountryListComboBox extends AxisTypeComboBox {
	
	private static final long serialVersionUID = 1L;

	public CountryListComboBox() {
		this(false);
	}

	public CountryListComboBox(boolean anyCountry) {
		super(CountryCode.class, anyCountry);
		if (!anyCountry) {
			this.setSelectedItem(CountryCode.US);
		}
	}

	public CountryCode getSelectedCountry() {
		return (CountryCode) getSelectedObject();
	}

	public static void main(String[] args) {
		new CountryListComboBox();
	}

}
