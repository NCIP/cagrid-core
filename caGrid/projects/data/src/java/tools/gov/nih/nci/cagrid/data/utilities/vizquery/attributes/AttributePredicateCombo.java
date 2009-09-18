package gov.nih.nci.cagrid.data.utilities.vizquery.attributes;

import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  AttributePredicateCombo
 *  Combo box to select an attribute's query predicate
 * 
 * @author David Ervin
 * 
 * @created Apr 6, 2007 9:34:11 AM
 * @version $Id: AttributePredicateCombo.java,v 1.3 2007-12-14 17:11:52 dervin Exp $ 
 */
public class AttributePredicateCombo extends JComboBox {

    private List<AttributePredicateChangeListener> listeners = null;
    
    public AttributePredicateCombo() {
        listeners = new LinkedList<AttributePredicateChangeListener>();
        loadPredicates();
        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                Predicate selection = (Predicate) getSelectedItem();
                firePredicateChanged(selection);
            }
        });
    }
    
    
    public void addPredicateChangeListener(AttributePredicateChangeListener listener) {
        listeners.add(listener);
    }
    
    
    public boolean removePredicateChangeListener(AttributePredicateChangeListener listener) {
        return listeners.remove(listener);
    }
    
    
    protected void firePredicateChanged(Predicate newValue) {
        for (AttributePredicateChangeListener listener : listeners) {
            // cloned value so it listener changes it, it won't affect things here
            Predicate clone = Predicate.fromString(newValue.getValue());
            listener.attributePredicateChanged(clone);
        }
    }
    
    
    private void loadPredicates() {
        Field[] fields = Predicate.class.getFields();
        try {
            for (Field field : fields) {
                if (field.getType().equals(Predicate.class)) {
                    int mods = field.getModifiers();
                    if (Modifier.isStatic(mods) && Modifier.isPublic(mods)) {
                        addItem(field.get(null));
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            CompositeErrorDialog.showErrorDialog("Error populating predicate combo: " + ex.getMessage(), ex);
        }
    }
}
