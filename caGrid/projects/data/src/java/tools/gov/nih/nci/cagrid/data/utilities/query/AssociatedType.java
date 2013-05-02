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
