package org.cagrid.identifiers.namingauthority.test;

import javax.annotation.Resource;

import org.cagrid.identifiers.namingauthority.NamingAuthority;


public abstract class NamingAuthorityTestCaseBase extends NamingAuthorityIntegrationTestCaseBase {
    protected NamingAuthority NamingAuthority;


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.NamingAuthority);
    }

}
