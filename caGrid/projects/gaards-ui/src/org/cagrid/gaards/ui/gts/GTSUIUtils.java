package org.cagrid.gaards.ui.gts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.configuration.ServiceConfiguration;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.cagrid.grape.configuration.Services;


public class GTSUIUtils {

    private static Log log = LogFactory.getLog(GTSUIUtils.class);

    public static List<GTSHandle> getGTSServices() {
        List<GTSHandle> services = new ArrayList<GTSHandle>();
        try {
            ServiceConfiguration conf = (ServiceConfiguration) GridApplication.getContext().getConfigurationManager()
                .getConfigurationObject(GTSUIConstants.UI_CONF);
            Services s = conf.getServices();
            if (s != null) {
                ServiceDescriptor[] list = s.getServiceDescriptor();
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        GTSHandle handle = new GTSHandle(list[i]);
                        services.add(handle);
                    }
                }
            }

        } catch (Throwable e) {
            log.error(e);
        }
        return services;
    }
}
