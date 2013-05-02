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
 *  BaseType
 *  TODO:DOCUMENT ME
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 9, 2006 
 * @version $Id$ 
 */
public class BaseType {
	private String typeName;

	public BaseType(String typeName) {
		this.typeName = typeName;
	}
	
	
	public String getTypeName() {
		return typeName;
	}
	
	
	public String toString() {
		return getTypeName();
	}
	
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof BaseType)) {
			return false;
		}
		return getTypeName().equals(((BaseType) o).getTypeName());
	}
	
	
	public int hashCode() {
		return getTypeName().hashCode();
	}
}
