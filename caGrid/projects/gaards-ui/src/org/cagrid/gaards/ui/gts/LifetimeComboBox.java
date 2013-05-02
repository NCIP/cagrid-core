/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.gts.bean.Lifetime;

import javax.swing.JComboBox;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class LifetimeComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;
	
	private static final String ANY = "Any";


	public LifetimeComboBox() {
		this.addItem(ANY);
		this.addItem(Lifetime.Valid);
		this.addItem(Lifetime.Expired);
		this.setEditable(false);
	}


	public Lifetime getLifetime() {
		if (getSelectedItem().equals(ANY)) {
			return null;
		} else {
			return (Lifetime) getSelectedItem();
		}
	}

}
