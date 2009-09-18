package gov.nih.nci.cagrid.common.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.globus.gsi.GlobusCredential;
import org.globus.util.ConfigUtil;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class ProxyUtil {
	public static void saveProxy(GlobusCredential proxy, String file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		proxy.save(fos);
		fos.close();
	}


	public static GlobusCredential getDefaultProxy() throws Exception {
		return loadProxy(ConfigUtil.discoverProxyLocation());
	}


	public static void destroyDefaultProxy() {
		File f = new File(ConfigUtil.discoverProxyLocation());
		f.delete();
	}


	public static GlobusCredential loadProxy(String location) throws Exception {
		FileInputStream fis = new FileInputStream(location);
		GlobusCredential proxy = new GlobusCredential(fis);
		return proxy;
	}


	public static void deleteDefaultProxy() {
		File f = new File(ConfigUtil.discoverProxyLocation());
		f.delete();
	}


	public static void saveProxyAsDefault(GlobusCredential proxy) throws Exception {
		saveProxy(proxy, ConfigUtil.discoverProxyLocation());
	}
}
