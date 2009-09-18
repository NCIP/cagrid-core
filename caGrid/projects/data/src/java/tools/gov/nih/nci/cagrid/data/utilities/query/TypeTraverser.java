package gov.nih.nci.cagrid.data.utilities.query;


/** 
 *  TypeTraverser
 *  Interface for traversing types, associations, and attributes
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 9, 2006 
 * @version $Id$ 
 */
public interface TypeTraverser {

	public BaseType[] getBaseTypes();
	
	
	public AssociatedType[] getAssociatedTypes(BaseType type);
	
	
	public AttributeType[] getAttributes(BaseType type);
}
