
package org.cagrid.gaards.ui.gridgrouper.tree;

import javax.swing.ImageIcon;



/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */

public abstract class GridGrouperBaseTreeNode extends BaseTreeNode {
	
	private static final long serialVersionUID = 1L;

	public GridGrouperBaseTreeNode(GridGrouperTree tree) {
		super(tree);
	}

	public abstract ImageIcon getIcon();


	public abstract String toString();


	public abstract void refresh();

}
