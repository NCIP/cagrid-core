package org.cagrid.mms.test;

import org.cagrid.mms.domain.SourceDescriptor;
import org.cagrid.mms.service.impl.MMS;
import org.cagrid.mms.service.impl.MMSGeneralException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(SpringTestApplicationContextConstants.MMS_BASE_LOCATION)
public abstract class MMSAbstractBaseTestCaseBase extends AbstractJUnit4SpringContextTests {
	
    @Test
    public void testMetadata() {
        try {
        	Assert.assertNotNull(applicationContext.getBean("mms"));
        	MMS mms = (MMS) applicationContext.getBean("mms");
            Assert.assertNotNull(mms.getModelSourceMetadata());
            String defaultSourceIdentifier = mms.getModelSourceMetadata().getDefaultSourceIdentifier();
            Assert.assertNotNull(defaultSourceIdentifier);

            Assert.assertNotNull("Cannot have a null list of supported sources!", mms.getModelSourceMetadata()
                .getSupportedModelSources().getSource());

            Assert.assertTrue("Cannot have an empty list of supported sources!", mms.getModelSourceMetadata()
                .getSupportedModelSources().getSource().length > 0);

            boolean found = false;
            for (SourceDescriptor desc : mms.getModelSourceMetadata().getSupportedModelSources().getSource()) {
            	Assert.assertNotNull(desc.getIdentifier());
                if (desc.getIdentifier().equals(defaultSourceIdentifier)) {
                    found = true;
                }
            }
            Assert.assertTrue("The default source identifer (" + defaultSourceIdentifier
                + ") was not found in the supported sources list!", found);

        } catch (MMSGeneralException e) {
            e.printStackTrace();
            Assert.fail("Problem accessing metadata:" + e.getMessage());
        }
    }

}