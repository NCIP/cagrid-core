package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/** 
 *  LogicalOperatorPanel
 *  Panel for showing and selecting a LogicalOperator
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 4, 2006 
 * @version $Id$ 
 */
public class LogicalOperatorPanel extends JPanel {
	private ButtonGroup group = null;

	public LogicalOperatorPanel() {
		group = new ButtonGroup();
		initialize();
	}
	
	
	private void initialize() {
		this.setBorder(BorderFactory.createTitledBorder(null, "Logical Operators", 
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
			javax.swing.border.TitledBorder.DEFAULT_POSITION, null, 
			PortalLookAndFeel.getPanelLabelColor()));
		this.setLayout(new GridBagLayout());
		initLogicButtons();
	}
	
	
	private void initLogicButtons() {
		// walk the static logical op fields
		Field[] fields = LogicalOperator.class.getFields();
		List<String> ops = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			int mods = fields[i].getModifiers();
			if (Modifier.isStatic(mods) && Modifier.isPublic(mods)
				&& fields[i].getType().equals(LogicalOperator.class)) {
				try {
					LogicalOperator l = (LogicalOperator) fields[i].get(null);
					ops.add(l.getValue());
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}
		// sort the predicates by value
		Collections.sort(ops);
		// create buttons
		for (int i = 0; i < ops.size(); i++) {
			JRadioButton button = new JRadioButton(ops.get(i));
			group.add(button);
			if (button.getText().equals(LogicalOperator._AND)) {
				// schema default for logical ops is AND
				group.setSelected(button.getModel(), true);
			}
			GridBagConstraints cons = new GridBagConstraints();
			cons.insets = new Insets(2, 2, 2, 2);
			cons.gridx = 0;
			cons.gridy = i;
			cons.anchor = GridBagConstraints.WEST;
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.weightx = 1.0D;
			add(button, cons);
		}
	}
	
	
	public LogicalOperator getLogicalOperator() {
		String textValue = null;
		Enumeration buttons = group.getElements();
		while (buttons.hasMoreElements()) {
			JRadioButton button = (JRadioButton) buttons.nextElement();
			if (button.getModel().equals(group.getSelection())) {
				textValue = button.getText();
				break;
			}
		}
		return LogicalOperator.fromString(textValue);
	}
	
	
	
	public void setLogicalOperator(LogicalOperator op) {
		setLogicalOperator(op.getValue());
	}
	
	
	public void setLogicalOperator(String op) {
		Enumeration buttons = group.getElements();
		while (buttons.hasMoreElements()) {
			JRadioButton button = (JRadioButton) buttons.nextElement();
			if (button.getText().equals(op)) {
				group.setSelected(button.getModel(), true);
				break;
			}
		}
	}
}
