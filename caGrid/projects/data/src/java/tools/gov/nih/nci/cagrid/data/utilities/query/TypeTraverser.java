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
