package gov.nih.nci.cagrid.data.sdk32query;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.data.QueryProcessingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** 
 *  AssociationManyCheckCache
 *  Checks for an association being a 'many' end of an association
 *  (eg. Collection or Array) and caches result
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 3, 2006 
 * @version $Id: AssociationManyCheckCache.java,v 1.3 2008-11-03 20:46:53 dervin Exp $ 
 */
public class AssociationManyCheckCache {
	private static Map<String, Boolean> manyCache = null;

	/**
	 * Returns true if the association from parent to target is a many 
	 * to [one | many] association
	 * @param parent
	 * @param target
	 * @return
	 * 		True if association starts with many
	 * @throws QueryProcessingException
	 */
	public static boolean isManyAssociation(Object parent, Association target) throws QueryProcessingException {
		if (manyCache == null) {
			manyCache = new HashMap<String, Boolean>();
		}
		// see if the association's status has been cached
		String fullAssociationName = getFullAssociationName(parent, target);
		Boolean isMany = manyCache.get(fullAssociationName);
		if (isMany == null) {
			try {
				// get the class of the parent object
				Class parentClass = Class.forName(parent.getName());
				// find the role name of the associated object
				String roleName = target.getRoleName();
				if (roleName == null) {
					roleName = ClassAccessUtilities.getRoleName(parent.getName(), target);
				}
				// find out the type of the parent's field
				Class targetFieldType = null;
				// get the field from the parent class
				Field targetField = ClassAccessUtilities.getNamedField(parentClass, roleName);
				if (targetField != null) {
					targetFieldType = targetField.getType();
				} else {
					Method getterMethod = ClassAccessUtilities.getNamedGetterMethod(parentClass, roleName);
					if (getterMethod != null) {
						targetFieldType = getterMethod.getReturnType();
					}
				}
				
				if (targetFieldType == null) {
					throw new QueryProcessingException("No field " + roleName 
						+ " found for class " + parentClass.getName());
				}
				
				isMany = new Boolean(targetFieldType.isArray()
					|| Collection.class.isAssignableFrom(targetFieldType));
				manyCache.put(fullAssociationName, isMany);
			} catch (Exception ex) {
				throw new QueryProcessingException(ex);
			}
		}
		return isMany.booleanValue();
	}
	
	
	private static String getFullAssociationName(Object parent, Association assoc) throws QueryProcessingException {
		String roleName = assoc.getRoleName();
		if (roleName == null) {
			roleName = ClassAccessUtilities.getRoleName(parent.getName(), assoc);
		}
		return parent.getName() + "." + roleName;
	}
}
