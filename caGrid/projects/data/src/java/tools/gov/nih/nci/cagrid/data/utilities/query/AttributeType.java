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
 *  AttributeType
 *  TODO:DOCUMENT ME
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 9, 2006 
 * @version $Id$ 
 */
public class AttributeType {
	private String name;
	private String dataType;

	public AttributeType(String name, String dataType) {
		this.name = name;
		this.dataType = dataType;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getDataType() {
		return dataType;
	}
	
	
	public String toString() {
		return getName() + " : " + getDataType();
	}
}
