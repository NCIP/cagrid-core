package org.cagrid.gaards.pki;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class TrustUtils {

	public static List<TrustedCAFileListing> getTrustedCertificates() throws Exception {
		return getTrustedCertificates(Utils.getTrustedCerificatesDirectory());

	}


	public static List<TrustedCAFileListing> getTrustedCertificates(File dir) throws Exception {
		Map caListings = new HashMap<String, TrustedCAFileListing>();
		File[] list = dir.listFiles();
		for (int i = 0; i < list.length; i++) {
			String fn = list[i].getName();
			int index = fn.lastIndexOf(".");
			if (index == -1) {
				continue;
			}
			String name = fn.substring(0, index);
			String extension = fn.substring(index + 1);

			TrustedCAFileListing ca = (TrustedCAFileListing) caListings.get(name);
			if (ca == null) {
				ca = new TrustedCAFileListing(name);
				caListings.put(name, ca);
			}

			if (extension.matches("[0-9]+")) {
				ca.setFileId(Integer.valueOf(extension));
				ca.setCertificate(list[i]);
			} else if (extension.matches("[r]{1}[0-9]+")) {
				ca.setCRL(list[i]);
			} else if (extension.equals("signing_policy")) {
				ca.setSigningPolicy(list[i]);
			} else if (extension.indexOf("syncgts") != -1) {
				ca.setMetadata(list[i]);
			} else {
				continue;
			}

		}
		Iterator<TrustedCAFileListing> itr = caListings.values().iterator();
		List<TrustedCAFileListing> listings = new ArrayList<TrustedCAFileListing>();
		while (itr.hasNext()) {
			listings.add(itr.next());
		}
		return listings;
	}
}
