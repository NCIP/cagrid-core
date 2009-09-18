package gov.nih.nci.cagrid.data.ui.tree.uml;

import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/** 
 *  UMLProjectTree
 *  Tree for describing and selecting items from caDSR UML Projects
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 5, 2006 
 * @version $Id: UMLProjectTree.java,v 1.3 2008-01-02 19:32:08 dervin Exp $ 
 */
public class UMLProjectTree extends CheckBoxTree {
	private Map<String, UMLPackageTreeNode> packageNodes;

	public UMLProjectTree() {
		super();
		packageNodes = new HashMap<String, UMLPackageTreeNode>();
	}
    
    
    public Set<String> getPackagesInTree() {
        Set<String> packageNames = new HashSet<String>();
        packageNames.addAll(packageNodes.keySet());
        return packageNames;
    }
	
	
	public UMLPackageTreeNode addUmlPackage(String packageName) {
		UMLPackageTreeNode packNode = new UMLPackageTreeNode(this, packageName);
		packageNodes.put(packageName, packNode);
		getRootNode().add(packNode);
		reloadFromRoot();
		return packNode;
	}
	
	
	public UMLPackageTreeNode getUmlPackageNode(String packageName) {
        return packageNodes.get(packageName);
	}
	
	
	public void removeUmlPackage(String packageName) {
		UMLPackageTreeNode packNode = packageNodes.remove(packageName);
		if (packNode == null) {
			throw new IllegalArgumentException("Package " + packageName + " is not in this tree!");
		}
		getRootNode().remove(packNode);
		reloadFromRoot();
	}
	
	
	public UMLClassTreeNode addUmlClass(String packageName, String className) {
		// find the packag node
		UMLPackageTreeNode packNode = packageNodes.get(packageName);
		if (packNode == null) {
			throw new IllegalArgumentException("Package " + packageName + " is not in this tree!");
		}
		UMLClassTreeNode classNode = new UMLClassTreeNode(this, className);
		packNode.add(classNode);
		reloadFromRoot();
		return classNode;
	}
	
	
	public UMLClassTreeNode getUmlClassNode(String packageName, String className) {
		UMLPackageTreeNode packNode = getUmlPackageNode(packageName);
		if (packNode != null) {
			Enumeration classNodes = packNode.children();
			while (classNodes.hasMoreElements()) {
				UMLClassTreeNode classNode = (UMLClassTreeNode) classNodes.nextElement();
				if (classNode.getClassName().equals(className)) {
					return classNode;
				}
			}
		}
		return null;
	}
	
	
	public void removeUmlClass(String packageName, String className) {
		// find the package node
		UMLPackageTreeNode packNode = packageNodes.get(packageName);
		if (packNode == null) {
			throw new IllegalArgumentException("Package " + packageName + " is not in this tree!");
		}
		// find the class node
		UMLClassTreeNode classNode = null;
		Enumeration classNodeEnum = packNode.children();
		while (classNodeEnum.hasMoreElements()) {
			UMLClassTreeNode node = (UMLClassTreeNode) classNodeEnum.nextElement();
			if (node.getClassName().equals(className)) {
				classNode = node;
				break;
			}
		}
		if (classNode == null) {
			throw new IllegalArgumentException("Class " + className + " is not in this tree!");
		}
		packNode.remove(classNode);
		reloadFromRoot();
	}
	
	
	public String[] getSelectedClassNames(String packageName) {
		List<String> names = new ArrayList<String>();
		UMLPackageTreeNode packNode = getUmlPackageNode(packageName);
		Enumeration classNodes = packNode.children();
		while (classNodes.hasMoreElements()) {
			UMLClassTreeNode classNode = (UMLClassTreeNode) classNodes.nextElement();
			names.add(classNode.getClassName());
		}
		String[] nameArray = new String[names.size()];
		names.toArray(nameArray);
		return nameArray;
	}
    
    
    public void expandFullTree() {
        DefaultMutableTreeNode mostRecentNode = null;
        if (getRootNode() != null) {
            Enumeration nodes = getRootNode().breadthFirstEnumeration();
            while (nodes.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
                if (!node.isNodeSibling(mostRecentNode)) {
                    expandPath(new TreePath(node.getPath()));
                }
                mostRecentNode = node;
            }
        }
    }
}
