package gov.nih.nci.cagrid.authorization;

public interface GridAuthorizationManager {
	
	/**
	 * Returns true if the identity has the specified privilege on the 
	 * object identified by the specified objectId.
	 * 
	 * @param identity the grid identity
	 * @param objectId 
	 * @param privilege
	 * @return true if authorized, otherwise false
	 */
	public boolean isAuthorized(String identity, String objectId, String privilege);

}
