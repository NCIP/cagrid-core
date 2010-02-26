package gov.nih.nci.cagrid.data.cql.validation;

import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * DataTypeValidator 
 * Validates values from a CQL query against their specified data type
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jul 31, 2006
 * @version $Id$
 */
public class DataTypeValidator {
	private static Log LOG = LogFactory.getLog(DataTypeValidator.class);


	public static void validate(String value, String dataType) throws DomainConformanceException {
		if (dataType.equals(String.class.getName())) {
			// this is fairly common, so returning immediately is a slight
			// performance boost
			return;
		} else if (dataType.equals(Long.class.getName())) {
			validateLong(value);
		} else if (dataType.equals(Integer.class.getName())) {
			validateInteger(value);
		} else if (dataType.equals(Date.class.getName())) {
			validateDate(value);
		} else if (dataType.equals(Boolean.class.getName())) {
			validateBoolean(value);
		} else if (dataType.equals(Character.class.getName()) || dataType.equals("CHARACTER")) {
			validateCharacter(value);
		} else {
			LOG.warn("Data type " + dataType + " not recognized; Validated only as a String");
		}
	}


	private static void validateInteger(String value) throws DomainConformanceException {
		// parse the integer
		try {
			Integer.parseInt(value);
		} catch (Exception ex) {
			throw new DomainConformanceException("Value " + value + " does not parse as an Integer");
		}
	}


	private static void validateLong(String value) throws DomainConformanceException {
		// parse the long
		try {
			Long.parseLong(value);
		} catch (Exception ex) {
			throw new DomainConformanceException("Value " + value + " does not parse as a Long");
		}
	}


	private static void validateDate(String value) throws DomainConformanceException {
		try {
			DateFormat.getInstance().parse(value);
		} catch (Exception ex) {
			throw new DomainConformanceException("Value " + value + " does not parse as a Date");
		}
	}


	private static void validateBoolean(String value) throws DomainConformanceException {
		try {
			Boolean.valueOf(value);
		} catch (Exception ex) {
			throw new DomainConformanceException("Value " + value + " does not parse as a Boolean");
		}
	}


	private static void validateCharacter(String value) throws DomainConformanceException {
		if (value.length() > 1) {
			throw new DomainConformanceException("Value " + value + " is not a single Character or empty");
		}
	}
	
	
	private DataTypeValidator() {
	    // prevent instantiation
	}
}
