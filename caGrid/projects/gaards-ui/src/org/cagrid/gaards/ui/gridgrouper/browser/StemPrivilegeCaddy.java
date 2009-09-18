package org.cagrid.gaards.ui.gridgrouper.browser;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class StemPrivilegeCaddy {
	private String identity;

	private boolean create;

	private boolean stem;


	public StemPrivilegeCaddy(String identity) {
		this.identity = identity;
		this.create = false;
		this.stem = false;
	}


	public boolean hasCreate() {
		return create;
	}


	public void setCreate(boolean create) {
		this.create = create;
	}


	public boolean hasStem() {
		return stem;
	}


	public void setStem(boolean stem) {
		this.stem = stem;
	}


	public String getIdentity() {
		return identity;
	}

}
