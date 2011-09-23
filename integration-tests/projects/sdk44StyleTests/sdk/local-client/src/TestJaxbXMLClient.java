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


public class TestJaxbXMLClient extends TestClient
{
	public static void main(String args[])
	{
		TestJaxbXMLClient client = new TestJaxbXMLClient ();
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

		ApplicationService appService = getApplicationService();
		Collection<Class> classList = getClasses();

		// JAXB
		boolean validate = true;
		boolean includeXmlDeclaration = true;
		String namespacePrefix = "gme://caCORE.caCORE/3.2/";
		String jaxbContextName = getJaxbContextName();
		//Marshaller marshaller = new JAXBMarshaller(includeXmlDeclaration,jaxbContextName);  // Use this constructor if you plan to pass the namespace prefix with each call instead
		Marshaller marshaller = new JAXBMarshaller(includeXmlDeclaration,jaxbContextName,namespacePrefix);
		Unmarshaller unmarshaller = new JAXBUnmarshaller(validate,jaxbContextName);		

		// JAXB
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

						System.out.println("Can read " + myFile.getName()
								+ "? " + myFile.canRead());

						System.out.println("Un-marshalling " + convertedObj.getClass().getName()
								+ " from " + myFile.getName() + "......\n\n");

						// JAXB
//						 Object myObj = (Object) myUtil.fromXML(convertedObj.getClass(), myFile);						// using class package name as a context
//						 Object myObj = (Object) myUtil.fromXML(convertedObj.getClass().getPackage().getName(), myFile);  // using package name as a context
						 Object myObj = (Object) myUtil.fromXML(myFile);  // using context name supplied during Unmarshaller instantiation

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

	public String getJaxbContextName() throws Exception
	{
		Collection<Class> classList = getClasses();
		Map<String,String> packageNames = new HashMap<String,String>();
		
		for (Class klass : classList){
			String packageName = klass.getPackage().getName();
			//System.out.println("package name: " + packageName);
			if (!packageName.equalsIgnoreCase("gov.nih.nci.cacoresdk.domain.interfaze.differentpackage")){
				packageNames.put(packageName, packageName);
			}
		}
		
		SortedSet<String> sortedset= new TreeSet<String>(packageNames.keySet());
			
		StringBuffer jaxbContextName = new StringBuffer();
		int totalCount = sortedset.size();
		int counter = 0;
		for (String packageName : sortedset){
			counter++;
			jaxbContextName.append(packageName);
			if (counter < totalCount)
				jaxbContextName.append(":");
		}
		
		System.out.println("jaxbContextName: "+jaxbContextName.toString());
		
		return jaxbContextName.toString();

	}	

}