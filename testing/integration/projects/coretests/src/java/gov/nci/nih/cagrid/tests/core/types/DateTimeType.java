/*
 * Created on Feb 16, 2005
 */
package gov.nci.nih.cagrid.tests.core.types;

import org.apache.xerces.impl.dv.xs.DateTimeDV;

/**
 * Wrapper for an XML dataTime
 * 
 * @author MCCON012
 */
public class DateTimeType
	extends DateTimeDV
{
	/**
	 * The DateTime values
	 */
	private int[] dateTime;
	
	/**
	 * Parse a dateTime
	 * 
	 * @param str The XML dateTime
	 */
	public DateTimeType(String str)
	{
		super();
		
		dateTime = this.parse(str);
	}
	
	/**
	 * Test whether this DateTime is equivalent to another
	 */
	public boolean equals(Object obj)
	{
		short value = compareDates(dateTime, ((DateTimeType) obj).dateTime, false);
		return value == 0;
	}
}