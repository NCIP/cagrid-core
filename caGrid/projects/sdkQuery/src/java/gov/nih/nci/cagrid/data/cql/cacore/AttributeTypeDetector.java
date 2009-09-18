package gov.nih.nci.cagrid.data.cql.cacore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 
 *  AttributeTypeDetector
 *  Determines types of attributes of objects
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Jan 3, 2007 
 * @version $Id: AttributeTypeDetector.java,v 1.1 2007-01-22 18:24:41 dervin Exp $ 
 */
public class AttributeTypeDetector {

	// Static constants indicate type
	public static final int STRING_TYPE = 101;
	public static final int DATE_TYPE = 102;
	public static final int INT_TYPE = 103;
	public static final int LONG_TYPE = 104;
	public static final int CHARACTER_TYPE = 105;
	public static final int BOOLEAN_TYPE = 106;
	
	// an extra code for otherwise unknown types
	public static final int UNKNOWN_TYPE = -1;
	
	// synchronized maps for use in a potentially multi threaded environment
	private static Map fieldTypeMap = Collections.synchronizedMap(new HashMap());
	private static Map methodTypeMap = Collections.synchronizedMap(new HashMap());
	
	/**
	 * Determines the data type of a field name in a class
	 * 
	 * @param field
	 * 		The field to determine the type of
	 * @return
	 * 		A static code indicating the type of the field
	 */
	public static int determineType(Field field) {
		String className = field.getDeclaringClass().getName();
		String fieldName = field.getName();
		String fullFieldName = className + '.' + fieldName;
		Integer typeValue = (Integer) fieldTypeMap.get(fullFieldName);
		if (typeValue == null) {
			// haven't looked at this one yet, determine the type
			String fieldTypeName = field.getType().getName();
			
			int code = encodeTypeName(fieldTypeName);
			typeValue = Integer.valueOf(code);
			fieldTypeMap.put(fullFieldName, typeValue);
		}
		return typeValue.intValue();
	}
	
	
	/**
	 * Determines the data type returned by a method
	 * 
	 * @param method
	 * 		The method
	 * @return
	 * 		An integer code indicating the type of returned object
	 */
	public static int determineReturnType(Method method) {
		StringBuilder fullMethodName = new StringBuilder();
		fullMethodName.append(method.getDeclaringClass()).append('.');
		fullMethodName.append(method.getName()).append('(');
		Class[] paramTypes = method.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			fullMethodName.append(paramTypes[i].getName());
			if (i + 1 < paramTypes.length) {
				fullMethodName.append(", ");
			}
		}
		fullMethodName.append(')');
		
		Integer typeValue = (Integer) methodTypeMap.get(fullMethodName.toString());
		if (typeValue == null) {
			// find out the type of the return class
			Class returnType = method.getReturnType();
			
			int code = UNKNOWN_TYPE;
			if (returnType != null) {
				String returnTypeName = returnType.getName();
				code = encodeTypeName(returnTypeName);
			}
			typeValue = Integer.valueOf(code);
			methodTypeMap.put(fullMethodName.toString(), typeValue);
		}
		return typeValue.intValue();
	}
	
	
	private static int encodeTypeName(String typeName) {
		int code = UNKNOWN_TYPE;
		if (typeName.equals(String.class.getName())) {
			code = STRING_TYPE;
		} else if (typeName.equals(Date.class.getName())) {
			code = DATE_TYPE;
		} else if (typeName.equals(Integer.class.getName()) || typeName.equals("int")) {
			code = INT_TYPE;
		} else if (typeName.equals(Long.class.getName()) || typeName.equals("long")) {
			code = LONG_TYPE;
		} else if (typeName.equals(Character.class.getName()) || typeName.equals("char")) {
			code = CHARACTER_TYPE;
		} else if (typeName.equals(Boolean.class.getName()) || typeName.equals("boolean")) {
			code = BOOLEAN_TYPE;
		}
		return code;
	}
}
