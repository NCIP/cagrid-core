package org.cagrid.gaards.ui.gridgrouper.expressioneditor;


import gov.nih.nci.cagrid.common.Runner;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public abstract class GridGrouperTreeNodeMenu extends JPopupMenu {
	
	private static final long serialVersionUID = 1L;
	
	private GridGrouperExpressionEditor editor;

	private GridGrouperTree tree;

	private JMenuItem refresh = null;


	public GridGrouperTreeNodeMenu(GridGrouperExpressionEditor browser, GridGrouperTree tree) {
		super("");
		this.editor = browser;
		this.tree = tree;
		initialize();

	}


	/**
	 * This method initializes this
	 */
	private void initialize() {

		this.add(getRefresh());

	}


	public GridGrouperExpressionEditor getEditor() {
		return editor;
	}


	public GridGrouperTree getGridGrouperTree() {
		return tree;
	}


	/**
	 * This method initializes refresh
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getRefresh() {
		if (refresh == null) {
			refresh = new JMenuItem();
			refresh.setText("Refresh");
			refresh.setIcon(GridGrouperLookAndFeel.getLoadIcon());
			refresh.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							getGridGrouperTree().getCurrentNode().refresh();
						}
					};
					try {
						Util.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}

			});
		}
		return refresh;
	}

}
