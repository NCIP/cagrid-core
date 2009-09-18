
package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipQuery;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipStatus;

import javax.swing.ImageIcon;
import javax.swing.tree.TreeNode;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class QueryNode extends ExpressionBaseTreeNode {

	private static final long serialVersionUID = 1L;
	
	private MembershipQuery query;

	public QueryNode(GridGrouperExpressionEditor editor, MembershipQuery query) {
		super(editor);
		this.query = query;
	}

	public void refresh() {
		TreeNode parentNode = this.getParent();
		if (parentNode != null) {
			getTree().reload(parentNode);
		} else {
			getTree().reload();
		}
	}

	public ImageIcon getIcon() {
		return GridGrouperLookAndFeel.getGroupIcon16x16();
	}

	public String toString() {
		if ((query.getMembershipStatus() == null)
				|| (query.getMembershipStatus()
						.equals(MembershipStatus.MEMBER_OF))) {
			return query.getGroupIdentifier().getGroupName() + " [Member]";
		} else {
			return query.getGroupIdentifier().getGroupName() + " [Not Member]";
		}

	}

	public MembershipQuery getQuery() {
		return query;
	}

}
