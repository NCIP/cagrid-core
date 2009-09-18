package gov.nih.nci.cagrid.common.portal;

import java.awt.Color;

import javax.swing.ImageIcon;


public class PortalLookAndFeel {
	
	public final static Color getPanelLabelColor() {
		float[] vals = new float[3];
		Color.RGBtoHSB(62, 109, 181, vals);
		return Color.getHSBColor(vals[0], vals[1], vals[2]);
	}

	public final static Color getTableSelectTextColor() {
		return Color.WHITE;
	}

	public final static Color getTableRowColor() {
		return getLightBlue();
	}
	
	public final static Color getLightGray() {
		float[] vals = new float[3];
		Color.RGBtoHSB(211, 211, 211, vals);
		return Color.getHSBColor(vals[0], vals[1], vals[2]);
	}
	
	public final static Color getDarkGray() {
		float[] vals = new float[3];
		Color.RGBtoHSB(180, 180, 180, vals);
		return Color.getHSBColor(vals[0], vals[1], vals[2]);
	}
	
	
	public final static Color getLightBlue() {
		float[] vals = new float[3];
		Color.RGBtoHSB(183, 201, 227, vals);
		return Color.getHSBColor(vals[0], vals[1], vals[2]);
	}

	public final static Color getDarkBlue() {
		float[] vals = new float[3];
		Color.RGBtoHSB(78, 111, 160, vals);
		return Color.getHSBColor(vals[0], vals[1], vals[2]);
	}

	public final static Color getTableSelectColor() {
		return getDarkBlue();
	}

	public final static ImageIcon getCloseIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/edit-delete.png"));
	}

	public final static ImageIcon getSelectIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/go-next.png"));
	}

	public final static ImageIcon getEditIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/Draw.gif"));
	}

	public final static ImageIcon getInformIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/Inform.gif"));
	}

	public final static ImageIcon getAddIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/list-add.png"));
	}

	public final static ImageIcon getRemoveIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/list-remove.png"));
	}

	public final static ImageIcon getQueryIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/system-search.png"));
	}
	
	public final static ImageIcon getSaveIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/save.png"));
	}
	
	public final static ImageIcon getImportIcon() {
		return new javax.swing.ImageIcon(PortalLookAndFeel.class
				.getResource("/folder-open.png"));
	}
}
