package gov.nih.nci.cagrid.introduce.portal.modification.security;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class GridMap {
	private String gridIdentity;
	private String localUser;


	public GridMap() {

	}


	public GridMap(String gridIdentity, String localUser) {
		this.gridIdentity = gridIdentity;
		this.localUser = localUser;
	}


	public String getGridIdentity() {
		return gridIdentity;
	}


	public String getLocalUser() {
		return localUser;
	}


	public void setGridIdentity(String gridIdentity) {
		this.gridIdentity = gridIdentity;
	}


	public void setLocalUser(String localUser) {
		this.localUser = localUser;
	}

}
