package gov.nih.nci.cagrid.data.ui.tree.types;

import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/** 
 *  TreeTester
 *  TODO:DOCUMENT ME
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 21, 2006 
 * @version $Id: TreeTester.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class TreeTester extends JFrame {
	private TargetTypesTree tree = null;
	private JScrollPane treeScrollPane = null;
	
	public TreeTester() {
		super();
		initialize();
	}
	
	
	private void initialize() {
		this.setContentPane(getJScrollPane());
		pack();
		this.setSize(new java.awt.Dimension(332,283));
		setVisible(true);
	}
	
	
	private TargetTypesTree getTree() {
		if (tree == null) {
			tree = new TargetTypesTree();
			tree.addNamespaceType(getNamespaces()[0]);
			tree.addNamespaceType(getNamespaces()[1]);
		}
		return tree;
	}
	
	
	private NamespaceType[] getNamespaces() {
		NamespaceType[] ns = new NamespaceType[2];
		NamespaceType bs = new NamespaceType();
		bs.setNamespace("projectmobius.org/1/BookStore");
		bs.setPackageName("org.projectmobius");
		bs.setLocation(".");
		SchemaElementType[] types = new SchemaElementType[4];
		for (int i = 0; i < types.length; i++) {
			SchemaElementType type = new SchemaElementType();
			type.setClassName("Book" + i);
			type.setDeserializer("FakeDeserializer");
			type.setSerializer("FakeSerializer");
			type.setType("Book" + i);
			types[i] = type;
		}
		bs.setSchemaElement(types);
		ns[0] = bs;
		NamespaceType foo = new NamespaceType();
		foo.setNamespace("foo.bar.org/1/Zor");
		foo.setPackageName("org.bar.foo");
		foo.setLocation(".");
		SchemaElementType[] types2 = new SchemaElementType[4];
		for (int i = 0; i < types2.length; i++) {
			SchemaElementType type = new SchemaElementType();
			type.setClassName("Foo" + i);
			type.setDeserializer("FakeDeserializer");
			type.setSerializer("FakeSerializer");
			type.setType("Foo" + i);
			types2[i] = type;
		}
		foo.setSchemaElement(types2);
		ns[1] = foo;
		return ns;
	}
	

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (treeScrollPane == null) {
			treeScrollPane = new JScrollPane();
			treeScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Tree!", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			treeScrollPane.setViewportView(getTree());
		}
		return treeScrollPane;
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		JFrame tester = new TreeTester();
		tester.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
