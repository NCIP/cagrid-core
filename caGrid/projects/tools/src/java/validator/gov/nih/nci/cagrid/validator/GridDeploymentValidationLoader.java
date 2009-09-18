package gov.nih.nci.cagrid.validator;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceDescription;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceTestStep;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceTestStepConfigurationProperty;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceType;
import gov.nih.nci.cagrid.tests.core.beans.validation.ValidationDescription;
import gov.nih.nci.cagrid.validator.steps.AbstractBaseServiceTestStep;
import gov.nih.nci.cagrid.validator.steps.base.DeleteTempDirStep;
import gov.nih.nci.cagrid.validator.steps.base.PullWsdlStep;
import gov.nih.nci.cagrid.validator.steps.base.TestServiceMetaData;
import gov.nih.nci.cagrid.validator.steps.base.TestServiceUpStep;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.xml.namespace.QName;

/** 
 *  GridDeploymentValidationLoader
 *  Loader utility for grid deploymentm validation tests
 * 
 * @author David Ervin
 * 
 * @created Aug 27, 2007 3:04:08 PM
 * @version $Id: GridDeploymentValidationLoader.java,v 1.5 2008-11-12 23:36:16 jpermar Exp $ 
 */
public class GridDeploymentValidationLoader {
    
    public static final String VALIDATION_NAMESPACE = "http://gov.nih.nci.cagrid.tests.core.validation/ValidationDescription";
    public static final String VALIDATION_DESCRIPTION_ELEMENT = "ValidationDescription";
    public static final QName VALIDATION_DESCRIPTION_QNAME = 
        new QName(VALIDATION_NAMESPACE, VALIDATION_DESCRIPTION_ELEMENT);
    
    private static Map<String, File> tempDirsForServices = new HashMap();

    public static ValidationPackage loadValidationPackage(InputStream validationDescriptionStream) throws Exception {
        // load the validation description
        InputStreamReader descReader = new InputStreamReader(validationDescriptionStream);
        ValidationDescription desc = (ValidationDescription) 
            Utils.deserializeObject(descReader, ValidationDescription.class);
        descReader.close();
        
        return createValidationPackage(desc);
    }
    
    
    public static ValidationPackage createValidationPackage(ValidationDescription desc) throws Exception {
        final List<Story> serviceStories = new ArrayList(desc.getServiceDescription().length);
        for (ServiceDescription service : desc.getServiceDescription()) {
            Story serviceStory = createStoryForService(service, desc);
            serviceStories.add(serviceStory);
        }
        
        return new ValidationPackage(serviceStories, desc.getSchedule());
    }
    
    
    private static Story createStoryForService(final ServiceDescription service, final ValidationDescription desc) throws Exception {
        String testName = service.getServiceName() + " validation tests";
        String testDescription = "Tests for " + service.getServiceName() 
            + " @ " + service.getServiceUrl().toString();
        
        Vector<Step> setUp = createSetupStepsForService(service);
        final Vector<Step> tests = createStepsForServiceType(service, desc);
        Vector<Step> tearDown = createTearDownStepsForService(service);
        
        ServiceValidationStory serviceStory = 
            new ServiceValidationStory(testName, testDescription, setUp, tests, tearDown);
        return serviceStory;
    }
    
    
    private static Vector<Step> createSetupStepsForService(final ServiceDescription service) {
        Vector<Step> setup = new Vector();
        String rawUrl = service.getServiceUrl().toString();
        setup.add(new TestServiceUpStep(rawUrl));
        setup.add(new PullWsdlStep(rawUrl));
        if (rawUrl.contains("/cagrid/")) {
            // should only allow caGrid services to be tested for metadata
            setup.add(new TestServiceMetaData(rawUrl));
        }
        return setup;
    }
    
    
    private static Vector<Step> createStepsForServiceType(ServiceDescription service, ValidationDescription desc) 
        throws ValidationLoadingException {
        Vector<Step> steps = new Vector();
        Class[] constructorArgTypes = new Class[] {String.class, File.class, Properties.class};
        String serviceTypeName = service.getServiceType();
        for (ServiceType type : desc.getServiceType()) {
            if (type.getTypeName().equals(serviceTypeName)) {
                if (type.getTestStep() != null) {
                    for (ServiceTestStep testStep : type.getTestStep()) {
                        String classname = testStep.getClassname();
                        // find the test step class
                        Class stepClass = null;
                        try {
                            stepClass = Class.forName(classname);
                        } catch (ClassNotFoundException ex) {
                            throw new ValidationLoadingException(
                                "Could not service type " + serviceTypeName + " test class " 
                                + classname + ": " + ex.getMessage(), ex);
                        }
                        // verify its inheritance
                        if (!AbstractBaseServiceTestStep.class.isAssignableFrom(stepClass)) {
                            throw new ValidationLoadingException(
                                "The service type " + serviceTypeName + " test class " +
                                classname + " does not extend " + AbstractBaseServiceTestStep.class.getName());
                        }
                        // produce the configuration properties for the step
                        Properties configuration = new Properties();
                        if (testStep.getConfigurationProperty() != null) {
                            for (ServiceTestStepConfigurationProperty prop : testStep.getConfigurationProperty()) {
                                String value = "";
                                if (prop.getValue() != null) {
                                    value = prop.getValue();
                                }
                                configuration.setProperty(prop.getKey(), value);
                            }
                        }
                        // call the constructor
                        Object[] constructorArgs = {
                            service.getServiceUrl().toString(),
                            getTempDirForService(service.getServiceName()),
                            configuration
                        };
                        try {
                            Constructor stepConstructor = stepClass.getConstructor(constructorArgTypes);
                            AbstractBaseServiceTestStep step = (AbstractBaseServiceTestStep) 
                            stepConstructor.newInstance(constructorArgs);
                            steps.add(step);
                        } catch (Exception ex) {
                            throw new ValidationLoadingException(
                                "Service type " + serviceTypeName + " test class " + classname 
                                + " could not be created: " + ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
        
        // Haste Story dies if you have no steps
        if (steps.size() == 0) {
            steps.add(new Step() {
                
                public String getName() {
                    return "Dummy Step";
                }
                
                
                public void runStep() throws Throwable {
                    System.out.println("No testing steps defined for this service type");
                }
            });
        }
        return steps;
    }
    
    
    private static Vector<Step> createTearDownStepsForService(final ServiceDescription service) {
        Vector<Step> teardown = new Vector();
        teardown.add(new DeleteTempDirStep(getTempDirForService(service.getServiceName())));
        return teardown;
    }
    
    
    private static File getTempDirForService(String serviceName) {
        File dir = tempDirsForServices.get(serviceName);
        if (dir == null) {
            File tempDir = new File("tmp").getAbsoluteFile();
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            File serviceTemp = new File(tempDir.getAbsolutePath() + File.separator + serviceName);
            serviceTemp.mkdirs();
            tempDirsForServices.put(serviceName, serviceTemp);
            dir = serviceTemp;
        }
        return dir;
    }
}
