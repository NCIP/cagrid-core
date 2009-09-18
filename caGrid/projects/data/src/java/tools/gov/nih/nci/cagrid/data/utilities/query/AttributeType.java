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
