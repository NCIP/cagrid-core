package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.ThreadManager;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.cagrid.grape.filters.XMLFileFilter;
import org.cagrid.grape.model.Application;
import org.cagrid.grape.model.Component;
import org.cagrid.grape.model.Components;
import org.cagrid.grape.model.Dimensions;
import org.cagrid.grape.model.Menu;
import org.cagrid.grape.model.Menus;
import org.cagrid.grape.model.RenderOptions;
import org.cagrid.grape.model.Submenus;
import org.cagrid.grape.utils.ErrorDialog;
import org.cagrid.grape.utils.IconUtils;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */

public class GridApplication extends JFrame {

    private javax.swing.JMenuBar jJMenuBar = null;

    private javax.swing.JMenu fileMenu = null;

    private javax.swing.JMenuItem exitMenuItem = null;

    private javax.swing.JMenu windowsMenu = null;

    private javax.swing.JMenu helpMenu = null;

    private javax.swing.JMenuItem about = null;

    private javax.swing.JToolBar toolBar = null;

    private JScrollPane scrollPane = null;

    private MDIDesktopPane desktop = null;

    private javax.swing.JMenuItem closeMenuItem = null;

    private javax.swing.JMenuItem closeAllMenuItem = null;

    private ApplicationComponent lastComp = null;

    private Application app;

    private ThreadManager threadManager;

    private ConfigurationManager configurationManager;

    private static GridApplication application;

    private ApplicationContext context;


    private GridApplication(Application app) throws Exception {
        super();
        ErrorDialog.setOwnerFrame(this);
        this.app = app;
        LookAndFeel.setApplicationLogo(this.app.getApplicationLogo());
        this.threadManager = new ThreadManager();
        this.context = new ApplicationContext(this);
        initialize();
    }


    public ThreadManager getThreadManager() {
        return threadManager;
    }


    public static GridApplication getInstance(Application app) throws Exception {
        if (application == null) {
            application = new GridApplication(app);
            application.startPostInitializer();
            return application;
        } else {
            throw new Exception("An instance of the Grid Application has already been created.");
        }
    }


    public static ApplicationContext getContext() {
        if (application == null) {
            return null;
        }
        return application.context;
    }


    public static void main(String[] args) {
        File file = null;
        boolean promptForConfigFile = false;

        if (args.length == 0) {
            promptForConfigFile = true;
        } else {
            file = new File(args[0]);
            if (!file.exists()) {
                System.out.println("Invalid configuration file specified, prompting for one...");
                promptForConfigFile = true;
            }
        }
        if (promptForConfigFile) {
            // No config passed, try to prompt for one
            JFrame tempFrame = new JFrame(); // temp frame to open file
            // chooser from
            System.out.println("No configuration file passed in, prompting for one...");
            JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
            chooser.setDialogTitle("Select an application configuration file to use.");
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            // TODO: FIX THIS
            chooser.setFileFilter(new XMLFileFilter());
            int choice = chooser.showOpenDialog(tempFrame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try {
                    file = new File(chooser.getSelectedFile().getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error loading file",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.err.println("No configuration file passed in or selected... exiting.");
                System.exit(1);
            }
            // destroy the temp frame
            tempFrame.dispose();
        } else if (args.length > 1) {
            // invalid usage
            System.out.println("USAGE:\n");
            System.out.println("	java " + GridApplication.class.getName() + " [application-config-file]");
            System.exit(1);
        }

        try {
            Application app = (Application) Utils.deserializeDocument(file.getAbsolutePath(), Application.class);

            // launch the portal with the passed config
            GridApplication applicationInstance = GridApplication.getInstance(app);

            try {
                applicationInstance.pack();
            } catch (Exception e) {
                applicationInstance.setIconImage(null);
                applicationInstance.pack();
            }
            if (app.getDimensions() != null) {
                Dimension d = new Dimension(app.getDimensions().getWidth(), app.getDimensions().getHeight());
                applicationInstance.setSize(d);
            }

            applicationInstance.setVisible(true);
            applicationInstance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startPostInitializer() throws Exception {
        if (this.app.getPostInitializerClass() != null) {
            ApplicationInitializer appInit = (ApplicationInitializer) Class.forName(this.app.getPostInitializerClass())
                .newInstance();
            appInit.intialize(app);
        }
    }
    
    private void startPreInitializer() throws Exception {
        if (this.app.getPreInitializerClass() != null) {
            ApplicationInitializer appInit = (ApplicationInitializer) Class.forName(this.app.getPreInitializerClass())
                .newInstance();
            appInit.intialize(app);
        }
    }


    private void initialize() throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Failed to set system look and feel.");
        }
        startPreInitializer();
        String syncClass = app.getConfigurationSynchronizerClass();

        ConfigurationSynchronizer cs = null;
        if (syncClass != null) {
            cs = (ConfigurationSynchronizer) Class.forName(syncClass).newInstance();
        }
        configurationManager = new ConfigurationManager(app.getConfiguration(), cs);

        List<Component> toolbarComponents = new ArrayList<Component>();
        this.setJMenuBar(getJJMenuBar(toolbarComponents));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(getJScrollPane(), BorderLayout.CENTER);
        this.getContentPane().add(getToolBar(toolbarComponents), BorderLayout.NORTH);

        this.setTitle(app.getName());

        if (app.getIcon() != null) {
            ImageIcon icon = IconUtils.loadIcon(app.getIcon());
            if (icon != null) {
                this.setIconImage(icon.getImage());
            }
        }
    }


    private void executeComponent(Component component) {
        try {
            ExecuteComponent comp = new ExecuteComponent(this, component);
            getThreadManager().executeInBackground(comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private javax.swing.JMenuItem getComponentItem(final Component comp, boolean showIcon) {
        JMenuItem item = new javax.swing.JMenuItem();
        item.setText(comp.getTitle());
        if (showIcon) {
            item.setIcon(IconUtils.loadIcon(comp.getIcon()));
        }
        if (comp.getMnemonic() != null) {
            item.setMnemonic(comp.getMnemonic().intValue());
        }
        item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                executeComponent(comp);
            }
        });
        return item;
    }


    private javax.swing.JButton getComponentButton(final Component comp) {
        JButton button = new JButton();
        button = new javax.swing.JButton();
        button.setText(comp.getTitle());
        button.setToolTipText(comp.getDescription());
        button.setIcon(IconUtils.loadIcon(comp.getIcon()));
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                executeComponent(comp);
            }
        });
        return button;
    }


    private JScrollPane getJScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.getViewport().add(this.getMDIDesktopPane());
        }
        return scrollPane;
    }


    /**
     * This method initializes jJMenuBar
     * 
     * @return javax.swing.JMenuBar
     */
    private javax.swing.JMenuBar getJJMenuBar(List<Component> toolbarComponents) throws Exception {
        if (jJMenuBar == null) {
            jJMenuBar = new javax.swing.JMenuBar();

            Menu localFileMenu = null;
            Menu localHelpMenu = null;
            Menus menus = app.getMenus();

            if (menus != null) {
                Menu[] menu = menus.getMenu();
                if (menu != null) {
                    for (int i = 0; i < menu.length; i++) {
                        if (menu[i].getTitle().equals("Help")) {
                            localHelpMenu = menu[i];
                        }
                        if (menu[i].getTitle().equals("File")) {
                            localFileMenu = menu[i];
                        }
                    }
                }
            }

            jJMenuBar.add(getFileMenu(toolbarComponents, localFileMenu));

            if (menus != null) {
                Menu[] menu = menus.getMenu();
                if (menu != null) {
                    for (int i = 0; i < menu.length; i++) {
                        if (!menu[i].getTitle().equals("Help") && !menu[i].getTitle().equals("File"))
                            jJMenuBar.add(getMenu(toolbarComponents, null, menu[i]));
                    }
                }
            }
            jJMenuBar.add(getWindowsMenu());
            jJMenuBar.add(getHelpMenu(toolbarComponents, localHelpMenu));
        }
        return jJMenuBar;
    }


    private javax.swing.JMenu addToMenu(List<Component> toolbarComponents, JMenu parent, Menu menu) {
        javax.swing.JMenu jmenu = parent;

        Submenus submenus = menu.getSubmenus();
        if (submenus != null) {
            Menu[] submenu = submenus.getMenu();
            if (submenu != null) {
                for (int i = 0; i < submenu.length; i++) {
                    jmenu.add(getMenu(toolbarComponents, menu, submenu[i]));
                }
            }
        }

        Components comps = menu.getComponents();
        if (comps != null) {
            Component[] comp = comps.getComponent();
            if (comp != null) {
                for (int i = 0; i < comp.length; i++) {
                    jmenu.add(getComponentItem(comp[i], menu.getShowIcons().booleanValue()));
                    if (comp[i].isShowOnToolBar()) {
                        toolbarComponents.add(comp[i]);
                    }
                }
            }
        }

        return jmenu;
    }


    private javax.swing.JMenu getMenu(List<Component> toolbarComponents, Menu parent, Menu menu) {
        javax.swing.JMenu jmenu = new javax.swing.JMenu();
        jmenu.setText(menu.getTitle());
        if ((parent != null) && (parent.getShowIcons().booleanValue())) {
            jmenu.setIcon(IconUtils.loadIcon(menu.getIcon()));
        }

        Submenus submenus = menu.getSubmenus();
        if (submenus != null) {
            Menu[] submenu = submenus.getMenu();
            if (submenu != null) {
                for (int i = 0; i < submenu.length; i++) {
                    jmenu.add(getMenu(toolbarComponents, menu, submenu[i]));
                }
            }
        }

        Components comps = menu.getComponents();
        if (comps != null) {
            Component[] comp = comps.getComponent();
            if (comp != null) {
                for (int i = 0; i < comp.length; i++) {
                    jmenu.add(getComponentItem(comp[i], menu.getShowIcons().booleanValue()));
                    if (comp[i].isShowOnToolBar()) {
                        toolbarComponents.add(comp[i]);
                    }
                }
            }
        }

        return jmenu;
    }


    /**
     * This method initializes windowsMenu
     * 
     * @return javax.swing.JMenu
     */
    private javax.swing.JMenu getWindowsMenu() {
        if (windowsMenu == null) {
            windowsMenu = new WindowMenu(getMDIDesktopPane(), this);
        }
        return windowsMenu;
    }


    /**
     * This method initializes fileMenu
     * 
     * @return javax.swing.JMenu
     */
    private javax.swing.JMenu getFileMenu(List<Component> toolbarComponents, Menu menu) {
        if (fileMenu == null) {
            fileMenu = new javax.swing.JMenu();
            if (menu != null) {
                addToMenu(toolbarComponents, fileMenu, menu);
            }
            fileMenu.addSeparator();
            fileMenu.add(getCloseMenuItem());
            fileMenu.add(getCloseAllMenuItem());
            fileMenu.addSeparator();
            fileMenu.setText("File");
            fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
            fileMenu.add(getExitMenuItem());

        }
        return fileMenu;
    }


    /**
     * This method initializes exitMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new javax.swing.JMenuItem();
            exitMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exitMenuItem.setText("Exit");
            exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    exit();
                }
            });
            exitMenuItem.setToolTipText("Closes the application.");
        }
        return exitMenuItem;
    }


    protected void exit() {
        System.out.println("Exiting...");
        System.exit(0);

    }


    private javax.swing.JMenu getHelpMenu(List<Component> toolbarComponents, Menu menu) {
        if (helpMenu == null) {
            helpMenu = new javax.swing.JMenu();
            if (menu != null) {
                addToMenu(toolbarComponents, helpMenu, menu);
            }
            fileMenu.addSeparator();
            helpMenu.add(getJMenuItem());
            helpMenu.setText("Help");
            helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
        }
        return helpMenu;
    }


    private javax.swing.JMenuItem getJMenuItem() {
        if (about == null) {
            about = new javax.swing.JMenuItem();
            about.setText("About");
            about.setMnemonic(java.awt.event.KeyEvent.VK_A);
            about.setIcon(LookAndFeel.getAboutIcon());
            about.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showAboutDialog();
                }
            });
        }
        return about;
    }


    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, this.app.getAboutHTML(), "About: " + this.app.getName(),
            JOptionPane.INFORMATION_MESSAGE, LookAndFeel.getApplicationLogo());
    }


    private javax.swing.JToolBar getToolBar(List comps) {
        if (toolBar == null) {
            toolBar = new javax.swing.JToolBar("tools", SwingConstants.HORIZONTAL);
            for (int i = 0; i < comps.size(); i++) {
                Component comp = (Component) comps.get(i);
                toolBar.add(this.getComponentButton(comp));
            }
            toolBar.setName("Tools");
            toolBar.setRollover(true);
        }
        return toolBar;
    }


    public void addApplicationComponent(ApplicationComponent frame, Dimensions dim, RenderOptions options) {
        this.lastComp = frame;
        getMDIDesktopPane().add(frame, dim, options);
    }


    public void addApplicationComponent(ApplicationComponent frame) {
        this.lastComp = frame;
        getMDIDesktopPane().add(frame);
    }


    public void addApplicationComponent(ApplicationComponent frame, int width, int height) {
        this.lastComp = frame;
        getMDIDesktopPane().add(frame, new Dimensions(height, width), null);
    }


    public ApplicationComponent getLastComponent() {
        return this.lastComp;
    }


    public MDIDesktopPane getMDIDesktopPane() {
        if (desktop == null) {
            desktop = new MDIDesktopPane();
        }
        return desktop;
    }


    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }


    protected void closeAllFrames() {
        JInternalFrame[] frameList = getMDIDesktopPane().getAllFrames();
        for (int i = 0; i < frameList.length; i++) {
            try {
                frameList[i].setClosed(true);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    private javax.swing.JMenuItem getCloseMenuItem() {
        if (closeMenuItem == null) {
            closeMenuItem = new javax.swing.JMenuItem();
            closeMenuItem.setText("Close");
            closeMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
            closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeFrame();
                }
            });
        }
        return closeMenuItem;
    }


    /**
     * 
     */
    protected void closeFrame() {
        JInternalFrame jif = getMDIDesktopPane().getSelectedFrame();
        if (jif != null) {
            try {
                jif.setClosed(true);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    /**
     * This method initializes closeAllMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getCloseAllMenuItem() {
        if (closeAllMenuItem == null) {
            closeAllMenuItem = new javax.swing.JMenuItem();
            closeAllMenuItem.setText("Close All");
            closeAllMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_A);
            closeAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeAllFrames();
                }
            });
        }
        return closeAllMenuItem;
    }


    static public class ExecuteComponent extends Runner {
        private Component component;

        private GridApplication app;


        public ExecuteComponent(GridApplication app, Component comp) {
            this.component = comp;
            this.app = app;
        }


        public void execute() {
            try {
                Object obj = Class.forName(component.getClassname()).newInstance();
                if (obj instanceof ApplicationComponent) {
                    ApplicationComponent comp = (ApplicationComponent) obj;
                    comp.setTitle(this.component.getTitle());
                    if (this.component.getIcon() != null) {
                        comp.setFrameIcon(IconUtils.loadIcon(this.component.getIcon()));
                    }
                    app.addApplicationComponent(comp, component.getDimensions(), component.getRenderOptions());
                } else if (obj instanceof JInternalFrame) {
                    JInternalFrame comp = (JInternalFrame) obj;
                    comp.setTitle(this.component.getTitle());
                    if (this.component.getIcon() != null) {
                        comp.setFrameIcon(IconUtils.loadIcon(this.component.getIcon()));
                    }
                    app.getMDIDesktopPane().add(comp, component.getDimensions(), component.getRenderOptions());
                } else if (obj instanceof JDialog) {
                    JDialog comp = (JDialog) obj;
                    app.getMDIDesktopPane().show(comp, component.getDimensions(), component.getRenderOptions());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}