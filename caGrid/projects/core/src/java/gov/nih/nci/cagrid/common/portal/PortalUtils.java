package gov.nih.nci.cagrid.common.portal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */

public class PortalUtils {

    /**
     * Centers the window on the users monitor
     * 
     * @param comp
     */
    public final static void centerWindowInScreen(Window comp) {
        comp.setLocationRelativeTo(null);
    }


    public static void setContainerEnabled(Container con, boolean enable) {
        for (int i = 0; i < con.getComponentCount(); i++) {
            Component comp = con.getComponent(i);
            comp.setEnabled(enable);
            if (comp instanceof Container) {
                setContainerEnabled((Container) comp, enable);
            }
        }
    }
}