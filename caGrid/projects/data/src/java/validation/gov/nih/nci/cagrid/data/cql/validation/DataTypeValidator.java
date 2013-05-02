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
package gov.nih.nci.cagrid.data.cql.validation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
		} else if (dataType.equals(Double.class.getName())) {
		    validateDouble(value);
		} else if (dataType.equals(Float.class.getName())) {
		    validateFloat(value);
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
	    // try short date / time, time, then XSD dateTime, just XSD date
        List<SimpleDateFormat> formats = new ArrayList<SimpleDateFormat>(4);
        formats.add((SimpleDateFormat) DateFormat.getInstance());
        formats.add(new SimpleDateFormat("HH:mm:ss"));
        formats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        formats.add(new SimpleDateFormat("yyyy-MM-dd"));
        
        Date date = null;
        Iterator<SimpleDateFormat> formatIter = formats.iterator();
        while (date == null && formatIter.hasNext()) {
            SimpleDateFormat formatter = formatIter.next();
            formatter.setLenient(false);
            try {
                // can we parse a date out of that string?
                date = formatter.parse(value);
                // does the resulting date match the original input when formatted?
                /*
                String reformat = formatter.format(date);
                if (!value.equals(reformat)) {
                    LOG.debug(value + " parsed by pattern " + formatter.toPattern() + 
                        " but reformats as " + reformat + ", and so is not valid");
                    date = null;
                }
                */
            } catch (ParseException ex) {
                LOG.debug(value + " was not parsable by pattern " + formatter.toPattern());
            }
        }
        if (date == null) {
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
	
	
	private static void validateDouble(String value) throws DomainConformanceException {
	    try {
	        Double.valueOf(value);
	    } catch (Exception ex) {
	        throw new DomainConformanceException("Value " + value + " does not parse as a Double");
	    }
	}
	
	
	private static void validateFloat(String value) throws DomainConformanceException {
	    try {
	        Float.valueOf(value);
	    } catch (Exception ex) {
	        throw new DomainConformanceException("Value " + value + " does not parse as a Float");
	    }
	}
	
	
	private DataTypeValidator() {
	    // prevent instantiation
	}
}
