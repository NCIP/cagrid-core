package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.GroupNotFoundException;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipQuery;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.globus.gsi.GlobusCredential;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */

public class GridGrouperClientUtils {

	public static final QName EXPRESSION_NAMESPACE = new QName("http://cagrid.nci.nih.gov/1/GridGrouper",
		"MembershipExpression");


	public static MembershipExpression xmlToExpression(String xml) throws Exception {
		StringReader reader = new StringReader(xml);
		MembershipExpression exp = (MembershipExpression) Utils.deserializeObject(reader, MembershipExpression.class);
		reader.close();
		return exp;
	}


	public static String expressionToXML(MembershipExpression exp) throws Exception {
		StringWriter sw = new StringWriter();
		Utils.serializeObject(exp, EXPRESSION_NAMESPACE, sw);
		return sw.toString();
	}


	public static boolean isMember(GlobusCredential cred, String xml, String memberId) throws Exception {
		return isMember(cred, xmlToExpression(xml), memberId);
	}


	public static boolean isMember(String xml, String memberId) throws Exception {
		return isMember(null, xml, memberId);
	}


	public static boolean isMember(GlobusCredential cred, MembershipExpression exp, String memberId) throws Exception {
		String uri = findGridGrouperURI(exp);
		if (uri == null) {
			throw new Exception("No Grid Grouper URI found in the expression!!!");
		}
		GridGrouper client = new GridGrouper(uri, cred);
		return client.isMember(memberId, exp);
	}


	public static String findGridGrouperURI(MembershipExpression exp) {
		MembershipQuery[] mq = exp.getMembershipQuery();
		if ((mq != null) && (mq.length > 0)) {
			if (mq[0].getGroupIdentifier().getGridGrouperURL() != null) {
				return mq[0].getGroupIdentifier().getGridGrouperURL();
			}
		}
		MembershipExpression[] list = exp.getMembershipExpression();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				return findGridGrouperURI(list[i]);
			}
		}
		return null;
	}


	public static boolean isMember(MembershipExpression exp, String memberId) throws Exception {
		return isMember(null, exp, memberId);
	}


	public static boolean isMemberOf(String gridGrouperURI, GlobusCredential cred, String groupName, String memberId)
		throws GroupNotFoundException {
		GridGrouper client = new GridGrouper(gridGrouperURI, cred);
		return client.isMemberOf(memberId, groupName);
	}


	public static boolean isMemberOf(String gridGrouperURI, String groupName, String memberId)
		throws GroupNotFoundException {
		return isMemberOf(gridGrouperURI, null, groupName, memberId);
	}

}
