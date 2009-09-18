package gov.nih.nci.cagrid.introduce.common;

public interface NamespaceToPackageMapper {

	public String getPackageName(String namespace) throws UnsupportedNamespaceFormatException;
}
