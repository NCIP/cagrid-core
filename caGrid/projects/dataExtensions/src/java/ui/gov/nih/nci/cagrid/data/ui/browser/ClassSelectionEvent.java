package gov.nih.nci.cagrid.data.ui.browser;

import java.util.EventObject;

/** 
 *  ClassSelectionEvent
 *  Event fired when a class is selected in the class browser panel
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 12, 2006 
 * @version $Id: ClassSelectionEvent.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public class ClassSelectionEvent extends EventObject {

	public ClassSelectionEvent(ClassBrowserPanel source) {
		super(source);
	}
}
