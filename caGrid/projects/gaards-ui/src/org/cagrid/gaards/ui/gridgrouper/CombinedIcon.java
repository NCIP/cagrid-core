package org.cagrid.gaards.ui.gridgrouper;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class CombinedIcon implements Icon {

	private final Icon mIcon1;

	private final Icon mIcon2;

	private final static int SPACE = 2;


	/**
	 * Creates a new instance of CombinedIcon.
	 */
	public CombinedIcon(Icon icon1, Icon icon2) {
		mIcon1 = icon1;
		mIcon2 = icon2;
	}


	public void paintIcon(Component c, Graphics g, int x, int y) {
		mIcon1.paintIcon(c, g, x, y);
		mIcon2.paintIcon(c, g, x + mIcon1.getIconWidth() + SPACE, y);
	}


	public int getIconWidth() {
		return mIcon1.getIconWidth() + SPACE + mIcon2.getIconWidth();
	}


	public int getIconHeight() {
		int h1 = mIcon1.getIconHeight();
		int h2 = mIcon2.getIconHeight();
		return h1 > h2 ? h1 : h2;
	}

}