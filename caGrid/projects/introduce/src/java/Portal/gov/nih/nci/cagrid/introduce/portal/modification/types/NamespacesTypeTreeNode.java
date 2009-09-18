
package gov.nih.nci.cagrid.introduce.portal.modification.types;

import javax.swing.tree.DefaultMutableTreeNode;

/** 
 *  Node for representing namepspace
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * 
 * @created Nov 22, 2004 
 * @version $Id: NamespacesTypeTreeNode.java,v 1.4 2006-04-05 18:17:49 hastings Exp $ 
 */
public class NamespacesTypeTreeNode extends DefaultMutableTreeNode {

	public NamespacesTypeTreeNode() {
		super();
		this.setUserObject("Data Types");
	}
	
	public String toString(){
		return this.getUserObject().toString();
	}
}
