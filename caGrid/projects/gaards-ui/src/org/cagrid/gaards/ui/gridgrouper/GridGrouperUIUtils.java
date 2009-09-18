package org.cagrid.gaards.ui.gridgrouper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.configuration.ServiceConfiguration;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.cagrid.grape.configuration.Services;


public class GridGrouperUIUtils {

	private static Logger log = Logger.getLogger(GridGrouperUIUtils.class);


    public static List<GridGrouperHandle> getGridGrouperServices() {
        List<GridGrouperHandle> services = new ArrayList<GridGrouperHandle>();
        try {
            ServiceConfiguration conf = (ServiceConfiguration) GridApplication.getContext().getConfigurationManager()
                .getConfigurationObject(GridGrouperUIConstants.UI_CONF);
            Services s = conf.getServices();
            if (s != null) {
                ServiceDescriptor[] list = s.getServiceDescriptor();
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        GridGrouperHandle handle = new GridGrouperHandle(list[i]);
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
