package gov.nih.nci.cagrid.sdkquery4.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.DomainTypesInformationUtil;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryEvent;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryEventListener;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryMapper;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  BeanTypeDiscoveryTest
 *  Test case for the bean type discovery utility
 * 
 * @author David Ervin
 * 
 * @created Jan 15, 2008 1:33:01 PM
 * @version $Id: BeanTypeDiscoveryTestCase.java,v 1.6 2008-04-17 15:26:29 dervin Exp $ 
 */
public class BeanTypeDiscoveryTestCase extends TestCase {

    public static final String BEANS_JAR_FILENAME = "build/remote-client/lib/example40-beans.jar";
    public static final String DOMAIN_MODEL_FILENAME = "test/resources/sdkExampleDomainModel.xml";
    public static final String GOLD_DOMAIN_TYPES_FILENAME = "test/resources/goldExampleDomainTypes.xml";

    private static final Log LOG = LogFactory.getLog(BeanTypeDiscoveryTestCase.class);
    
    private File beansJar = null;
    private DomainModel model = null;
    private DomainTypesInformation goldTypesInfo = null;

    public BeanTypeDiscoveryTestCase() {
        super();
    }
    
    
    public void setUp() {
        // inits the beans jar, domain model, and gold type info
        beansJar = new File(BEANS_JAR_FILENAME);
        assertTrue("Beans jar (" + beansJar.getAbsolutePath() + ") did not exist", beansJar.exists());
        assertTrue("Beans jar (" + beansJar.getAbsolutePath() + ") could not be read", beansJar.canRead());
        LOG.debug("Using beans jar " + beansJar.getAbsolutePath());
        
        try {
            File modelFile = new File(DOMAIN_MODEL_FILENAME);
            LOG.debug("Using domain model from " + modelFile.getAbsolutePath());
            FileReader modelReader = new FileReader(modelFile);
            model = Utils.deserializeObject(modelReader, DomainModel.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading domain model: " + ex.getMessage());
        }
        
        try {
            File goldTypesFile = new File(GOLD_DOMAIN_TYPES_FILENAME);
            LOG.debug("Using gold domain types from " + goldTypesFile.getAbsolutePath());
            FileReader reader = new FileReader(goldTypesFile);
            goldTypesInfo = DomainTypesInformationUtil.deserializeDomainTypesInformation(reader);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing gold domain types information document: " + ex.getMessage());
        }
    }
    
    
    public void testTypeInfoDiscovery() {
        BeanTypeDiscoveryMapper mapper = new BeanTypeDiscoveryMapper(beansJar, model);
        mapper.addBeanTypeDiscoveryEventListener(new BeanTypeDiscoveryEventListener() {
            public void typeDiscoveryBegins(BeanTypeDiscoveryEvent e) {
                LOG.debug("Discovering information for " + e.getBeanClassname() 
                    + " (" + e.getCurrentBean() + "/" + e.getTotalBeans() + ")");
            }
        });
        
        DomainTypesInformation typesInformation = null;
        try {
            typesInformation = mapper.discoverTypesInformation();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error processing domain types information: " + ex.getMessage());
        }        
        assertNotNull("Types information document was null", typesInformation);
        
        assertEquals("Processed types information did not match expected", goldTypesInfo, typesInformation);
    }
    
    
    public void testGetAttributeTypeNames() {
        // create a URL classloader for the beans jar
        URLClassLoader beansLoader = null;
        try {
            beansLoader = new URLClassLoader(new URL[] {beansJar.toURI().toURL()});
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating beans classloader: " + ex.getMessage());
        }
        
        DomainTypesInformationUtil infoUtil = new DomainTypesInformationUtil(goldTypesInfo);
        String testClassname = "gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Book";
        
        // load a class from the beans class loader
        Class<?> bookClass = null;
        try {
            bookClass = beansLoader.loadClass(testClassname);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading class " + testClassname + ": " + ex.getMessage());
        }
        
        // walk the fields, get names and types
        Field[] bookFields = bookClass.getDeclaredFields();
        for (Field field : bookFields) {
            int mods = field.getModifiers();
            if (!Modifier.isStatic(mods) && Modifier.isPublic(mods)) {
                String name = field.getName();
                String fieldJavaType = field.getType().getName();
                // ask the domain types info util about the field
                String infoJavaType = infoUtil.getAttributeJavaType(testClassname, name);
                assertNotNull("Unable to find field " + name + " of class " +
                    testClassname + " in domain types information", infoJavaType);
                assertEquals("Java type of field " + name + " of class " + 
                    testClassname + " differs in info from actual class",
                    fieldJavaType, infoJavaType);
            }
        }
    }
    
    
    public void testGetSubclassesFromTypeInfo() {        
        DomainTypesInformationUtil infoUtil = new DomainTypesInformationUtil(goldTypesInfo);
        String testClassname = "gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Payment";
        
        List<String> modelSubclasses = getSubclasses(testClassname);
        List<String> infoSubclasses = infoUtil.getSubclasses(testClassname);
        assertEquals("Unexpected number of subclasses from info utility", 
            modelSubclasses.size(), infoSubclasses.size());
        
        for (String expected : modelSubclasses) {
            assertTrue("Subclasses from info util did not contain expected class " + expected,
                infoSubclasses.contains(expected));
        }
    }
    
    
    private List<String> getSubclasses(String className) {
        List<String> subclasses = new ArrayList<String>();
        UMLGeneralization[] generalizations = model.getUmlGeneralizationCollection().getUMLGeneralization();
        for (UMLGeneralization gen : generalizations) {
            UMLClass superclass = DomainModelUtils.getReferencedUMLClass(model, gen.getSuperClassReference());
            if (getClassName(superclass).equals(className)) {
                UMLClass subClass = DomainModelUtils.getReferencedUMLClass(model, gen.getSubClassReference());
                String subName = getClassName(subClass);
                subclasses.add(subName);
                List<String> subsubclasses = getSubclasses(subName);
                subclasses.addAll(subsubclasses);
            }
        }
        return subclasses;
    }
    
    
    private String getClassName(UMLClass clazz) {
        return clazz.getPackageName() + "." + clazz.getClassName();
    }
    
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(BeanTypeDiscoveryTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
