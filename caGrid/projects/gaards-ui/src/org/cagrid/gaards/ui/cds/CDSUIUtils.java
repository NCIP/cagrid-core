package org.cagrid.gaards.ui.cds;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.GroupDelegationPolicy;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.configuration.ServiceConfiguration;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.cagrid.grape.configuration.Services;


public class CDSUIUtils {

    private static Log log = LogFactory.getLog(CDSUIUtils.class);


    public static List<CDSHandle> getCDSServices() {
        List<CDSHandle> services = new ArrayList<CDSHandle>();
        try {
            ServiceConfiguration conf = (ServiceConfiguration) GridApplication.getContext().getConfigurationManager()
                .getConfigurationObject(CDSUIConstants.UI_CONF);
            Services s = conf.getServices();
            if (s != null) {
                ServiceDescriptor[] list = s.getServiceDescriptor();
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        CDSHandle handle = new CDSHandle(list[i]);
                        services.add(handle);
                    }
                }
            }

        } catch (Throwable e) {
            log.error(e);
        }
        return services;
    }


    public static DelegationPolicyPanel getPolicyPanel(String policyType, boolean editable) {
        if (policyType.equals(CDSUIConstants.IDENTITY_POLICY_TYPE)) {
            return new IdentityDelegationPolicyPanel(editable);
        } else if (policyType.equals(CDSUIConstants.GROUP_POLICY_TYPE)) {
            return new GroupDelegationPolicyPanel(editable);
        } else {
            return null;
        }
    }


    public static String getDelegationPolicyType(DelegationPolicy policy) {
        if (policy == null) {
            return "None";
        } else if (policy instanceof IdentityDelegationPolicy) {
            return CDSUIConstants.IDENTITY_POLICY_TYPE;
        } else if (policy instanceof GroupDelegationPolicy) {
            return CDSUIConstants.GROUP_POLICY_TYPE;
        } else {
            return policy.getClass().getName();
        }
    }
}
