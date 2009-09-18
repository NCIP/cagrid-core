package org.cagrid.mms.test;

import org.cagrid.mms.domain.SourceDescriptor;
import org.cagrid.mms.service.impl.MMS;
import org.cagrid.mms.service.impl.MMSGeneralException;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;


public abstract class MMSAbstractBaseTestCaseBase extends AbstractAnnotationAwareTransactionalTests {

    protected MMS mms;


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.mms);
    }


    public MMSAbstractBaseTestCaseBase() {
        setPopulateProtectedVariables(true);
    }


    public void testMetdata() {
        try {
            assertNotNull(this.mms.getModelSourceMetadata());
            String defaultSourceIdentifier = this.mms.getModelSourceMetadata().getDefaultSourceIdentifier();
            assertNotNull(defaultSourceIdentifier);

            assertNotNull("Cannot have a null list of supported sources!", this.mms.getModelSourceMetadata()
                .getSupportedModelSources().getSource());

            assertTrue("Cannot have an empty list of supported sources!", this.mms.getModelSourceMetadata()
                .getSupportedModelSources().getSource().length > 0);

            boolean found = false;
            for (SourceDescriptor desc : this.mms.getModelSourceMetadata().getSupportedModelSources().getSource()) {
                assertNotNull(desc.getIdentifier());
                if (desc.getIdentifier().equals(defaultSourceIdentifier)) {
                    found = true;
                }
            }
            assertTrue("The default source identifer (" + defaultSourceIdentifier
                + ") was not found in the supported sources list!", found);

        } catch (MMSGeneralException e) {
            e.printStackTrace();
            fail("Problem accessing metadata:" + e.getMessage());
        }
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{SpringTestApplicationContextConstants.MMS_BASE_LOCATION};
    }
}