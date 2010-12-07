package gov.nih.nci.cagrid.introduce.common;

public class UnsupportedNamespaceFormatException extends Exception {

	/**
     * Hash code for serialization.
     */
    private static final long serialVersionUID = -3394168995397280156L;


    public UnsupportedNamespaceFormatException(String s) {
		super(s);
	}


	public UnsupportedNamespaceFormatException(String s, Exception e) {
		super(s, e);
	}

}
