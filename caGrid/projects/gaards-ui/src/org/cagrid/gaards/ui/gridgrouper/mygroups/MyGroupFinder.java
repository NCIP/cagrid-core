package org.cagrid.gaards.ui.gridgrouper.mygroups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperSession;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class MyGroupFinder extends Runner {

    private GridGrouperSession session;
    private MyGroupsTable groupsTable;
    private boolean isSuccessful = false;
    private String error = null;


    public MyGroupFinder(GridGrouperSession session, MyGroupsTable groupsTable) {
        this.session = session;
        this.groupsTable = groupsTable;

    }


    public void execute() {
        try {
            String targetIdentity = session.getIdentity();
            GridGrouper gridGrouper = session.getClient();
            groupsTable.addGroups(gridGrouper.getMembersGroups(targetIdentity));
            isSuccessful = true;
        } catch (Exception e) {
            error = Utils.getExceptionMessage(e);
            if ((error.indexOf("Operation name could not be determined") >= 0)) {
                error = "The Grid Grouper service maybe an older version which does not support looking up a member's groups.";
            }
        }
    }


    public String getGridGrouperURI() {
        return session.getServiceURL();
    }


    public boolean isSuccessful() {
        return isSuccessful;
    }


    public String getError() {
        return error;
    }

}
