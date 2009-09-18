package gov.nih.nci.cagrid.data.sdk32query;


import gov.nih.nci.cagrid.authorization.GridAuthorizationManager;
import gov.nih.nci.cagrid.authorization.impl.CSMGridAuthorizationManager;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** 
 *  CsmSecurityCheck
 *  Check for CSM security authorization
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 26, 2006 
 * @version $Id: CsmSecurityCheck.java,v 1.3 2008-11-03 20:46:53 dervin Exp $ 
 */
public class CsmSecurityCheck {

	public static final String CSM_CONFIG_PROPERTY = "gov.nih.nci.security.configFile";
	public static final String CSM_PRIVILEGE = "READ";

	public static synchronized boolean checkAuthorization(
		String csmConfigFile, String callerId, String csmContextName, CQLQuery query) 
		throws RemoteException {
		if (csmConfigFile == null || csmConfigFile.trim().length() == 0) {
			throw new RemoteException("No CSM Configuration file was specified.");
		} else {
			// here's why its synchronized...
			System.setProperty(CSM_CONFIG_PROPERTY, csmConfigFile);
		}
		GridAuthorizationManager mgr = new CSMGridAuthorizationManager(csmContextName);
		List<String> authObjects = new LinkedList<String>();
		populateObjectsToAuthorize(query.getTarget(), authObjects);
		Iterator authObjectIter = authObjects.iterator();
		while (authObjectIter.hasNext()) {
			String objectName = (String) authObjectIter.next();
			// TODO: verify I can call this multiple times without problems
			// also a TODO: Should first object be READ and all others be ACCESS?
			if (!mgr.isAuthorized(callerId, objectName, CSM_PRIVILEGE)) {
				return false;
			}
		}
		return true;
	}
	
	
	private static void populateObjectsToAuthorize(Object queryObject, List<String> objects) {
		objects.add(queryObject.getName());
		if (queryObject.getAssociation() != null) {
			populateObjectsToAuthorize(queryObject.getAssociation(), objects);
		}
		if (queryObject.getGroup() != null && queryObject.getGroup().getAssociation() != null) {
			Association[] associations = queryObject.getGroup().getAssociation();
			for (int i = 0; i < associations.length; i++) {
				populateObjectsToAuthorize(associations[i], objects);
			}
		}
	}
}
