package gov.nih.nci.cagrid.common.portal;

import java.awt.Color;

import javax.swing.table.DefaultTableModel;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class PortalBaseTable extends PortalTable{
	public PortalBaseTable(DefaultTableModel model){
		super(model,Color.WHITE,Color.BLACK,PortalLookAndFeel.getTableRowColor(),Color.BLACK,PortalLookAndFeel.getTableSelectColor(),PortalLookAndFeel.getTableSelectTextColor());
	}

}
