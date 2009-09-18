package gov.nih.nci.cagrid.data.utilities.query;

/** 
 *  AssociatedType
 *  TODO:DOCUMENT ME
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 9, 2006 
 * @version $Id$ 
 */
public class AssociatedType extends BaseType {

	private String roleName;
	
	public AssociatedType(String typeName, String roleName) {
		super(typeName);
		this.roleName = roleName;
	}
	
	
	public String getRoleName() {
		return roleName;
	}
}
