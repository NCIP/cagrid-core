package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.MetadataConstants;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.metadata.ResourcePropertyHelper;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.StringReader;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.cql2.extensionsupport.SupportedExtensions;
import org.cagrid.dataservice.cql.support.Cql2SupportType;
import org.cagrid.dataservice.cql.support.QueryLanguageSupport;
import org.cagrid.dataservice.cql.support.QueryLanguageSupportCQL2Support;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;

public class CheckCql2QueryLanguageSupportResourcePropertyStep extends Step {
    
    private ServiceContainer container = null;
    private TestCaseInfo dataServiceInfo = null;

    public CheckCql2QueryLanguageSupportResourcePropertyStep(ServiceContainer container, TestCaseInfo dataServiceInfo) {
        this.container = container;
        this.dataServiceInfo = dataServiceInfo;
    }


    public void runStep() throws Throwable {
        QueryLanguageSupport languageSupport = getQueryLanguageSupportResourceProperty();
        // the plain data service shouldn't have a CQL 2 query processor
        QueryLanguageSupportCQL2Support cql2Support = languageSupport.getCQL2Support();
        assertNotNull("No CQL 2 support was defined in the resource property", cql2Support);
        Cql2SupportType supportType = cql2Support.getSupport();
        assertEquals("CQL 2 should not be supported by " + dataServiceInfo.getName() + 
            " and the resource property should explicitly state this", 
            Cql2SupportType.ImplementationNotProvided, supportType);
        // shouldn't be any supported extensions either
        SupportedExtensions supportedExtensions = cql2Support.getSupportedExtensions();
        if (supportedExtensions != null) {
            // that's interesting...
            assertTrue("Unexpected CQL 2 attribute extensions found!", supportedExtensions.getAttributeExtension() == null || supportedExtensions.getAttributeExtension().length == 0);
            assertTrue("Unexpected CQL 2 modifier extensions found!", supportedExtensions.getModifierExtension() == null || supportedExtensions.getModifierExtension().length == 0);
            assertTrue("Unexpected CQL 2 object extensions found!", supportedExtensions.getObjectExtension() == null || supportedExtensions.getObjectExtension().length == 0);
            assertTrue("Unexpected CQL 2 result extensions found!", supportedExtensions.getResultExtension() == null || supportedExtensions.getResultExtension().length == 0);
        }
    }
    
    
    private QueryLanguageSupport getQueryLanguageSupportResourceProperty() {
        EndpointReferenceType epr = null;
        try {
            epr = container.getServiceEPR("cagrid/" + dataServiceInfo.getName());
        } catch (MalformedURIException ex) {
            ex.printStackTrace();
            fail("Error obtaining service EPR: " + ex.getMessage());
        }
        Element resourceProperty = null;
        try {
            resourceProperty = ResourcePropertyHelper.getResourceProperty(
                epr, MetadataConstants.QUERY_LANGUAGE_SUPPORT_QNAME);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error reading resource property: " + ex.getMessage());
        }
        // deserialize the resource property
        QueryLanguageSupport support = null;
        try {
            support = Utils.deserializeObject(new StringReader(XmlUtils.toString(resourceProperty)), QueryLanguageSupport.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing query language support document: " + ex.getMessage());
        }
        return support;
    }
}
