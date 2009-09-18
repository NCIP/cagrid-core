package org.cagrid.cacore.sdk4x.cql2.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** 
 *  ClassAccessUtilities
 *  A package of utilities for accessing classes in the context
 *  of CQL query processing
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 3, 2006 
 * @version $Id: ClassAccessUtilities.java,v 1.2 2008/04/04 15:22:34 dervin Exp $ 
 */
public class ClassAccessUtilities {	
	
	/**
	 * Gets the named field from the class or any of its super classes
	 * 
	 * @param clazz
	 * 		The class to retrieve a field from
	 * @param fieldName
	 * 		The name of the field to retrieve
	 * @return
	 * 		The named field, or <code>null</code> if it was not found
	 */
	public static Field getNamedField(Class<?> clazz, String fieldName) {
		Class<?> checkClass = clazz;
		while (checkClass != null) {
			Field[] classFields = checkClass.getDeclaredFields();
			for (int i = 0; i < classFields.length; i++) {
				if (classFields[i].getName().equals(fieldName)) {
					return classFields[i];
				}
			}
			checkClass = checkClass.getSuperclass();
		}
		return null;
	}
	
	
	/**
	 * Gets the 'getter' method from the class or any of its super 
	 * classes for a named field.  For example, if a field name
	 * of 'foo' is given, this method looks for a method named
	 * <code>getFoo</code> on the class.
	 * 
	 * @param clazz
	 * 		The class to retrieve a getter method from
	 * @param fieldName
	 * 		The name of the field
	 * @return
	 * 		The getter method for the field, or <code>null</code> if it was not found
	 */
	public static Method getNamedGetterMethod(Class<?> clazz, String fieldName) {
		Class<?> checkClass = clazz;
		while (checkClass != null) {
			Method[] methods = checkClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				String methodName = methods[i].getName();
				if (methodName.startsWith("get") && methods[i].getParameterTypes().length == 0) {
					// strip off the 'get'
					String getterFieldName = methodName.substring(3);
                    // check the getterFieldName without changing the case of the first
                    // character first.  This way, if an attribute field name starts
                    // with an uppercase char, it'll still find the getter
                    if (getterFieldName.equals(fieldName)) {
                        return methods[i];
                    }
                    // lowercase the first character and check again
					if (getterFieldName.length() == 1) {
						getterFieldName = String.valueOf(Character.toLowerCase(getterFieldName.charAt(0)));
					} else {
						getterFieldName = String.valueOf(Character.toLowerCase(getterFieldName.charAt(0))) 
							+ getterFieldName.substring(1);
					}
					if (getterFieldName.equals(fieldName)) {
						return methods[i];
					}
				}			
			}
			checkClass = checkClass.getSuperclass();
		}
		return null;
	}
}
