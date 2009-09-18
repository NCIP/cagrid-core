package gov.nih.nci.cagrid.introduce.codegen.common;

public class SynchronizationException extends Exception {

	public SynchronizationException(String message) {
		super(message);
	}


	public SynchronizationException(String message, Exception e) {
		super(message, e);
	}
}
