/*
 * Created on Jun 8, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.compare.BeanComparator;
import gov.nci.nih.cagrid.tests.core.util.FileUtils;
import gov.nci.nih.cagrid.tests.core.util.IntroduceServiceInfo;
import gov.nci.nih.cagrid.tests.core.util.ReflectionUtils;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.globus.gsi.GlobusCredential;


/**
 * This step invokes a grid service by loading client classes at runtime and
 * passing objects defined in files as parameters and comparing the results to
 * an object defined in a file.
 * 
 * @author Patrick McConnell
 */
public class ServiceInvokeStep extends Step {
	private File serviceDir;
	private String className;
	private String methodName;

	private Object[] params;
	private File methodDir;

	private File resultsFile;
	private String url;
	private GlobusCredential proxy;

	private Class cl;


	public ServiceInvokeStep(File serviceDir, File testDir, File methodDir, String methodName, String url)
		throws Exception {
		super();

		this.serviceDir = serviceDir;
		this.methodName = methodName;
		this.url = url;

		// set className
		File serviceXmlDescriptor = new File(testDir, IntroduceServiceInfo.INTRODUCE_SERVICEXML_FILENAME);
		if (!serviceXmlDescriptor.exists()) {
			serviceXmlDescriptor = new File(serviceDir, IntroduceServiceInfo.INTRODUCE_SERVICEXML_FILENAME);
		}
		IntroduceServiceInfo serviceInfo = new IntroduceServiceInfo(serviceXmlDescriptor);
		String serviceName = serviceInfo.getServiceName();
		String packageName = serviceInfo.getPackageName();
		this.className = packageName + ".client." + serviceName + "Client";

		// set cl, params, and resultsFile
		// File methodDir = new File(testDir + "test" + File.separator +
		// "resources" + File.separator + methodName);
		// this.cl = loadClass();
		// params = parseParams(methodDir);
		this.params = null;
		this.methodDir = methodDir;
		this.resultsFile = getResultsFile(methodDir);

		// TODO set proxy
		this.proxy = null;
	}


	public ServiceInvokeStep(File serviceDir, String className, String methodName, Object[] params, File resultsFile,
		String url, GlobusCredential proxy) {
		super();

		this.serviceDir = serviceDir;
		this.className = className;
		this.methodName = methodName;
		this.params = params;
		this.methodDir = null;
		this.url = url;
		this.proxy = proxy;
		this.resultsFile = resultsFile;
	}


	@Override
	public void runStep() throws Throwable {
		if (this.cl == null) {
			this.cl = loadClass();
		}
		if (this.params == null) {
			this.params = parseParams(this.methodDir);
		}

		// find method
		Method[] methods = ReflectionUtils.getMethodsByName(this.cl, this.methodName);
		assertTrue(methods.length > 0);
		Method m = methods[0];

		// create client
		Object obj = null;
		if (this.proxy != null) {
			Constructor cstor = this.cl.getConstructor(new Class[]{String.class, GlobusCredential.class});
			obj = cstor.newInstance(new Object[]{this.url, this.proxy});
		} else {
			Constructor cstor = this.cl.getConstructor(new Class[]{String.class});
			obj = cstor.newInstance(new Object[]{this.url});
		}

		// invoke client
		Object result = m.invoke(obj, this.params);

		// compare to results file
		validateResults(result);
	}


	private void validateResults(Object result) throws Exception {
		// simple results
		if (this.resultsFile == null) {
			return; // do nothing
		} else if (this.resultsFile.getName().endsWith(".txt")) {
			assertEquals(FileUtils.readText(this.resultsFile), String.valueOf(result));
		}
		// complex results
		else if (this.resultsFile.getName().endsWith(".xml")) {
			BeanComparator bc = new BeanComparator(this);
			bc.assertEquals(Utils.deserializeDocument(this.resultsFile.toString(), result.getClass()), result);
		}
		// anything else
		else {
			throw new IllegalArgumentException("results file " + this.resultsFile + " not a valid file type");
		}
	}


	private Class loadClass() throws MalformedURLException, ClassNotFoundException {
		return IntroduceServiceInfo.loadClass(this.serviceDir, this.className);
	}


	private Object[] parseParams(File dir) throws Exception {
		// get method
		Method[] methods = ReflectionUtils.getMethodsByName(this.cl, this.methodName);
		assertTrue(methods.length > 0);
		Method m = methods[0];

		// get param types and files
		Class[] paramTypes = m.getParameterTypes();
		File[] files = getParamFiles(dir);
		ArrayList<Object> params = new ArrayList<Object>(files.length);

		for (int i = 0; i < paramTypes.length; i++) {
			File file = files[i];
			Class paramType = paramTypes[i];

			Object obj = null;
			// simply type (*.txt)
			if (file.getName().endsWith(".txt")) {
				String s = FileUtils.readText(file);
				Constructor cstor = paramType.getConstructor(new Class[]{String.class});
				cstor.setAccessible(true);
				obj = cstor.newInstance(new Object[]{s});
			}
			// complext type (*.xml)
			else if (file.getName().endsWith(".xml")) {
				obj = Utils.deserializeDocument(file.toString(), paramType);
			}
			// don't understand anything else
			else {
				throw new IllegalArgumentException("param file " + file + " not .txt or .xml");
			}
			params.add(obj);
		}

		return params.toArray();
	}


	private File[] getParamFiles(File dir) {
		// get files
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return !file.getName().startsWith("out.") && file.isFile();
			}
		});

		// sort files
		ArrayList<File> fileList = new ArrayList<File>(files.length);
		for (File file : files) {
			fileList.add(file);
		}
		Collections.sort(fileList, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return parseParamPos(f1) - parseParamPos(f2);
			}
		});

		return fileList.toArray(new File[0]);
	}


	public static int parseParamPos(File file) {
		String name = file.getName();
		int index = name.indexOf('_');
		return Integer.parseInt(name.substring(0, index));
	}


	public static String parseParamName(File file) {
		String name = file.getName();
		int index = name.indexOf('_');
		return name.substring(index + 1);
	}


	private File getResultsFile(File methodDir) {
		String[] fileNames = methodDir.list(new FilenameFilter() {
			public boolean accept(File dir, String fileName) {
				return fileName.startsWith("out.") && (fileName.endsWith(".txt") || fileName.endsWith(".xml"));
			}
		});
		if (fileNames.length == 0) {
			return null; // throw new IllegalArgumentException("missing out
		}
		// file in " + methodDir);
		return new File(methodDir, fileNames[0]);
	}


	public static void main(String[] args) throws Throwable {
		File serviceDir = new File("C:\\tmp\\Service41220dir\\BasicAnalyticalServiceWithMetadata");
		String serviceName = "BasicAnalyticalServiceWithMetadata";
		File testDir = new File("test" + File.separator + "resources" + File.separator + "services" + File.separator
			+ serviceName);
		File methodDir = new File(testDir, "test\\resources\\0_reverseTranslate");
		ServiceInvokeStep step = new ServiceInvokeStep(serviceDir, testDir, methodDir, ServiceInvokeStep
			.parseParamName(methodDir), "http://localhost:8080/wsrf/services/cagrid/BasicAnalyticalServiceWithMetadata");
		step.runStep();
	}
}
