package gov.nih.nci.cagrid.graph.uml;

public class Attribute {
	public static final int PROTECTED = 0;
	public static final int PUBLIC = 1;
	public static final int PRIVATE = 2;

	protected int access;
	protected String type;
	protected String name;


	public Attribute(int access, String type, String name) {
		this.access = access;
		this.type = type;
		this.name = name;
	}


	public String toString() {
		return " + " + name + ": " + type;
	}
}
