package gov.nih.nci.cagrid.bdt.service.globus.resource;

import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.ResourceHomeImpl;
import org.globus.wsrf.impl.SimpleResourceKey;

public class BaseResourceHome extends ResourceHomeImpl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public ResourceKey createBDTResource() throws Exception {
		
		// Create a resource and initialize it
		BDTResourceBase bdtr = (BDTResourceBase) createNewInstance();
		bdtr.initialize();

		// Get key
		ResourceKey key = new SimpleResourceKey(getKeyTypeName(), bdtr.getID());
		
		// Add the resource to the list of resources in this home
		add(key, bdtr);
		return key;
	}

}
