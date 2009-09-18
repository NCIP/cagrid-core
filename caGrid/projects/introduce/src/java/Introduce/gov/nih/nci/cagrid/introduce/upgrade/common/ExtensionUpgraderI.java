package gov.nih.nci.cagrid.introduce.upgrade.common;


public interface ExtensionUpgraderI {
	void execute() throws Exception;
	ExtensionUpgradeStatus getStatus();
}
