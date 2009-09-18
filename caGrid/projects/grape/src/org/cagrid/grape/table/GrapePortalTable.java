
package org.cagrid.grape.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class GrapePortalTable extends JTable {

	private final static Color DEFAULT_FOREGROUND_1 = Color.BLACK;
	private final static Color DEFAULT_BACKGROUND_1 = Color.WHITE;
	private final static Color DEFAULT_FOREGROUND_2 = Color.BLACK;
	private final static Color DEFAULT_BACKGROUND_2 = Color.WHITE;
	private final static Color DEFAULT_SELECTED_FOREGROUND = Color.BLACK;
	private final static Color DEFAULT_SELECTED_BACKGROUND = Color.WHITE;


	public GrapePortalTable(DefaultTableModel model) {
		this(model, DEFAULT_BACKGROUND_1, DEFAULT_FOREGROUND_1, DEFAULT_BACKGROUND_2, DEFAULT_FOREGROUND_2,
			DEFAULT_SELECTED_BACKGROUND, DEFAULT_SELECTED_FOREGROUND);
	}


	public GrapePortalTable(DefaultTableModel model, Color bg1, Color fg1, Color bg2, Color fg2, Color sbg, Color sfg) {
		super(model);
		setDefaultRenderer(Object.class, new GrapeTableCellRenderer(bg1, fg1, bg2, fg2, sbg, sfg));
		// setDefaultEditor(JComponent.class, new JComponentCellEditor());
		// this.setCellSelectionEnabled(true);
		this.setRowSelectionAllowed(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					try {
						doubleClick();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.getClickCount() == 1) {
					try {
						singleClick();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		// this.setOpaque(true);
	}


	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}


	public abstract void doubleClick() throws Exception;


	public abstract void singleClick() throws Exception;


	public boolean isCellEditable(int row, int column) {
		return false;
	}


	public synchronized void addRow(Vector v) {
		((DefaultTableModel) this.getModel()).addRow(v);
	}


	public synchronized void removeRow(int i) {
		((DefaultTableModel) this.getModel()).removeRow(i);
	}


	public synchronized void clearTable() {
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		while (model.getRowCount() != 0) {
			model.removeRow(0);
		}

	}

}