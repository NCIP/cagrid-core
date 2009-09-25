package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;

import java.rmi.RemoteException;
import java.util.List;

import org.cagrid.gaards.csm.bean.Application;
import org.globus.wsrf.security.SecurityManager;
import org.springframework.core.io.FileSystemResource;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class CSMImpl extends CSMImplBase {

    private CSM csm;

    public CSMImpl() throws RemoteException {
        super();
        try {
            String configFile = CSMConfiguration.getConfiguration().getCsmConfiguration();
            String propertiesFile = CSMConfiguration.getConfiguration().getCsmProperties();
            BeanUtils utils = new BeanUtils(new FileSystemResource(configFile), new FileSystemResource(propertiesFile));
            CSMProperties properties = utils.getCSMProperties();
            csm = new CSM(properties);
        } catch (Exception e) {
            FaultHelper.printStackTrace(e);
            throw new RemoteException(Utils.getExceptionMessage(e));
        }
    }

    private String getCallerIdentity() {
        String caller = SecurityManager.getManager().getCaller();
        // System.out.println("Caller: " + caller);
        if ((caller == null) || (caller.trim().length() == 0) || (caller.equals("<anonymous>"))) {
            caller = Constants.ANONYMOUS_CALLER;
        }
        return caller;
    }

  public org.cagrid.gaards.csm.bean.Application[] getApplications(org.cagrid.gaards.csm.bean.ApplicationSearchCriteria applicationSearchCriteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
        List<Application> apps = csm.getApplications(applicationSearchCriteria);
        Application[] result = new Application[apps.size()];
        return apps.toArray(result);
    }

  public org.cagrid.gaards.csm.bean.Application createApplication(org.cagrid.gaards.csm.bean.Application application) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
        return this.csm.createApplication(getCallerIdentity(), application);
    }

  public void removeApplication(long applicationId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
        this.csm.removeApplication(getCallerIdentity(), applicationId);
    }

}
