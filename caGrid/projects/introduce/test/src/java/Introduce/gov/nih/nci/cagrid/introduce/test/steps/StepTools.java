package gov.nih.nci.cagrid.introduce.test.steps;

import java.io.File;

import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.util.JavaParser;


public class StepTools {

	public static boolean methodExists(String fileName, String methodNames) throws Exception {
		return methodsExists(fileName, new String[]{methodNames});
	}


	public static boolean methodsExists(String fileName, String[] methodNames) throws Exception {

		JavaSource sourceI;

		JavaSourceFactory jsf;

		JavaParser jp;

		jsf = new JavaSourceFactory();
		jp = new JavaParser(jsf);

		jp.parse(new File(fileName));
		sourceI = (JavaSource) jsf.getJavaSources().next();
		sourceI.setForcingFullyQualifiedName(true);

		System.out.println(sourceI.getClassName());

		JavaMethod[] methods = sourceI.getMethods();

		boolean found = true;
		for (int i = 0; i < methodNames.length; i++) {
			String methodName = methodNames[i];
			boolean foundThisOne = false;
			for (int j = 0; j < methods.length; j++) {
				if (methods[j].getName().equals(methodName)) {
					foundThisOne = true;
				}
			}
			if (foundThisOne != true) {
				found = false;
			}
		}

		sourceI = null;
		jsf = null;
		jp = null;
		System.gc();

		return found;
	}
}
