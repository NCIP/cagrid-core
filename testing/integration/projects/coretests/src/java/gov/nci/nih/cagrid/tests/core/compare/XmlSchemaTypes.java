/*
 * Created on Feb 16, 2005
 */
package gov.nci.nih.cagrid.tests.core.compare;

import gov.nci.nih.cagrid.tests.core.types.BooleanType;
import gov.nci.nih.cagrid.tests.core.types.DateTimeType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Provides support for mapping types to XmlSchema elements and attributes.
 * This only works for elements and attributes that have a simple XML type.
 * The mapping is handled by the name of the element or attribute, so schemas
 * that have multiple attributes or elements with the same name will fail.
 * @author MCCON012
 */
public class XmlSchemaTypes
{
	/**
	 * Name to Class
	 */
	protected Hashtable typeTable = new Hashtable();
	/**
	 * Currently parsed file
	 */
	protected File xmlSchema = null;
	/**
	 * List of files parsed to avoid circular includes
	 */
	protected HashSet schemaList = new HashSet();
	
	/**
	 * Add the element and attribute simple types to the mapping
	 * @param xmlSchema
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void parseTypes(File xmlSchema) 
		throws ParserConfigurationException, SAXException, IOException
	{
		if (schemaList.contains(xmlSchema)) return;
		schemaList.add(xmlSchema);
		
		File oldXmlSchema = this.xmlSchema;
		this.xmlSchema = xmlSchema;
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		SAXParser parser = factory.newSAXParser();
		
		SchemaHandler handler = new SchemaHandler();
		parser.parse(xmlSchema, handler);

		this.xmlSchema = oldXmlSchema;
	}
	
	/**
	 * Determine whether the values are equal.  Uses a mapped type if available.
	 * Otherwise, a string comparison is made.
	 * 
	 * @param name
	 * @param values
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public boolean isEqual(String name, String[] values) 
		throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Class typeCl = (Class) typeTable.get(name);
		if (typeCl == null) return AbstractComparator.valuesEqual(values);
		
		Constructor ctor = typeCl.getConstructor(new Class[] { String.class });
		ctor.setAccessible(true);
		
		Object[] objs = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			objs[i] = ctor.newInstance(new Object[] { values[i] });
		}
		return AbstractComparator.valuesEqual(objs);
	}
	
	/**
	 * Manually add a mapping.  The equals method should be implemented, as well
	 * as a constructor that will take a String (the value).
	 * @param name The element or attribute name, with namespace
	 * @param type The class to create
	 */
	@SuppressWarnings("unchecked")
	public void addMapping(String name, Class typeCl)
	{
		typeTable.put(name, typeCl);
	}
	
	/**
	 * SAX handler that populates typeTable 
	 * @author MCCON012
	 */
	private class SchemaHandler
		extends DefaultHandler
	{
		/**
		 * Map the name to the type for elements and attributes
		 */
		public void startElement(
			String uri, String localName, String qName, Attributes attributes
		)
			throws SAXException
		{
			String type = attributes.getValue("type");
			String name = attributes.getValue("name");
			
			if (qName.equals("xs:include")) {
				String schemaLocation= attributes.getValue("schemaLocation");
				
				File xmlSchema = new File(
					XmlSchemaTypes.this.xmlSchema.getParentFile(), 
					schemaLocation
				);
				try {
					parseTypes(xmlSchema);
				} catch (Exception e) {
					throw new SAXException(e);
				}
			} else if (type != null && (qName.equals("xs:attribute") || qName.equals("xs:element"))) {
				Class typeCl = getClassForType(type);
				
				if (typeCl != null) addMapping(name, typeCl);
			}
		}
	}
	
	/**
	 * Default mapping of XML simple types to Java classes
	 * @param type
	 * @return
	 */
	protected Class getClassForType(String type)
	{
		// Integer
		if (type.equals("xs:positiveInteger")) return Integer.class;
		else if (type.equals("xs:negativeInteger")) return Integer.class;
		else if (type.equals("xs:integer")) return Integer.class;
		else if (type.equals("xs:int")) return Integer.class;

		// Double
		else if (type.equals("xs:double")) return Double.class;

		// Float
		else if (type.equals("xs:float")) return Float.class;
		else if (type.equals("xs:decimal")) return Float.class;
		
		// DateTime
		else if (type.equals("xs:dateTime")) return DateTimeType.class;		
		else if (type.equals("xs:date")) return DateTimeType.class;		

		// BooleanType
		else if (type.equals("xs:boolean")) return BooleanType.class;		

		return null;
	}
}