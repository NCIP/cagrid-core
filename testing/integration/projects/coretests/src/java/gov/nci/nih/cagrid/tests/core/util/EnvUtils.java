/*
 * Created on Apr 22, 2006
 */
package gov.nci.nih.cagrid.tests.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class EnvUtils {
	private EnvUtils() {
		super();
	}


	public static String[] parseEnvVar(String envVar) {
		int index = envVar.indexOf('=');
		if (index == -1) {
			throw new IllegalArgumentException("envVar " + envVar + " not of the form name=val");
		}
		return new String[]{envVar.substring(0, index), envVar.substring(index + 1)};
	}


	public static String[] overrideEnv(String[] envp) {
		Map<String, String> envm = new HashMap<String, String>(System.getenv());
		for (String element : envp) {
			String[] envVar = parseEnvVar(element);
			envm.put(envVar[0], envVar[1]);
		}
		envp = new String[envm.size()];
		Iterator<String> keys = envm.keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			String key = keys.next();
			envp[i++] = key + "=" + envm.get(key);
		}
		return envp;
	}
}
