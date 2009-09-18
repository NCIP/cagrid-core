package gov.nih.nci.cagrid.data.cql.cacore;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/** 
 *  DateUtils
 *  Basic utilities for manipulating Dates
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 29, 2006 
 * @version $Id: DateUtils.java,v 1.1 2006-11-29 17:08:25 dervin Exp $ 
 */
public class DateUtils {

	public static Date parseDate(String dateString) throws ParseException {
		DateFormat localFormat = DateFormat.getDateInstance();
		return localFormat.parse(dateString);
	}
	
	
	public static Date createDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal.getTime();
	}
	
	
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	
	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH);
	}
	
	
	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	
	public static String getDateString(Date date) {
		DateFormat localFormat = DateFormat.getDateInstance();
		return localFormat.format(date);
	}
	
	
	public static String getDateString(int year, int month, int day) {
		Date date = createDate(year, month, day);
		return getDateString(date);
	}
}
