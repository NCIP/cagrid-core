package gov.nih.nci.cagrid.data.ui.tree.types;

import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTree;
import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTreeNode;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;

import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/** 
 *  TargetTypesTree
 *  Tree for showing and selecting types to expose via a data service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 20, 2006 
 * @version $Id: TargetTypesTree.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class TargetTypesTree extends CheckBoxTree {
	
	public TargetTypesTree() {
		super();
	}
	
	
	public void addNamespaceType(NamespaceType ns) {
		DomainTreeNode domainNode = new DomainTreeNode(this, ns);
		getRootNode().add(domainNode);
		getTreeModel().reload(getRootNode());
		if (domainNode.getChildCount() != 0) {
			TreeNode child = domainNode.getChildAt(0);
			TreePath path = new TreePath(getTreeModel().getPathToRoot(child));
			makeVisible(path);
		}
	}
	
	
	public void removeNamespaceType(String namespace) {
		Enumeration nsNodeEnumeration = getRootNode().children();
		DomainTreeNode removeMe = null;
		while (nsNodeEnumeration.hasMoreElements()) {
			DomainTreeNode nsNode = (DomainTreeNode) nsNodeEnumeration.nextElement();
			if (nsNode.getNamespace().getNamespace().equals(namespace)) {
				removeMe = nsNode;
				break;
			}
		}
		if (removeMe != null) {
			setSelectionRow(0);
			final DomainTreeNode node = removeMe;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// remove all checked namespaces from the node
					Enumeration typeNodes = node.children();
					while (typeNodes.hasMoreElements()) {
						((TypeTreeNode) typeNodes.nextElement()).getCheckBox().setSelected(false);
					}
					getTreeModel().removeNodeFromParent(node);			
				}
			});
		}
		reloadFromRoot();
	}
	
	
	public void checkTypeNodes(NamespaceType ns, SchemaElementType[] types) {
		for (int i = 0; i < getRootNode().getChildCount(); i++) {
			DomainTreeNode domainNode = (DomainTreeNode) getRootNode().getChildAt(i);
			if (domainNode.getNamespace().equals(ns)) {
				domainNode.checkTypeNodes(types);
				break;
			}
		}
	}
	
	
	public NamespaceType[] getNamespaceTypes() {
		NamespaceType[] namespaces = new NamespaceType[getRootNode().getChildCount()];
		for (int i = 0; i < getRootNode().getChildCount(); i++) {
			namespaces[i] = ((DomainTreeNode) getRootNode().getChildAt(i)).getNamespace();
		}
		return namespaces;
	}
	
	
	public SchemaElementType[] getCheckedTypes(NamespaceType namespace) {
		// find the namespace type node
		DomainTreeNode namespaceNode = null;
		Enumeration nsTypeNodesEnum = getRootNode().children();
		while (nsTypeNodesEnum.hasMoreElements()) {
			DomainTreeNode typeNode = (DomainTreeNode) nsTypeNodesEnum.nextElement();
			if (typeNode.getNamespace().equals(namespace)) {
				namespaceNode = typeNode;
				break;
			}
		}
		if (namespaceNode != null) {
			CheckBoxTreeNode[] checkedNodes = namespaceNode.getCheckedChildren();
			SchemaElementType[] typeArray = new SchemaElementType[checkedNodes.length];
			for (int i = 0; i < typeArray.length; i++) {
				typeArray[i] = ((TypeTreeNode) checkedNodes[i]).getType();
			}
			return typeArray;
		}
		return null;
	}
}
