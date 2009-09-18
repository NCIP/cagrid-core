package gov.nih.nci.cagrid.introduce.extension.utils;

import org.apache.axis.message.MessageElement;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;

/** 
 *  AxisJdomUtils
 *  Utils to consolidate conversion to / from axis' message element and JDom's element
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 9, 2006 
 * @version $Id: AxisJdomUtils.java,v 1.2 2006-06-30 16:03:51 dervin Exp $ 
 */
public class AxisJdomUtils {

	public static Element fromMessageElement(MessageElement me) {
		return (Element) ((new DOMBuilder())).build(me).detach();
	}
	
	
	public static MessageElement fromElement(Element elem) throws JDOMException {
		Document doc = new Document(elem);
		org.w3c.dom.Document tempDoc = new DOMOutputter().output(doc);
		return new MessageElement(tempDoc.getDocumentElement());
	}
}
