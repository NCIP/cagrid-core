package org.cagrid.gaards.dorian.service.globus.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.dorian.common.DorianConstants;
import org.cagrid.gaards.dorian.federation.TrustedIdentityProviders;
import org.cagrid.gaards.dorian.policy.DorianPolicy;
import org.cagrid.gaards.dorian.service.Dorian;
import org.cagrid.gaards.dorian.stubs.DorianResourceProperties;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;


/**
 * The implementation of this DorianResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class DorianResource extends DorianResourceBase {

    private Dorian dorian;
    private Log log;


    public DorianResource() {
        this.log = LogFactory.getLog(this.getClass().getName());
    }


    public ResourcePropertySet getResourcePropertySet() {
        ResourcePropertySet set = super.getResourcePropertySet();
        updateTrustedIdentityProviders(set);
        updateDorianPolicy(set);
        return set;
    }


    public org.cagrid.gaards.dorian.federation.TrustedIdentityProviders getTrustedIdentityProviders() {
        updateTrustedIdentityProviders(super.getResourcePropertySet());
        return ((DorianResourceProperties) getResourceBean()).getTrustedIdentityProviders();
    }


    private void updateTrustedIdentityProviders(ResourcePropertySet set) {
        try {
            TrustedIdentityProviders idps = this.dorian.getTrustedIdentityProviders();
            ResourceProperty prop = set.get(DorianConstants.TRUSTEDIDENTITYPROVIDERS);
            prop.set(0, idps);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public org.cagrid.gaards.dorian.policy.DorianPolicy getDorianPolicy() {
        updateDorianPolicy(super.getResourcePropertySet());
        return ((DorianResourceProperties) getResourceBean()).getDorianPolicy();
    }


    private void updateDorianPolicy(ResourcePropertySet set) {
        try {
            DorianPolicy policy = this.dorian.getDorianPolicy();
            ResourceProperty prop = set.get(DorianConstants.DORIANPOLICY);
            prop.set(0, policy);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public void setDorian(Dorian dorian) {
        this.dorian = dorian;
    }

}
