import gov.nih.nci.cagrid.common.XMLUtilities;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;


public class NarrativeParser {

	public void parse() throws Exception {
		String filename = "schema/ISO_datatypes_Narrative.xsd";
		Document doc = XMLUtilities.fileNameToDocument(filename);
		MyFilter filter = new MyFilter();
		List children = doc.getRootElement().getContent(filter);
		for (Object o : children) {
			if (o instanceof Element) {
				Element el = (Element)o;
				if (el.getName().equals("complexType") || el.getName().equals("simpleType")) {
					System.out.println("type: " + el.getAttributeValue("name"));							
				} else if (el.getName().equals("element")) {
					if (el.getAttributeValue("type") != null) {
						System.out.println("element: " + el.getAttributeValue("name") + "," + el.getAttributeValue("type"));							
				}
				}
//				System.out.println(el.getName());
			}
		}
	}
	
	static class MyFilter implements Filter {
		public boolean matches(Object arg0) {
			return true;
		}
	}
	
	public static void main(String[] args) throws Exception {
		NarrativeParser parser = new NarrativeParser();
		parser.parse();
	}
}
