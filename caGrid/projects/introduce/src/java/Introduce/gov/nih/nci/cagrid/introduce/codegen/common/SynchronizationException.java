package gov.nih.nci.cagrid.introduce.codegen.common;

public class SynchronizationException extends Exception {

	/**
     * Hash code for serialization.
     */
    private static final long serialVersionUID = -6640132366031695789L;


    public SynchronizationException(String message) {
		super(message);
	}


	public SynchronizationException(String message, Exception e) {
		super(message, e);
	}
}
