package gov.nih.nci.cagrid.common.portal;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class PortalTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

    private Color bg1;
    private Color fg1;
    private Color bg2;
    private Color fg2;
    private Color sbg;
    private Color sfg;


    public PortalTableCellRenderer(Color bg1, Color fg1, Color bg2, Color fg2, Color sbg, Color sfg) {
        this.bg1 = bg1;
        this.fg1 = fg1;
        this.bg2 = bg2;
        this.fg2 = fg2;
        this.sbg = sbg;
        this.sfg = sfg;
    }


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            if ((row % 2) == 0) {
                comp.setBackground(bg1);
                comp.setForeground(fg1);
            } else {
                comp.setBackground(bg2);
                comp.setForeground(fg2);
            }
        } else {
            comp.setBackground(sbg);
            comp.setForeground(sfg);
        }

        return comp;
    }
}
