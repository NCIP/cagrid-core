package org.cagrid.gaards.ui.gridgrouper.browser;

import javax.swing.JMenuItem;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.gaards.ui.gridgrouper.tree.AddGroupWindow;
import org.cagrid.gaards.ui.gridgrouper.tree.AddStemWindow;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperBaseTreeNode;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.gaards.ui.gridgrouper.tree.StemTreeNode;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class StemNodeMenu extends GridGrouperTreeNodeMenu {

    private static final long serialVersionUID = 1L;

    private JMenuItem addStem = null;

    private JMenuItem addGroup = null;


    public StemNodeMenu(GroupManagementBrowser browser, GridGrouperTree tree) {
        super(browser, tree);
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(getAddStem());
        this.add(getAddGroup());
    }


    private StemTreeNode getStemNode() {
        return (StemTreeNode) getGridGrouperTree().getCurrentNode();
    }


    public void removeNode() {
        StemTreeNode node = getStemNode();
        if (node.getChildCount() > 0) {
            ErrorDialog.showError("Cannot remove stem with child stems or child groups!!!");
            return;
        }

        if (node.isRootStem()) {
            ErrorDialog.showError("Cannot remove root stem!!!");
            return;
        }
        int id = getBrowser().getProgress().startEvent("Removing the stem.... ");
        try {
            node.getStem().delete();
            getBrowser().getContentManager().removeStem(node);
            ((GridGrouperBaseTreeNode) node.getParent()).refresh();
            getBrowser().getProgress().stopEvent(id, "Successfully removed the stem !!!");
        } catch (Exception e) {
            getBrowser().getProgress().stopEvent(id, "Error removing the stem !!!");
            ErrorDialog.showError(e);
        }
    }


    /**
     * This method initializes addStem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAddStem() {
        if (addStem == null) {
            addStem = new JMenuItem();
            addStem.setIcon(GridGrouperLookAndFeel.getStemIcon22x22());
            addStem.setText("Add Stem");
            addStem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    GridApplication.getContext().addApplicationComponent(
                        new AddStemWindow((StemTreeNode) getGridGrouperTree().getCurrentNode()), 700, 300);
                }
            });
        }
        return addStem;
    }


    /**
     * This method initializes addGroup
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAddGroup() {
        if (addGroup == null) {
            addGroup = new JMenuItem();
            addGroup.setText("Add Group");
            addGroup.setIcon(GridGrouperLookAndFeel.getGroupIcon22x22());
            addGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    GridApplication.getContext().addApplicationComponent(
                        new AddGroupWindow((StemTreeNode) getGridGrouperTree().getCurrentNode()), 700, 300);
                }
            });
        }
        return addGroup;
    }


    protected void toggleAddGroup(boolean toggle) {
        getAddGroup().setEnabled(toggle);
    }
}
