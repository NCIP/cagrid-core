import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.client.util.xml.JAXBMarshaller;
import gov.nih.nci.system.client.util.xml.JAXBUnmarshaller;
import gov.nih.nci.system.client.util.xml.Marshaller;
import gov.nih.nci.system.client.util.xml.Unmarshaller;
import gov.nih.nci.system.client.util.xml.XMLUtility;
import gov.nih.nci.system.client.util.xml.caCOREMarshaller;
import gov.nih.nci.system.client.util.xml.caCOREUnmarshaller;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;


public class TestCastorXMLClient extends TestClient
{
	public static void main(String args[])
	{
		TestCastorXMLClient client = new TestCastorXMLClient();
		try
		{
			client.testXMLUtility();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void testXMLUtility() throws Exception {
		// Application Service retrieval for secured system
		// ApplicationService appService =
		// ApplicationServiceProvider.getApplicationService("userId","password");

		ApplicationService appService = ApplicationServiceProvider
				.getApplicationService();
		Collection<Class> classList = getClasses();

		// Castor
		Marshaller marshaller = new caCOREMarshaller("xml-mapping.xml", false);
		Unmarshaller unmarshaller = new caCOREUnmarshaller(
				"unmarshaller-xml-mapping.xml", false);		

		// Castor
		XMLUtility myUtil = new XMLUtility(marshaller, unmarshaller);

		for (Class klass : classList) {
			if (!Modifier.isAbstract(klass.getModifiers())) {

				Object o = klass.newInstance();
				System.out.println("Searching for " + klass.getName());
				try {
					Collection results = appService.search(klass, o);
					int counter=0;
					for (Object obj : results) {
						counter++;
						boolean includeAssociations = true;
						Object convertedObj = XMLUtility.convertFromProxy(obj, includeAssociations);
						
						System.out.println("Printing Object prior to marshalling...");
						boolean includeAssocation = true;
						printObject(convertedObj, convertedObj.getClass(), includeAssocation);
						
//						File myFile = new File("./output/" + klass.getName()
//								+ "_test.xml");
						File myFile = new File("./output/" + convertedObj.getClass().getName()
								+ "_test"+counter+".xml");

						FileWriter myWriter = new FileWriter(myFile);

						//myUtil.toXML(convertedObj, myWriter, namespacePrefix);  // use this method to set the namespace prefix with each call
						myUtil.toXML(convertedObj, myWriter);
						myWriter.close();
						

						DocumentBuilder parser = DocumentBuilderFactory
								.newInstance().newDocumentBuilder();

						System.out.println("Can read " + myFile.getName()
								+ "? " + myFile.canRead());

						//Uncomment for independent validation when using Castor
//						Document document = parser.parse(myFile);
//						SchemaFactory factory = SchemaFactory
//								.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
//						try {
//							System.out.println("Validating " + convertedObj.getClass().getName()
//									+ " against the schema......\n\n");
//							Source schemaFile = new StreamSource(Thread
//									.currentThread().getContextClassLoader()
//									.getResourceAsStream(
//											convertedObj.getClass().getPackage().getName()
//													+ ".xsd"));
//							Schema schema = factory.newSchema(schemaFile);
//							Validator validator = schema.newValidator();
//
//							validator.validate(new DOMSource(document));
//							System.out.println(convertedObj.getClass().getName()
//									+ " has been validated!!!\n\n");
//						} catch (Exception e) {
//							System.out
//									.println(obj.getClass().getName()
//											+ " has failed validation!!!  Error reason is: \n\n"
//											+ e.getMessage());
//						}

						System.out.println("Un-marshalling " + convertedObj.getClass().getName()
								+ " from " + myFile.getName() + "......\n\n");

						// Castor 
						Object myObj = (Object)myUtil.fromXML(myFile);

						printObject(myObj, convertedObj.getClass(), includeAssocation);
						break;
					}
				} catch (Exception e) {
					System.out.println("Exception caught processing class "
							+ klass.getName() + ": ");
					e.printStackTrace();
				}
				//break;
			}
		}
	}
}