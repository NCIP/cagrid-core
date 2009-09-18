package gov.nih.nci.cagrid.introduce.common;

import java.util.StringTokenizer;


public class DomainNameVersionNamespaceToPackageMapper implements NamespaceToPackageMapper {

	public String getPackageName(String namespace) throws UnsupportedNamespaceFormatException {
		int index = namespace.indexOf("://");
		if (index == -1) {
			throw new UnsupportedNamespaceFormatException(
				"Namespace must be in the format PROTOCOL://DOMAIN/VERSION/NAME");
		} else {
			namespace = namespace.substring(index + 3);
			index = namespace.indexOf("/");
			if (index == -1) {
				throw new UnsupportedNamespaceFormatException(
					"Namespace must be in the format PROTOCOL://DOMAIN/VERSION/NAME");
			} else {
				String domain = namespace.substring(0, index);
				namespace = namespace.substring(index + 1);
				domain = domain.replace('/', '.');
				StringTokenizer tokenizer = new StringTokenizer(domain, ".", true);
				StringBuffer packageNameBuf = new StringBuffer();
				while (tokenizer.hasMoreElements()) {
					String nextToken = tokenizer.nextToken();
					if (nextToken.length() > 0) {
						char startingChar = nextToken.charAt(0);
						if (startingChar >= 48 && startingChar <= 57) {
							nextToken = "_" + nextToken;
						}
						packageNameBuf.insert(0, nextToken);
					}
				}
				index = namespace.indexOf("/");
				if (index == -1) {
					throw new UnsupportedNamespaceFormatException(
						"Namespace must be in the format PROTOCOL://DOMAIN/VERSION/NAME");
				} else {

					if (index == -1) {
						throw new UnsupportedNamespaceFormatException(
							"Namespace must be in the format PROTOCOL://DOMAIN/VERSION/NAME");
					} else {
						namespace = namespace.substring(index + 1);
						index = namespace.indexOf("/");
						if (index == -1) {
							String packageName = packageNameBuf.toString() + "." + namespace + ".bean";
							return packageName.toLowerCase();
						} else {
							throw new UnsupportedNamespaceFormatException(
								"Namespace must be in the format PROTOCOL://DOMAIN/VERSION/NAME");
						}
					}
				}
			}
		}
	}
}
