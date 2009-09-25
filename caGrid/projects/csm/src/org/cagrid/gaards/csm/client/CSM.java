package org.cagrid.gaards.csm.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;
import org.globus.gsi.GlobusCredential;


public class CSM {

    private String serviceURL;
    private GlobusCredential credential;
    private CSMClient client;


    public CSM(String serviceURL) throws Exception {
        this(serviceURL, null);
    }


    public CSM(String serviceURL, GlobusCredential credential) throws Exception {
        this.serviceURL = serviceURL;
        this.credential = credential;
        this.client = new CSMClient(this.serviceURL, this.credential);
        this.client.setAnonymousPrefered(false);
    }


    protected CSMClient getClient() {
        return client;
    }


    /**
     * This method get a list of application that the CSM Web Service manages
     * the access control policy for.
     * 
     * @param criteria
     *            The search criteria used to refine the application returned.
     * @return The list of application meeting the search criteria.
     * @throws RemoteException
     * @throws org.cagrid.gaards.csm.stubs.types.CSMInternalFault
     */

    public List<Application> getApplications(ApplicationSearchCriteria criteria) throws RemoteException,
        org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
        List<Application> list = new ArrayList<Application>();
        org.cagrid.gaards.csm.bean.Application[] result = getClient().getApplications(criteria);
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                list.add(new Application(this, result[i]));
            }
        }
        return list;
    }


    /**
     * This method enables one to create an application in CSM such that CSM may
     * manage the access control policy for that application.
     * 
     * @param name
     *            The name of the application
     * @param description
     *            the description of the application.
     * @return The remote application object representing the application.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */
    public Application createApplication(String name, String description) throws RemoteException, CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.Application app = new org.cagrid.gaards.csm.bean.Application();
        app.setName(name);
        app.setDescription(description);
        return new Application(this, getClient().createApplication(app));
    }


    /**
     * This method removes and application from CSM, in which case all the
     * access control policy for that application will be removed.
     * 
     * @param applicationId
     *            The id of the application to remove.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void removeApplication(long applicationId) throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        getClient().removeApplication(applicationId);
    }

}
