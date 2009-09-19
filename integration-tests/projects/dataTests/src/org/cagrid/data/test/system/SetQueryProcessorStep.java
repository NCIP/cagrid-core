package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;


/**
 * SetQueryProcessorStep 
 * Step to set the service's query processor to my testing
 * one
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 8, 2006
 * @version $Id: SetQueryProcessorStep.java,v 1.5 2007/03/02 19:06:16 hastings
 *          Exp $
 */
public class SetQueryProcessorStep extends Step {

    private String serviceDir;


    public SetQueryProcessorStep(String serviceDir) {
        this.serviceDir = serviceDir;
    }


    public void runStep() throws Throwable {
        System.out.println("Running step: " + getClass().getName());
        String serviceModelFile = serviceDir + File.separator + IntroduceConstants.INTRODUCE_XML_FILE;
        ServiceDescription desc = null;
        try {
            desc = (ServiceDescription) Utils.deserializeDocument(serviceModelFile, ServiceDescription.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading service description: " + ex.getMessage());
        }
        if (desc == null) {
            throw new NullPointerException("Service description is NULL!");
        }
        // find the data service extension
        ExtensionType[] extensions = desc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension == null) {
            fail("Data service extension not found in service description");
        }

        // set service properties for the testing CQL Query Processor
        TestingCQLQueryProcessor testProc = new TestingCQLQueryProcessor();
        Properties testProperties = testProc.getRequiredParameters();
        // remove all current props for cql query processors
        ServicePropertiesProperty[] currentProperties = desc.getServiceProperties().getProperty();
        List<ServicePropertiesProperty> retainedPropereties = 
            new ArrayList<ServicePropertiesProperty>();
        for (int i = 0; i < currentProperties.length; i++) {
            if (!currentProperties[i].getKey().startsWith(DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX)) {
                retainedPropereties.add(currentProperties[i]);
            }
        }
        // create properties for the test QP's properties
        Enumeration testPropKeys = testProperties.keys();
        while (testPropKeys.hasMoreElements()) {
            String key = (String) testPropKeys.nextElement();
            String prefixedKey = DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + key;
            String defaultValue = testProperties.getProperty(key);
            String changedValue = defaultValue + "_CHANGED";
            ServicePropertiesProperty testProp = new ServicePropertiesProperty("", Boolean.FALSE, prefixedKey,
                changedValue);
            retainedPropereties.add(testProp);
        }

        // set the new properties in the service description
        ServicePropertiesProperty[] properties = new ServicePropertiesProperty[retainedPropereties.size()];
        retainedPropereties.toArray(properties);
        desc.getServiceProperties().setProperty(properties);

        // set the service property for the new query processor
        CommonTools.setServiceProperty(desc, DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY,
            TestingCQLQueryProcessor.class.getName(), false);
        // serialize the service model back to disk
        Utils.serializeDocument(serviceModelFile, desc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
}
