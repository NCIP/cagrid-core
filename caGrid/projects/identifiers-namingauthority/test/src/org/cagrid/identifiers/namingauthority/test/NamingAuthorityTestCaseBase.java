package org.cagrid.identifiers.namingauthority.test;

import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;


public abstract class NamingAuthorityTestCaseBase extends NamingAuthorityIntegrationTestCaseBase {
    protected MaintainerNamingAuthority NamingAuthority;


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.NamingAuthority);
    }

}
