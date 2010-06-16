package org.cagrid.iso21090.portal.discovery;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;
import gov.nih.nci.iso21090.JAXBGenerationProcessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.iso21090.portal.discovery.ISO21090TypeSelectionComponent.ModificationException;
import org.jdom.Element;

public class FixISO21090IntroduceStubs {

	public static final String EXPECTED_INTRODUCE_VERSION = "1.3";
	public static final String SRC_DIR = "src";

	public void modifyDevBuildFileWithOperationStubsFixes(File svcDir) throws Exception {

		File introduceXml = new File(svcDir, IntroduceConstants.INTRODUCE_XML_FILE);
		Element root = XMLUtilities.fileNameToDocument(introduceXml.getAbsolutePath()).getRootElement();
		String introduceVersion = root.getAttributeValue("introduceVersion");
		if (introduceVersion == null) {
			throw new ModificationException("Could not find introduce version in " + IntroduceConstants.INTRODUCE_XML_FILE);
		}
		if (!EXPECTED_INTRODUCE_VERSION.equals(introduceVersion)) {
			// bail out here, unexpected version
			String msg = "Unexpected introduce version: " + introduceVersion;
			throw new ModificationException(msg);
		} else {
			// load up the beans
		    ServiceDescription serviceDesc = (ServiceDescription) Utils.deserializeDocument(
		        introduceXml.getAbsolutePath(), ServiceDescription.class);
			//TODO JDP how to determine location of extension.xml ???
			//NOTE: when this class is run from dev-build.xml, we don't have access to extension.xml
//			File extensionXml = new File("extension.xml");
//			FileReader extensionXmlReader = new FileReader(extensionXml);
//			ExtensionDescription type = (ExtensionDescription)Utils.deserializeObject(extensionXmlReader, ExtensionDescription.class);
//			editOperationStubs(type.getDiscoveryExtensionDescription(), serviceInfo);
			editOperationStubs(svcDir, serviceDesc);
		}

	}

	private void editOperationStubs(File serviceDir, ServiceDescription description) throws ModificationException{

		/*
		 * the basic algorithm is as follows:
		 * 1. determine if any operations use the INT ISO 21090 datatype
		 * 2. If so, determine the operation names and search through the generated stubs for that method
		 * 3. Fix the Introduce-generated stubs.
		 * 
		 * An example from a test service illustrates the problem:
		 * 1. Use the ISO types extension to add the ISO types, including type named "Int"
		 * 2. The "Int" type is in namespace "uri:iso.org:21090" and has classname "INT" in package "org.iso._21090"
		 * 3. In Introduce, add a new operation: int sendInt(int _int)
		 * This method has input with name _int and Type "Int" and output with type "Int"
		 * 
		 * When the user clicks save (without this extension installed), Introduce generates stubs with errors as follows:
		 * 1. In the service client source file, Introduce generates a method stub that's incorrect:
		 * 
		 *   public org.iso._21090.INT sendInt(org.iso._21090.INT _int) throws RemoteException {
		 *		synchronized(portTypeMutex){
		 *		configureStubSecurity((Stub)portType,"sendInt");
		 *		org.cagrid.helloworld.stubs.SendIntRequest params = new org.cagrid.helloworld.stubs.SendIntRequest();
		 *		org.cagrid.helloworld.stubs.SendIntRequest_int _intContainer = new org.cagrid.helloworld.stubs.SendIntRequest_int();
		 *		_intContainer.set_int(_int);
		 *		params.set_int(_intContainer);
		 *		org.cagrid.helloworld.stubs.SendIntResponse boxedResult = portType.sendInt(params);
		 *		return boxedResult.getInt();
		 *	 }
		 *
		 * The problem is with "return boxedResult.getInt();" which should read "return boxedResult.get_int();"
		 * to match the Axis-generated stub. For now, we can assume that the method is "get_int()".
		 * 
		 * There is a similar problem with the ProviderImpl
		 * 
		 *     public org.cagrid.helloworld.stubs.SendIntResponse sendInt(org.cagrid.helloworld.stubs.SendIntRequest params) throws RemoteException {
		 *		  org.cagrid.helloworld.stubs.SendIntResponse boxedResult = new org.cagrid.helloworld.stubs.SendIntResponse();
		 *		  boxedResult.setInt(impl.sendInt(params.get_int().get_int()));
		 *		  return boxedResult;
		 *	   }
		 *
		 * The problem is with "boxedResult.setInt(impl.sendInt(params.get_int().get_int()));" which should read
		 * "boxedResult.set_int(impl.sendInt(params.get_int().get_int()));"
		 * 
		 * Again, we can assume the correct method is "get_int".
		 * 
		 * Note that the *Client.java and *ProviderImpl.java files are broken for each context that has an
		 * operation that uses the INT datatype. Thus, we need to find all methods on all contexts and flag them
		 * for fixing.
		 * 
		 * This part of the algorithm is to search for .java files named: serviceContext + "Client.java" and serviceContext + "ProviderImpl.java"
		 * Then search within those files for the service operation that uses the INT type.
		 * The following code fixes this problem.
		 * 
		 */

		//phase 1: 1) get the iso 21090 namespace and broken element names and
		//2) create an array of QNames that we're searching for on service operations


		//TODO JDP get these properties from the schema directly
		/*
		String iso21090Namespace = null;
		Properties props = desc.getProperties();
		PropertiesProperty[] allPropertiesArray = props.getProperty();
		for (PropertiesProperty prop : allPropertiesArray) {
			if (prop.getKey().equals(Constants.EXTENSION_NAMESPACE_KEY)) {
				//get our namespace
				iso21090Namespace = prop.getValue();
			}
		}

		//check that iso21090Namespace is not null
		if (iso21090Namespace == null) {
			String msg = "Could not find ISO 21090 namespace in extension.xml under key" + Constants.EXTENSION_NAMESPACE_KEY; 
			throw new ModificationException(msg);
		}

		//get iso 21090 element name for INT datatype
		String iso21090IntElementName = null;
		props = desc.getProperties();
		allPropertiesArray = props.getProperty();
		for (PropertiesProperty prop : allPropertiesArray) {
			if (prop.getKey().equals(Constants.INT_ELEMENT_NAME_FROM_SCHEMA)) {
				//get our namespace
				iso21090IntElementName = prop.getValue();
			}
		}

		//check that iso21090IntElementName is not null
		if (iso21090IntElementName == null) {
			String msg = "Could not find ISO 21090 Int element property in extension.xml under key" + Constants.INT_ELEMENT_NAME_FROM_SCHEMA; 
			throw new ModificationException(msg);
		}
		*/
		
		String iso21090Namespace = "uri:iso.org:21090";
		String iso21090IntElementName = "Int";

		//create list of broken QNames that we need to fix in Introduce-generated stubs
		QName[] brokenQNames = new QName[] { new QName(iso21090Namespace, iso21090IntElementName) };

		//phase 2: identify service contexts with operations that use the broken types


		Map serviceContextsToFix = new HashMap(); //Map<ServiceType, List<String>>
		//NOTE: a ServiceType instance contains information about a service context
		//the above variable is of type Map<ServiceType, List<String>> where Map keys are ServiceType objects
		//and the values are List<String> objects that is a List of methods on the service context

		ServicesType servicesType = description.getServices();
		ServiceType[] allServiceTypes = servicesType.getService();
		for (ServiceType serviceType : allServiceTypes) {
			MethodsType methodsType = serviceType.getMethods();
			MethodType[] methodTypeArray = methodsType.getMethod();
			//now that we have all methods on this service, search every method's inputs
			//and return types for our brokenQNames
			for (MethodType methodType : methodTypeArray) {

				//determine if this is a matching method
				boolean match = isMatchingMethod(methodType, brokenQNames);

				if (match) {
					//found. flag this service context and operation for fixing
					//check if this serviceContextName is in our ArrayList. If not, create a new Map entry
					List<String> serviceContextEntry = null;
					if (serviceContextsToFix.containsKey(serviceType)) {
						//get entry
						serviceContextEntry = (List<String>)serviceContextsToFix.get(serviceType);
					} else {
						serviceContextEntry = new ArrayList<String>();
						serviceContextsToFix.put(serviceType, serviceContextEntry);
					}
					//TODO JDP may need to put a List<MethodInformation> in this map instead of List<String>
					//in order to perform more advanced processing of the source file via JaxMe
					String methodName = methodType.getName();
					serviceContextEntry.add(methodName);				
				}
			}
		}

		//phase 3: fix the files
		try {
			modifyBrokenFiles(serviceContextsToFix, serviceDir);
		} catch (IOException e) {
			String msg = "Problem modifying dev build for ISO 21090 operations workaround:" + e.getMessage(); 
			throw new ModificationException(msg);
		}
	}

	/**
	 * 
	 * @param serviceContextsToFix this is a Map<ServiceType, List<String>> where the values are List of service operations that match our QName filter
	 * @throws IOException 
	 */
	private void modifyBrokenFiles(Map serviceContextsToFix, File serviceBaseDir) throws IOException {
		//1. search the service for the *Client.java and *ProviderImpl.java Introduce-generated source files in the service
		File serviceSrcDir = new File(serviceBaseDir.getAbsolutePath() + File.separator + SRC_DIR);
		//search packages inside the "src" directory for the files
		//    	List<File> clientFilesToModify = new ArrayList<File>();
		//    	List<File> providerImplFilesToModify = new ArrayList<File>();

		Map clientFilesToModify = new HashMap(); //Map<File, String> where the File key is the actual file to modify and the value is the operation name in that file to modify 
		Map providerImplFilesToModify = new HashMap(); //Map<File,String> where the File key is the actual file to modify and the value is the operation name in that file to modify

		//make a directory from the service context package information
		Set<ServiceType> allBrokenServiceTypes = serviceContextsToFix.keySet();

		Iterator<ServiceType> i = allBrokenServiceTypes.iterator();

		while (i.hasNext()) {
			ServiceType curType = i.next();
			String curPackageName = curType.getPackageName();

			//search curPackageName + ".client" package for package + "Client.java" files
			String sourceFilename = curType.getName() + "Client.java";
			String clientPackageName = curPackageName + ".client";
			File clientFile = convertToFilename(serviceSrcDir, clientPackageName, sourceFilename);
			clientFilesToModify.put(clientFile, serviceContextsToFix.get(curType));

			//search package + ".service.globus" package for package + "ProviderImpl.java" files
			sourceFilename = curType.getName() + "ProviderImpl.java";
			String providerImplPackageName = curPackageName + ".service.globus";
			File providerImplFile = convertToFilename(serviceSrcDir, providerImplPackageName, sourceFilename);
			providerImplFilesToModify.put(providerImplFile, serviceContextsToFix.get(curType));
		}

		//now that we have the list of files, modify them.
		//2. Find the operation in the file and fix the code: getInt -> get_int and setInt -> set_int
		//in Client files, find the operation and change getInt -> get_int
		fixClientFiles(clientFilesToModify);

		//in ProviderImpl files, find the operation and change setInt -> set_int
		fixProviderImplFiles(providerImplFilesToModify);

	}

	private void fixClientFiles(Map clientFilesToModify) throws IOException {
		Set<File> files = (Set<File>)clientFilesToModify.keySet();

		Iterator<File> it = files.iterator();

		while (it.hasNext()) {
			//load file, find op, modify, and save
			File cur = it.next();

			String contents = JAXBGenerationProcessing.getContents(cur);
			//search for method declaration
			//    		String methodName = (String)clientFilesToModify.get(cur);
			String lineToFind = "boxedResult.getInt";
			String replacement = "boxedResult.get_int";
			if (contents.contains(lineToFind)) {
				int index = contents.indexOf(lineToFind);
				//change getInt -> get_int
				String updatedFileContents = contents.replaceAll(lineToFind, replacement);
				JAXBGenerationProcessing.setContents(cur, updatedFileContents);

			}

		}
	}

	/*
	static class MethodInformation {

		private String methodName;
		private JavaQName[] qnames;

		public MethodInformation(String methodName, JavaQName[] qnames) {
			this.methodName = methodName;
			this.qnames = qnames;
		}

		public String getMethodName() {
			return this.methodName;
		}

		public JavaQName[] getQNames() {
			return this.qnames;
		}

	}
	 */

	/*
	private void fixClientFiles(Map clientFilesToModify) throws IOException {
		Set<File> files = (Set<File>)clientFilesToModify.keySet();

		Iterator<File> it = files.iterator();

		while (it.hasNext()) {
			//load file, find op, modify, and save
			File cur = it.next();

			JavaSourceFactory factory = new JavaSourceFactory();
			JavaParser parser = new JavaParser(factory);
			List<JavaSource> classesParsed = parser.parse(cur);
			for (JavaSource s : classesParsed) {
				//search for @XmlRootElement annotation
				//if not present, add annotation
				//debug
				System.out.println(s);

				//find our method
				List<String> methodNames = (List<String>)clientFilesToModify.get(cur);

				for (String methodName : methodNames) {
					JavaMethod javaMethod = s.getMethod(methodName, null);
					JavaSource source = javaMethod.getJavaSource();
					String[] lines = javaMethod.getLines(javaMethod.getLevel());

					//now search these lines for our match
					//TODO JDP

					//save file
					source.write(new FileWriter(cur));

				}


			}
		}    
	 */

	private void fixProviderImplFiles(Map providerImplFilesToModify) throws IOException {
		Set<File> files = (Set<File>)providerImplFilesToModify.keySet();

		Iterator<File> it = files.iterator();

		while (it.hasNext()) {
			//load file, find op, modify, and save
			File cur = it.next();
			String contents = JAXBGenerationProcessing.getContents(cur);
			//search for method declaration
			//    		String methodName = (String)providerImplFilesToModify.get(cur);
			String lineToFind = "boxedResult.setInt";
			String replacement = "boxedResult.set_int";
			if (contents.contains(lineToFind)) {
				int index = contents.indexOf(lineToFind);
				//change getInt -> get_int
				String updatedFileContents = contents.replaceAll(lineToFind, replacement);
				JAXBGenerationProcessing.setContents(cur, updatedFileContents);
			}

		}

	}

	private File convertToFilename(File serviceSrcDir, String packageName, String sourceFilename) {
		String packageNameAsDirectoryString = packageName.replace('.', File.separatorChar);

		String completeFileNameAsString = serviceSrcDir.getAbsolutePath() + File.separator + packageNameAsDirectoryString + File.separator + sourceFilename;

		return new File(completeFileNameAsString);
	}

	/**
	 * Check if the given service method uses any of the broken QNames we're looking for
	 * @param methodType the service method
	 * @param brokenQNames broken qnames that we need to fix up
	 * @return true if any of the brokenQNames is used in the method, either as an input or as the output
	 */
	private boolean isMatchingMethod(MethodType methodType, QName[] brokenQNames) {
		//			List<MethodInformation> methodsList = new ArrayList<MethodInformation>();

		MethodTypeInputs inputs = methodType.getInputs();
		if (inputs != null) {
			MethodTypeInputsInput[] actualInputs = inputs.getInput();
			//Hmm... found when debugging that actualInputs can be null. Maybe another Introduce bug...
			if (actualInputs != null) {
				for (MethodTypeInputsInput actualInput : actualInputs) {
					QName qname = actualInput.getQName();
					for (QName curBrokenQName : brokenQNames) {
						if (qname.equals(curBrokenQName)) {
							return true;
						}
					}
				}
			}
		}

		MethodTypeOutput output = methodType.getOutput();
		if (output != null) {
			QName outputQName = output.getQName();
			for (QName curBrokenQName : brokenQNames) {
				if (outputQName.equals(curBrokenQName)) {
					return true;
				}
			}
		}

		return false;
	}

}
