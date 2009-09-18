package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;
import gov.nih.nci.cagrid.gridgrouper.grouper.NamingPrivilegeI;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;

import java.util.Iterator;
import java.util.Set;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGrouperExample {

	public static void main(String[] args) {
		System.out.println("Running the Grid Service Client");
		try {

			GridGrouper grouper = new GridGrouper("https://localhost:8443/wsrf/services/cagrid/GridGrouper");
			StemI stem = grouper.getRootStem();
			printStems(stem, "");

			System.out.println();
			System.out.println();
			System.out.println("Updating.............");
			System.out.println();
			System.out.println();
			updateStems(stem, " [Updated]", "This is a stem!!!");
			printStems(stem, "");

			System.out.println();
			System.out.println();
			System.out.println("Reseting.............");
			System.out.println();
			System.out.println();
			updateStems(stem, "", "");
			printStems(stem, "");

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	public static void updateStems(StemI stem, String displayExtension, String desrciption) throws Exception {
		String dn = stem.getDisplayExtension();
		int index = dn.indexOf(" [Updated]");
		String childExtension = "CHILD";
		String childDisplayExtension = "Test Child";
		boolean isReset = false;
		if (index != -1) {
			dn = dn.substring(0, index);
			isReset = true;
		}
		stem.setDisplayExtension(dn + displayExtension);
		stem.setDescription(desrciption);
		Set s = stem.getChildStems();
		Iterator itr = s.iterator();
		while (itr.hasNext()) {
			updateStems((StemI) itr.next(), displayExtension, desrciption);
		}
		Subject creator = SubjectUtils.getSubject("/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN="
			+ stem.getUuid() + " Creator");
		Subject stemmer = SubjectUtils.getSubject("/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN="
			+ stem.getUuid() + " Stemmer");
		if (isReset) {
			stem.revokePriv(creator, edu.internet2.middleware.grouper.NamingPrivilege.CREATE);
			stem.revokePriv(stemmer, edu.internet2.middleware.grouper.NamingPrivilege.STEM);
		} else {
			stem.grantPriv(creator, edu.internet2.middleware.grouper.NamingPrivilege.CREATE);
			stem.grantPriv(stemmer, edu.internet2.middleware.grouper.NamingPrivilege.STEM);
		}

		if ((!isReset) && (!stem.getExtension().equals(childExtension))) {
			System.out.println("Adding child to the stem " + stem.getDisplayName());
			stem.addChildStem(childExtension, childDisplayExtension);
		}
		if (stem.getExtension().equals(childExtension)) {
			System.out.println("Deleting the stem " + stem.getDisplayName());
			stem.delete();
		}

	}


	public static void printStems(StemI stem, String buffer) throws Exception {
		System.out.println(buffer + stem.getDisplayExtension() + " (" + stem.getUuid() + ")");
		System.out.println(buffer + "  " + "Description:" + stem.getDescription());
		try {
			System.out.println(buffer + "  " + "Parent:" + stem.getParentStem().getDisplayExtension());
		} catch (Exception e) {
		}
		System.out.println(buffer + "  " + "Create Source:" + stem.getCreateSource());
		System.out.println(buffer + "  " + "Create Subject Id:" + stem.getCreateSubject().getId());
		System.out.println(buffer + "  " + "Create Time:" + stem.getCreateTime());
		System.out.println(buffer + "  " + "Modify Time:" + stem.getModifyTime());

		Set stemmers = stem.getStemmers();
		System.out.println(buffer + "  " + "Stemmers:");
		Iterator i2 = stemmers.iterator();
		while (i2.hasNext()) {
			Subject sbj = (Subject) i2.next();
			System.out.println(buffer + "    " + sbj.getId());
		}

		Set creators = stem.getCreators();
		System.out.println(buffer + "  " + "Creators:");
		Iterator i1 = creators.iterator();
		while (i1.hasNext()) {
			Subject sbj = (Subject) i1.next();
			System.out.println(buffer + "    " + sbj.getId());
		}
		Subject sub = SubjectUtils.getSubject("/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=langella");
		Set privs = stem.getPrivs(sub);
		System.out.println(buffer + "  " + "Privileges for " + sub.getId() + ":");
		Iterator i3 = privs.iterator();
		while (i3.hasNext()) {
			NamingPrivilegeI priv = (NamingPrivilegeI) i3.next();
			System.out.println(buffer + "    " + priv.toString());
		}

		System.out.println(buffer + "  Has Create Privilege [" + sub.getId() + "]?:" + stem.hasCreate(sub));
		System.out.println(buffer + "  Has Stem Privilege [" + sub.getId() + "]?:" + stem.hasStem(sub));

		Set s = stem.getChildStems();
		Iterator itr = s.iterator();
		while (itr.hasNext()) {
			System.out.println();
			System.out.println();
			printStems((StemI) itr.next(), buffer);

		}
	}

}
