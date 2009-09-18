package org.cagrid.gaards.ui.gridgrouper.browser;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;



/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ContentManagerTabCloseIcon implements Icon {
	private final Icon mIcon;

	private ContentManager mTabbedPane = null;

	private transient Rectangle mPosition = null;


	/**
	 * Creates a new instance of TabCloseIcon.
	 */
	public ContentManagerTabCloseIcon(Icon icon) {
		mIcon = icon;
	}


	/**
	 * Creates a new instance of TabCloseIcon.
	 */
	public ContentManagerTabCloseIcon() {
		this(GridGrouperLookAndFeel.getCloseTab());
	}


	/**
	 * when painting, remember last position painted.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (null == mTabbedPane) {
			mTabbedPane = (ContentManager) c;
			mTabbedPane.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					// asking for isConsumed is *very* important, otherwise more
					// than one tab might get closed!
					if (!e.isConsumed() && mPosition.contains(e.getX(), e.getY())) {
						mTabbedPane.removeSelectedNode();
						mTabbedPane.removeMouseListener(this);
					}
				}
			});
		}

		mPosition = new Rectangle(x, y, getIconWidth(), getIconHeight());
		mIcon.paintIcon(c, g, x, y);
	}


	/**
	 * just delegate
	 */
	public int getIconWidth() {
		return mIcon.getIconWidth();
	}


	/**
	 * just delegate
	 */
	public int getIconHeight() {
		return mIcon.getIconHeight();
	}

}
