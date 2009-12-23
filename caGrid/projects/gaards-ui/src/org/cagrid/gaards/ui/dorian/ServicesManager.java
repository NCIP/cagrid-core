package org.cagrid.gaards.ui.dorian;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.RunnerGroup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.grape.GAARDSApplication;
import org.cagrid.grape.configuration.ServiceConfiguration;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.cagrid.grape.configuration.Services;


public class ServicesManager extends Runner {

    private static ServicesManager instance;

    private Log log;

    private List<DorianHandle> dorianServices;

    private Object mutex;
    private boolean firstRun;


    private ServicesManager() {
        this.log = LogFactory.getLog(getClass());
        this.dorianServices = new ArrayList<DorianHandle>();
        this.mutex = new Object();
        this.firstRun = true;
    }


    private void startup() {
        try {
            GAARDSApplication.getContext().getApplication().getThreadManager().executeInBackground(this);
        } catch (Exception e) {
            log.error(e);
        }
    }


    public synchronized static ServicesManager getInstance() {
        if (instance == null) {
            instance = new ServicesManager();
            instance.startup();
        }
        return instance;
    }


    public void execute() {
        while (true) {
            if (firstRun) {
                synchronized (mutex) {
                    syncServices();
                    firstRun = false;
                }
            } else {
                syncServices();
            }
            try {
                Thread.sleep(120000);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }


    public void syncWithUpdatedConfiguration() {
        synchronized (mutex) {
            if (!firstRun) {
                syncServices();
            }
        }
    }


    public synchronized void syncServices() {
        List<DorianHandle> dorians = new ArrayList<DorianHandle>();
        try {
            ServiceConfiguration conf = (ServiceConfiguration) GAARDSApplication.getContext().getConfigurationManager()
                .getActiveConfigurationObject(DorianUIConstants.DORIAN_UI_CONF);
            Services s = conf.getServices();
            if (s != null) {
                ServiceDescriptor[] list = s.getServiceDescriptor();
                if (list != null) {
                    RunnerGroup group = new RunnerGroup();
                    for (int i = 0; i < list.length; i++) {
                        try {
                            DorianHandle handle = new DorianHandle(list[i]);
                            dorians.add(handle);
                            group.add(new AuthenticationLookupThread(handle));
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                    GAARDSApplication.getContext().getApplication().getThreadManager().executeGroup(group);
                }
            }
        } catch (Throwable e) {
            log.error(e);
        }
        synchronized (mutex) {
            this.dorianServices = dorians;
        }
    }


    public DorianHandle getDorianHandle(String serviceURL) {
        synchronized (mutex) {
            for (int i = 0; i < dorianServices.size(); i++) {
                if (serviceURL.equals(dorianServices.get(i).getServiceURL())) {
                    return dorianServices.get(i);
                }
            }
            return null;
        }
    }


    public List<DorianHandle> getDorianServices() {
        synchronized (mutex) {
            return dorianServices;
        }
    }
}
