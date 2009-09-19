/*
 * Created on Jun 8, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.AntUtils;
import gov.nci.nih.cagrid.tests.core.util.FileUtils;
import gov.nci.nih.cagrid.tests.core.util.IntroduceServiceInfo;
import gov.nci.nih.cagrid.tests.core.util.SourceUtils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * This step creates a new Introduce service by running a series of ant commands on an introduce.xml 
 * and other files. 
 * @author Patrick McConnell
 */
public class ServiceCreateStep
	extends Step
{
	private File introduceDir;
	private File serviceDir;
	private String serviceName;
	private String pkg;
	private String namespace;
	private File serviceXmlDescriptor;
	private File schemaDir;
	private File[] schemas;
	private File implFile;
	private File[] jars;
	private File[] etcFiles;
	private Properties introduceProps;
	private File testDir;
	
	public ServiceCreateStep(File introduceDir, File testDir, File tmpDir) 
		throws ParserConfigurationException, SAXException, IOException
	{
		super();
		
		// set introduceDir
		this.introduceDir = introduceDir;
		this.testDir = testDir;
		
		// set serviceXmlDescriptor, serviceName, pkg, and namespace
		this.serviceXmlDescriptor = new File(testDir, IntroduceServiceInfo.INTRODUCE_SERVICEXML_FILENAME);
		IntroduceServiceInfo serviceInfo = new IntroduceServiceInfo(this.serviceXmlDescriptor);
		this.serviceName = serviceInfo.getServiceName();
		this.namespace = serviceInfo.getNamespace();
		this.pkg = serviceInfo.getPackageName();
		
		// set serviceDir
		this.serviceDir = new File(tmpDir, serviceName);
		this.serviceDir.mkdirs();
		
		// set schemas
		this.schemaDir = new File(testDir, "schema");
		if (schemaDir.exists()) {
			this.schemas = FileUtils.listRecursively(schemaDir, new FileFilter() {
				public boolean accept(File file) {
					return file.getName().endsWith(".xsd");
				}
			});
		} else {
			this.schemas = new File[0];
		}
		
		// set implFile
		this.implFile = new File(testDir, "src" + File.separator + serviceName + "Impl.java");
		
		// set libJars
		File libDir = new File(testDir, "lib");
		if (libDir.exists()) {
			this.jars = libDir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().endsWith(".jar");
				}
			});
		} else {
			this.jars = new File[0];
		}
		
		// set metadata file
		this.etcFiles = new File(testDir, "etc").listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		});
		
		introduceProps = new Properties();
		File introducePropFile = new File(testDir, "introduce.properties");
		if (introducePropFile.exists()) {
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(introducePropFile));
			introduceProps.load(is);
			is.close();
		}
	}
	
	public ServiceCreateStep(
		File introduceDir, File serviceDir, String serviceName, String pkg, String namespace, 
		File serviceXmlDescriptor, File schemaDir, File[] schemas, File implFile, File[] jars, File[] etcFiles, 
		Properties introduceProps
	) {
		super();
		
		this.introduceDir = introduceDir;
		this.serviceDir = serviceDir;
		this.serviceName = serviceName;
		this.pkg = pkg;
		this.namespace = namespace;
		this.serviceXmlDescriptor = serviceXmlDescriptor;
		this.schemas = schemas;
		this.schemaDir = schemaDir;
		this.implFile = implFile;
		this.jars = jars;
		this.etcFiles = etcFiles;
		this.introduceProps = introduceProps;
	}
	
	public void runStep() 
		throws IOException, InterruptedException, ParserConfigurationException, SAXException
	{
		// create skeleton
		createSkeleton();
		
		// copy schemas
		File schemaDir = new File(serviceDir, "schema" + File.separator + serviceName);
		schemaDir.mkdirs();
		for (File schema : schemas) {
			String path = schema.toString().substring(this.schemaDir.toString().length());
			if (path.startsWith("/") || path.startsWith("\\")) path = path.substring(1);
			File targetFile = new File(schemaDir, path);
			targetFile.getParentFile().mkdirs();
			FileUtils.copy(schema, targetFile);
		}
		
		// copy interface
		FileUtils.copy(serviceXmlDescriptor, new File(serviceDir, IntroduceServiceInfo.INTRODUCE_SERVICEXML_FILENAME));
		
		// copy service properties
		File serviceProperties = new File(testDir, "service.properties");
		if (serviceProperties.exists()) {
			FileUtils.copy(serviceProperties, new File(serviceDir, serviceProperties.getName()));
		}
		
		// copy jars
		File libDir = new File(serviceDir, "lib");
		libDir.mkdirs();
		for (File jar : jars) {
			FileUtils.copy(jar, new File(libDir, jar.getName()));
		}
		
		// copy metadata
		if (etcFiles != null) {
			File etcDir = new File(serviceDir, "etc");
			for (File file : etcFiles) {
				FileUtils.copy(file, new File(etcDir, file.getName()));
			}
		}

		// synchronize
		synchronizeSkeleton();
		
		// add implementation
		if (implFile.exists()) addImplementation();
		
		// rebuild
		buildSkeleton();
	}
	
	private void addImplementation() 
		throws ParserConfigurationException, SAXException, IOException
	{
		String targetPath = pkg.replace('.', File.separatorChar);		
		File targetJava = new File(serviceDir, "src" + File.separator + targetPath + File.separator + "service" + File.separator + serviceName + "Impl.java");
		
		// add method impl
		IntroduceServiceInfo info = new IntroduceServiceInfo(serviceXmlDescriptor);
		for (String methodName : info.getMethodNames()) {
			SourceUtils.modifyImpl(implFile, targetJava, methodName);
		}
		
		// add constructor impl
		SourceUtils.modifyImpl(implFile, targetJava, serviceName);
	}
	
	private void createSkeleton() 
		throws IOException, InterruptedException
	{
		//String cmd = "ant -D=BasicAnalyticalService -Dintroduce.skeleton.destination.dir=BasicAnalyticalService -Dintroduce.skeleton.package=edu.duke.test -Dintroduce.skeleton.package.dir=edu/duke/test -Dintroduce.skeleton.namespace.domain=http://cagrid.nci.nih.gov \"-Dintroduce.skeleton.extensions=\" createService";
		
		// create properties
		Properties sysProps = new Properties();
		sysProps.setProperty("introduce.skeleton.service.name", serviceName);
		sysProps.setProperty("introduce.skeleton.destination.dir", serviceDir.toString());
		sysProps.setProperty("introduce.skeleton.package", pkg);
		sysProps.setProperty("introduce.skeleton.package.dir", pkg.replace('.', '/'));
		sysProps.setProperty("introduce.skeleton.namespace.domain", namespace);
		sysProps.setProperty("introduce.skeleton.extensions", "");
		sysProps.setProperty("introduce.skeleton.resource.options", IntroduceConstants.INTRODUCE_MAIN_RESOURCE + "," + IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE + "," + IntroduceConstants.INTRODUCE_RESOURCEPROPETIES_RESOURCE);
		
		for (Object key : introduceProps.keySet()) {
			sysProps.setProperty((String) key, introduceProps.getProperty((String) key));
		}
		
		// invoke ant
		AntUtils.runAnt(introduceDir, null, IntroduceServiceInfo.INTRODUCE_CREATESERVICE_TASK, sysProps, null);
		// invoke ant
		AntUtils.runAnt(introduceDir, null, "postCreateService", sysProps, null);
	}
	
	private void synchronizeSkeleton() 
		throws IOException, InterruptedException
	{
		//String cmd = "ant -Dintroduce.skeleton.destination.dir=BasicAnalyticalService resyncService";
		
		// create properties
		Properties sysProps = new Properties();
		sysProps.setProperty("introduce.skeleton.destination.dir", serviceDir.toString());
		
		//SyncTools sync = new SyncTools(introduceDir);
		//try {
       //     sync.sync();
       // } catch (Exception e) {
            // TODO Auto-generated catch block
       //     e.printStackTrace();
       // }
		// invoke ant
		AntUtils.runAnt(introduceDir, null, IntroduceServiceInfo.INTRODUCE_RESYNCSERVICE_TASK, sysProps, null);
	}
	
	private void buildSkeleton() 
		throws IOException, InterruptedException
	{
		// invoke ant
		AntUtils.runAnt(serviceDir, null, "all", null, null);
	}
	
	public File getServiceDir()
	{
		return serviceDir;
	}

	public String getServiceName()
	{
		return serviceName;
	}
}
