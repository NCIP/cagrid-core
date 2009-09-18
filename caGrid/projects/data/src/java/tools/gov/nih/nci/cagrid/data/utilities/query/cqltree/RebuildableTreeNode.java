package gov.nih.nci.cagrid.data.utilities.query.cqltree;

/** 
 *  RebuildableTreeNode
 *  Tree node that knows how to rebuild its children given a change to the backend model
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public interface RebuildableTreeNode {

	public void rebuild() throws IllegalStateException;
}
