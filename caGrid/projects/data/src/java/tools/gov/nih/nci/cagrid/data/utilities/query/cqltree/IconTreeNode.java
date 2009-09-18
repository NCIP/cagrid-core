package gov.nih.nci.cagrid.data.utilities.query.cqltree;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/** 
 *  IconTreeNode
 *  TODO:DOCUMENT ME
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public abstract class IconTreeNode extends DefaultMutableTreeNode implements RebuildableTreeNode {

	public abstract Icon getIcon();
}
