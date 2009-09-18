
package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import gov.nih.nci.cagrid.gridgrouper.bean.LogicalOperator;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipQuery;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipStatus;
import gov.nih.nci.cagrid.gridgrouper.client.Group;

import javax.swing.ImageIcon;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ExpressionNode extends ExpressionBaseTreeNode {
	
	private static final long serialVersionUID = 1L;

	private MembershipExpression expression;

	private boolean rootStem;


	public ExpressionNode(GridGrouperExpressionEditor editor, MembershipExpression expression, boolean root) {
		super(editor);
		this.rootStem = root;
		this.expression = expression;
	}


	public synchronized void clearExpression() {
		expression.setMembershipExpression(null);
		expression.setMembershipQuery(null);
		super.removeAllChildren();
	}


	public synchronized void resetExpression(MembershipExpression exp) {
		super.removeAllChildren();
		this.expression = exp;
		this.refresh();
	}


	public void loadExpression() {
		this.removeAllChildren();
		MembershipExpression[] exps = this.expression.getMembershipExpression();
		if (exps != null) {
			for (int i = 0; i < exps.length; i++) {
				ExpressionNode node = new ExpressionNode(getEditor(), exps[i], false);
				synchronized (getTree()) {
					this.add(node);
					TreeNode parentNode = this.getParent();
					if (parentNode != null) {
						getTree().reload(parentNode);
					} else {
						getTree().reload();
					}
				}
				node.loadExpression();
			}
		}
		MembershipQuery[] queries = this.expression.getMembershipQuery();
		if (queries != null) {
			for (int i = 0; i < queries.length; i++) {
				QueryNode node = new QueryNode(getEditor(), queries[i]);
				synchronized (getTree()) {
					this.add(node);
					TreeNode parentNode = this.getParent();
					if (parentNode != null) {
						getTree().reload(parentNode);
					} else {
						getTree().reload();
					}
				}
			}
		}
	}


	public void addGroup(Group grp) {
		MembershipQuery[] mq = expression.getMembershipQuery();
		int size = 0;
		if (mq != null) {
			size = mq.length;
		}
		MembershipQuery[] nmq = new MembershipQuery[size + 1];
		for (int i = 0; i < size; i++) {
			if (mq[i].getGroupIdentifier().equals(grp.getGroupIdentifier())) {
				Util.showErrorMessage("The group " + grp.getDisplayName() + " has already exists in the expression!!!");
				return;
			}
			nmq[i] = mq[i];
		}
		nmq[size] = new MembershipQuery();
		nmq[size].setGroupIdentifier(grp.getGroupIdentifier());
		nmq[size].setMembershipStatus(MembershipStatus.MEMBER_OF);
		expression.setMembershipQuery(nmq);
		loadExpression();
	}


	public void addAndExpression() {
		MembershipExpression exp = new MembershipExpression();
		exp.setLogicRelation(LogicalOperator.AND);
		addExpression(exp);
	}


	public void addOrExpression() {
		MembershipExpression exp = new MembershipExpression();
		exp.setLogicRelation(LogicalOperator.OR);
		addExpression(exp);
	}


	public void addExpression(MembershipExpression exp) {
		MembershipExpression[] me = expression.getMembershipExpression();
		int size = 0;
		if (me != null) {
			size = me.length;
		}
		MembershipExpression[] nme = new MembershipExpression[size + 1];
		for (int i = 0; i < size; i++) {
			nme[i] = me[i];
		}
		nme[size] = exp;
		expression.setMembershipExpression(nme);
		loadExpression();
	}


	public void removeExpression(MembershipExpression exp) {
		MembershipExpression[] me = expression.getMembershipExpression();
		MembershipExpression[] nme = new MembershipExpression[me.length - 1];
		if (me.length == 1) {
			expression.setMembershipExpression(null);
			refresh();
			return;
		} else {
			int count = 0;
			for (int i = 0; i < me.length; i++) {
				if (me[i] != exp) {
					nme[count] = me[i];
					count = count + 1;
				}
			}
			expression.setMembershipExpression(nme);
			TreePath currentSelection = getEditor().getExpressionTree().getSelectionPath();
			getEditor().getExpressionTree().setSelectionPath(currentSelection.getParentPath());
			loadExpression();
		}
	}


	public void removeGroup(MembershipQuery query) {
		MembershipQuery[] qe = expression.getMembershipQuery();
		MembershipQuery[] nqe = new MembershipQuery[qe.length - 1];
		if (qe.length == 1) {
			expression.setMembershipQuery(null);
			refresh();
			return;
		} else {
			int count = 0;
			for (int i = 0; i < qe.length; i++) {
				if (qe[i] != query) {
					nqe[count] = qe[i];
					count = count + 1;
				}
			}
			expression.setMembershipQuery(nqe);
			TreePath currentSelection = getEditor().getExpressionTree().getSelectionPath();
			getEditor().getExpressionTree().setSelectionPath(currentSelection.getParentPath());
			loadExpression();
		}
	}


	public void refresh() {
		loadExpression();
		TreeNode parentNode = this.getParent();
		if (parentNode != null) {
			getTree().reload(parentNode);
		} else {
			getTree().reload();
		}
	}


	public ImageIcon getIcon() {
		return GridGrouperLookAndFeel.getMembershipExpressionIcon16x16();
	}


	public String toString() {
		return expression.getLogicRelation().getValue();
	}


	public boolean isRootExpression() {
		return rootStem;
	}


	public MembershipExpression getExpression() {
		return expression;
	}

}
