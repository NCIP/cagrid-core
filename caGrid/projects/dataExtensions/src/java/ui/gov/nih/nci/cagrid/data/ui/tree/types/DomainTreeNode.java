package gov.nih.nci.cagrid.data.ui.tree.types;

import gov.nih.nci.cagrid.data.ui.tree.CheckBoxTreeNode;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.tree.DefaultTreeModel;


/** 
 *  DomainTreeNode
 *  Node in the TargetTypesTree to represent a domain model.  
 *  Children are all TypeTreeNodes
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 20, 2006 
 * @version $Id: DomainTreeNode.java,v 1.2 2008-01-02 19:32:08 dervin Exp $ 
 */
public class DomainTreeNode extends CheckBoxTreeNode {
	
	private NamespaceType namespace;
	private TargetTypesTree parentTree;
	private Map<JCheckBox, SchemaElementType> checkBoxTypes;
	private Map<SchemaElementType, JCheckBox> typeCheckBoxes;

	public DomainTreeNode(TargetTypesTree tree, NamespaceType namespace) {
		super(tree, namespace.getNamespace());
		this.parentTree = tree;
		this.namespace = namespace;
		this.checkBoxTypes = new HashMap<JCheckBox, SchemaElementType>();
		this.typeCheckBoxes = new HashMap<SchemaElementType, JCheckBox>();
		
		// add child nodes
		SchemaElementType[] types = namespace.getSchemaElement();
		if (types != null) {
			// add the nodes
			for (int i = 0; i < types.length; i++) {
				TypeTreeNode node = new TypeTreeNode(tree, types[i]);
				checkBoxTypes.put(node.getCheckBox(), node.getType());
				typeCheckBoxes.put(node.getType(), node.getCheckBox());
				add(node);
			}
		}
		// add listener to turn all children's check boxes on / off
		getCheckBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int childCount = getChildCount();
				for (int i = 0; i < childCount; i++) {
					TypeTreeNode node = (TypeTreeNode) getChildAt(i);
					node.getCheckBox().setSelected(isChecked());
					((DefaultTreeModel) parentTree.getModel()).nodeChanged(node);
				}
			}
		});
		// repaint the node when it changes
		getCheckBox().addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				((DefaultTreeModel) parentTree.getModel()).nodeChanged(DomainTreeNode.this);
			}
		});
	}
	
	
	public NamespaceType getNamespace() {
		return this.namespace;
	}
	
	
	public void checkTypeNodes(SchemaElementType[] types) {
		for (int i = 0; i < types.length; i++) {
			JCheckBox check = typeCheckBoxes.get(types[i]);
			if (check != null) {
				check.setSelected(true);
			}
		}
	}
	
	
	public boolean allChildrenChecked() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			TypeTreeNode node = (TypeTreeNode) getChildAt(i);
			if (!node.isChecked()) {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean noChildrenChecked() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			TypeTreeNode node = (TypeTreeNode) getChildAt(i);
			if (node.isChecked()) {
				return false;
			}
		}
		return true;
	}
}
