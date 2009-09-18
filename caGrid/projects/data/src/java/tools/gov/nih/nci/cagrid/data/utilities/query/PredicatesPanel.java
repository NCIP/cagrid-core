package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.cqlquery.Predicate;

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
 *  PredicatesPanel
 *  Panel for showing and selecting a Predicate
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 4, 2006 
 * @version $Id$ 
 */
public class PredicatesPanel extends JPanel {
	private ButtonGroup group = null;
		
	public PredicatesPanel() {
		group = new ButtonGroup();
		initialize();		
	}
	
	
	private void initialize() {
		this.setBorder(BorderFactory.createTitledBorder(null, "Predicates", 
			javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
			javax.swing.border.TitledBorder.DEFAULT_POSITION, null, 
			PortalLookAndFeel.getPanelLabelColor()));
		this.setLayout(new GridBagLayout());
		initPredicateButtons();
	}
	
	
	private void initPredicateButtons() {
		// walk the static predicate fields
		Field[] fields = Predicate.class.getFields();
		List<String> predicates = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			int mods = fields[i].getModifiers();
			if (Modifier.isStatic(mods) && Modifier.isPublic(mods)
				&& fields[i].getType().equals(Predicate.class)) {
				try {
					Predicate p = (Predicate) fields[i].get(null);
					predicates.add(p.getValue());
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}
		// sort the predicates by value
		Collections.sort(predicates);
		// create buttons
		for (int i = 0; i < predicates.size(); i++) {
			JRadioButton button = new JRadioButton(predicates.get(i));
			group.add(button);
			if (button.getText().equals(Predicate._EQUAL_TO)) {
				// schema default for predicate is EQUAL_TO
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


	public Predicate getPredicate() {
		String textValue = null;
		Enumeration buttons = group.getElements();
		while (buttons.hasMoreElements()) {
			JRadioButton button = (JRadioButton) buttons.nextElement();
			if (button.getModel().equals(group.getSelection())) {
				textValue = button.getText();
				break;
			}
		}
		return Predicate.fromString(textValue);
	}
	
	
	
	public void setPredicate(Predicate predicate) {
		setPredicate(predicate.getValue());
	}
	
	
	public void setPredicate(String predicate) {
		Enumeration buttons = group.getElements();
		while (buttons.hasMoreElements()) {
			JRadioButton button = (JRadioButton) buttons.nextElement();
			if (button.getText().equals(predicate)) {
				group.setSelected(button.getModel(), true);
				break;
			}
		}
	}
}
