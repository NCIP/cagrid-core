package gov.nih.nci.cagrid.common.portal;

import java.awt.AWTEvent;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

/** 
 *  NonEditableCheckBox
 *  A non-editable JCheckBox.  This check box's selection state can be
 *  changed programatically, but it will not respond to user edits from
 *  the keyboard or mouse.  The font has been ita
 * 
 * @author David Ervin
 * 
 * @created Aug 8, 2007 10:57:31 AM
 * @version $Id: NonEditableCheckBox.java,v 1.1 2007-08-08 15:07:03 dervin Exp $ 
 */
public class NonEditableCheckBox extends JCheckBox {

    public NonEditableCheckBox() {
        super();
        initFont();
    }


    public NonEditableCheckBox(Icon icon) {
        super(icon);
        initFont();
    }


    public NonEditableCheckBox(String text) {
        super(text);
        initFont();
    }


    public NonEditableCheckBox(Action a) {
        super(a);
        initFont();
    }


    public NonEditableCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
        initFont();
    }


    public NonEditableCheckBox(String text, boolean selected) {
        super(text, selected);
        initFont();
    }


    public NonEditableCheckBox(String text, Icon icon) {
        super(text, icon);
        initFont();
    }


    public NonEditableCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        initFont();
    }

    
    private void initFont() {
        //  use an italics font to indicate non-editable
        Font font = getFont();
        Font italFont = new Font(font.getName(), Font.ITALIC, font.getSize());
        setFont(italFont);
    }
        
        
    public void processEvent(AWTEvent e) {
        // nuke all events == NO EDITING
    }
}
