package org.cagrid.gaards.ui.gridgrouper;

import javax.swing.ImageIcon;

import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.IconUtils;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGrouperLookAndFeel extends LookAndFeel {
	public final static ImageIcon getGrouperIcon22x22() {
		return IconUtils.loadIcon("/grouper_logo_22x22.png");
	}


	public final static ImageIcon getGrouperIconNoBackground() {
		return IconUtils.loadIcon("/grouper_logo_no_background.png");
	}


	public final static ImageIcon getGrouperIconNoBackground22X22() {
		return IconUtils.loadIcon("/grouper_logo_no_background_22x22.png");
	}


	public final static ImageIcon getGrouperAddIcon22x22() {
		return IconUtils.loadIcon("/grouper_add_22x22.png");
	}


	public final static ImageIcon getGrouperRemoveIcon22x22() {
		return IconUtils.loadIcon("/grouper_remove_22x22.png");
	}


	public final static ImageIcon getGridGrouperServicesIcon16x16() {
		return IconUtils.loadIcon("/applications-internet-16x16.png");
	}


	public final static ImageIcon getMembershipExpressionIcon16x16() {
		return IconUtils.loadIcon("/edit-find-16x16.png");
	}


	public final static ImageIcon getGrouperIcon16x16() {
		return IconUtils.loadIcon("/grouper_logo_16x16.png");
	}


	public final static ImageIcon getLoadIcon() {
		return IconUtils.loadIcon("/view-refresh.png");
	}


	public final static ImageIcon getStemIcon16x16() {
		return IconUtils.loadIcon("/chart_organisation_16x16.png");
	}


	public final static ImageIcon getStemIcon22x22() {
		return IconUtils.loadIcon("/chart_organisation_22x22.png");
	}


	public final static ImageIcon getCloseTab() {
		return IconUtils.loadIcon("/closeTab.gif");
	}


	public final static ImageIcon getPrivilegesIcon() {
		return IconUtils.loadIcon("/bookmark-new.png");
	}


	public final static ImageIcon getGroupIcon22x22() {
		return IconUtils.loadIcon("/system-users-22x22.png");
	}


	public final static ImageIcon getGroupIcon16x16() {
		return IconUtils.loadIcon("/system-users-16x16.png");
	}


	public final static ImageIcon getMemberIcon22x22() {
		return IconUtils.loadIcon("/system-users-22x22.png");
	}


	public final static ImageIcon getMemberIcon16x16() {
		return IconUtils.loadIcon("/system-users-16x16.png");
	}


	public final static ImageIcon getDetailsIcon() {
		return IconUtils.loadIcon("/Inform.gif");
	}
}
