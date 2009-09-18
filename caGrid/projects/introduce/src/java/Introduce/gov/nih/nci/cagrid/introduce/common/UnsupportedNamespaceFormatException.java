package gov.nih.nci.cagrid.introduce.common;

public class UnsupportedNamespaceFormatException extends Exception {

	public UnsupportedNamespaceFormatException(String s) {
		super(s);
	}


	public UnsupportedNamespaceFormatException(String s, Exception e) {
		super(s, e);
	}

}
