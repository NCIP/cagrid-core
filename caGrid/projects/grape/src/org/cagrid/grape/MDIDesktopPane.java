package org.cagrid.grape;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.Border;

import org.cagrid.grape.model.Dimensions;
import org.cagrid.grape.model.RenderOptions;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MDIDesktopPane extends JDesktopPane {
    private static int FRAME_OFFSET = 20;
    private GrapeMDIDesktopManager manager;


    public MDIDesktopPane() {
        manager = new GrapeMDIDesktopManager(this);
        setDesktopManager(manager);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }


    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        checkDesktopSize();
    }
    
    
    public Component add(JInternalFrame frame) {
        JInternalFrame[] array = getAllFrames();
        Point p;
        int w;
        int h;

        Component retval = super.add(frame);
        checkDesktopSize();
        if (array.length > 0) {
            p = array[0].getLocation();
            p.x = p.x + FRAME_OFFSET;
            p.y = p.y + FRAME_OFFSET;
        } else {
            p = new Point(0, 0);
        }
        frame.setLocation(p.x, p.y);
        if (frame.isResizable()) {
            w = getWidth() - (getWidth() / 8);
            h = getHeight() - (getHeight() / 8);
            if (w < frame.getMinimumSize().getWidth())
                w = (int) frame.getMinimumSize().getWidth();
            if (h < frame.getMinimumSize().getHeight())
                h = (int) frame.getMinimumSize().getHeight();
            frame.setSize(w, h);
        }
        frame.show();
        return retval;
    }


    public Component add(JInternalFrame frame, Dimensions dim, RenderOptions options) {
        JInternalFrame[] array = getAllFrames();
        Point p;
        int w;
        int h;
        Component retval = super.add(frame);
        checkDesktopSize();
        if (array.length > 0) {
            p = array[0].getLocation();
            p.x = p.x + FRAME_OFFSET;
            p.y = p.y + FRAME_OFFSET;
        } else {
            p = new Point(0, 0);
        }
        frame.setLocation(p.x, p.y);
        if (frame.isResizable()) {
            w = getWidth() - (getWidth() / 8);
            h = getHeight() - (getHeight() / 8);
            if (w < frame.getMinimumSize().getWidth())
                w = (int) frame.getMinimumSize().getWidth();
            if (h < frame.getMinimumSize().getHeight())
                h = (int) frame.getMinimumSize().getHeight();
            frame.setSize(w, h);
        }
        if (dim != null) {
            frame.setSize(dim.getWidth(), dim.getHeight());
        }
        setRenderOptions(frame, options);;
        frame.show();
        return retval;
    }
    
    
    public void show(JDialog dialog, Dimensions dim, RenderOptions options) {
        JInternalFrame[] array = getAllFrames();
        Point p;
        checkDesktopSize();
        if (array.length > 0) {
            p = array[0].getLocation();
            p.x = p.x + FRAME_OFFSET;
            p.y = p.y + FRAME_OFFSET;
        } else {
            p = new Point(0, 0);
        }
        dialog.setLocation(p.x, p.y);
        if (dim != null) {
            dialog.setSize(dim.getWidth(), dim.getHeight());
        }
        setRenderOptions(dialog, options);
        dialog.setVisible(true);

    }


    private void setRenderOptions(JInternalFrame frame, RenderOptions options) {
        if (options != null) {
            if (options.isCentered()) {
                // Determine the new location of the window
                Dimension paneSize = this.getSize();
                Dimension frameSize = frame.getSize();
                frame.setLocation(
                    (paneSize.width / 2) - (frameSize.width / 2),
                    (paneSize.height / 2) - (frameSize.height / 2));
            }
            if (options.isMaximized()) {
                try {
                    frame.setMaximum(true);
                } catch (Exception e) {

                }
            }
        }

    }
    
    private void setRenderOptions(JDialog dialog, RenderOptions options) {
        if (options != null) {
            if (options.isCentered()) {
                // Determine the new location of the window
                Dimension paneSize = this.getSize();
                Dimension dialogSize = dialog.getSize();
                dialog.setLocation(
                    (paneSize.width / 2) - (dialogSize.width / 2),
                    (paneSize.height / 2) - (dialogSize.height / 2));
            }
            if (options.isMaximized()) {
                try {
                    dialog.setSize(this.getSize());
                } catch (Exception e) {

                }
            }
        }

    }


    public void remove(Component c) {
        super.remove(c);
        checkDesktopSize();
    }


    /**
     * Cascade all internal frames
     */
    public void cascadeFrames() {
        int x = 0;
        int y = 0;
        JInternalFrame allFrames[] = getAllFrames();

        manager.setNormalSize();
        int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
        int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
        for (int i = allFrames.length - 1; i >= 0; i--) {
            allFrames[i].setSize(frameWidth, frameHeight);
            allFrames[i].setLocation(x, y);
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }
    }


    /**
     * Tile all internal frames
     */
    public void tileFrames() {
        java.awt.Component allFrames[] = getAllFrames();
        manager.setNormalSize();
        int frameHeight = getBounds().height / allFrames.length;
        int y = 0;
        for (int i = 0; i < allFrames.length; i++) {
            allFrames[i].setSize(getBounds().width, frameHeight);
            allFrames[i].setLocation(0, y);
            y = y + frameHeight;
        }
    }


    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the
     * given dimension.
     */
    public void setAllSize(Dimension d) {
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }


    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the
     * given width and height.
     */
    public void setAllSize(int width, int height) {
        setAllSize(new Dimension(width, height));
    }


    private void checkDesktopSize() {
        if (getParent() != null && isVisible())
            manager.resizeDesktop();
    }
}

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane.
 * Used to provide scrollbar functionality.
 */
class MDIDesktopManager extends DefaultDesktopManager {
    private MDIDesktopPane desktop;


    public MDIDesktopManager(MDIDesktopPane desktop) {
        this.desktop = desktop;
    }


    public void endResizingFrame(JComponent f) {
        super.endResizingFrame(f);
        resizeDesktop();
    }


    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
        resizeDesktop();
    }


    public void setNormalSize() {
        JScrollPane scrollPane = getScrollPane();
        int x = 0;
        int y = 0;

        if (scrollPane != null) {
            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                Insets scrollInsets = getScrollPaneInsets();
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
                    - scrollInsets.bottom);
            }

            d.setSize(d.getWidth() - 20, d.getHeight() - 20);
            desktop.setAllSize(x, y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }


    private Insets getScrollPaneInsets() {
        JScrollPane scrollPane = getScrollPane();
        if (scrollPane == null) {
            return new Insets(0, 0, 0, 0);
        }

        Border border = getScrollPane().getBorder();
        if (border == null) {
            return new Insets(0, 0, 0, 0);
        }

        return border.getBorderInsets(scrollPane);
    }


    private JScrollPane getScrollPane() {
        if (desktop.getParent() instanceof JViewport) {
            JViewport viewPort = (JViewport) desktop.getParent();
            if (viewPort.getParent() instanceof JScrollPane)
                return (JScrollPane) viewPort.getParent();
        }
        return null;
    }


    protected void resizeDesktop() {
        int x = 0;
        int y = 0;
        JScrollPane scrollPane = getScrollPane();

        if (scrollPane != null) {
            JInternalFrame allFrames[] = desktop.getAllFrames();
            for (int i = 0; i < allFrames.length; i++) {
                if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
                    x = allFrames[i].getX() + allFrames[i].getWidth();
                }
                if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
                    y = allFrames[i].getY() + allFrames[i].getHeight();
                }
            }
            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                Insets scrollInsets = getScrollPaneInsets();
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
                    - scrollInsets.bottom);
            }

            if (x <= d.getWidth())
                x = ((int) d.getWidth()) - 20;
            if (y <= d.getHeight())
                y = ((int) d.getHeight()) - 20;
            desktop.setAllSize(x, y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }
}