package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class WindowMenu extends JMenu {
    private MDIDesktopPane desktop;

    private JMenuItem cascade = new JMenuItem("Cascade");

    private JMenuItem tile = new JMenuItem("Tile");

    private JMenuItem prefs;

    private GridApplication app;


    public WindowMenu(MDIDesktopPane desktop, final GridApplication app) {
        this.desktop = desktop;
        this.app = app;
        setText("Window");
        setMnemonic(java.awt.event.KeyEvent.VK_W);
        cascade.setMnemonic(java.awt.event.KeyEvent.VK_C);
        cascade.setIcon(LookAndFeel.getCascadeIcon());
        cascade.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.cascadeFrames();
            }
        });

        tile.setMnemonic(java.awt.event.KeyEvent.VK_T);
        tile.setIcon(LookAndFeel.getTileIcon());
        tile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.tileFrames();
            }
        });

        prefs = new javax.swing.JMenuItem();
        prefs.setText("Preferences");
        prefs.setIcon(LookAndFeel.getPreferencesIcon());
        prefs.setMnemonic(java.awt.event.KeyEvent.VK_Q);
        prefs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    ConfigurationWindow window = new ConfigurationWindow(app);
                    window.setModal(false);
                    window.setSize(800, 500);
                    window.setVisible(true);
                    GridApplication.getContext().centerWindow(window);
                } catch (Exception ex) {
                    ErrorDialog.showError(Utils.getExceptionMessage(ex));
                    ex.printStackTrace();
                }
            }
        });

        addMenuListener(new MenuListener() {
            public void menuCanceled(MenuEvent e) {
            }


            public void menuDeselected(MenuEvent e) {
                removeAll();
            }


            public void menuSelected(MenuEvent e) {
                buildChildMenus();
            }
        });
    }


    /* Sets up the children menus depending on the current desktop state */
    private void buildChildMenus() {
        int i;
        ChildMenuItem menu;
        JInternalFrame[] array = desktop.getAllFrames();

        add(cascade);
        add(tile);
        if (app.getConfigurationManager().getConfiguration() != null) {
            add(prefs);
        }
        if (array.length > 0)
            addSeparator();
        cascade.setEnabled(array.length > 0);
        tile.setEnabled(array.length > 0);

        for (i = 0; i < array.length; i++) {
            menu = new ChildMenuItem(array[i]);
            menu.setState(i == 0);
            menu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JInternalFrame frame = ((ChildMenuItem) ae.getSource()).getFrame();
                    frame.moveToFront();
                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
                }
            });
            menu.setIcon(array[i].getFrameIcon());
            add(menu);
        }
    }


    /*
     * This JCheckBoxMenuItem descendant is used to track the child frame that
     * corresponds to a give menu.
     */
    static class ChildMenuItem extends JCheckBoxMenuItem {
        private JInternalFrame frame;


        public ChildMenuItem(JInternalFrame frame) {
            super(frame.getTitle());
            this.frame = frame;
        }


        public JInternalFrame getFrame() {
            return frame;
        }
    }
}