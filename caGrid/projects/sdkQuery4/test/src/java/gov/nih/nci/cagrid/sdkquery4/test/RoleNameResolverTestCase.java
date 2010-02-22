package gov.nih.nci.cagrid.sdkquery4.test;

import gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Assistant;
import gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Professor;
import gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Employee;
import gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Project;
import gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.Computer;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.sdkquery4.processor.RoleNameResolver;

import java.io.FileReader;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  RoleNameResolverTestCase
 *  Tests the role name resolver
 * 
 * @author David Ervin
 * 
 * @created Feb 13, 2008 1:59:59 PM
 * @version $Id: RoleNameResolverTestCase.java,v 1.2 2008-04-17 15:26:29 dervin Exp $ 
 */
public class RoleNameResolverTestCase extends TestCase {
    
    public static final Log LOG = LogFactory.getLog(RoleNameResolverTestCase.class);
    
    private RoleNameResolver resolver = null;
    
    public RoleNameResolverTestCase() {
        super();
    }
    
    
    public void setUp() {
        LOG.debug("Setting up test");
        try {
            // InputStream modelInStream = RoleNameResolverTestCase.class.getResourceAsStream(
            //    "/test/resources/sdkExampleDomainModel.xml");
            // InputStreamReader reader = new InputStreamReader(modelInStream);
            FileReader reader = new FileReader("test/resources/sdkExampleDomainModel.xml");
            DomainModel model = Utils.deserializeObject(reader, DomainModel.class);
            reader.close();
            resolver = new RoleNameResolver(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up role name resolver: " + ex.getMessage());
        }
    }
    
    
    public void testValidUnidirectionalAssociation() {
        String sourceName = Professor.class.getName();
        Association association = new Association();
        association.setName(Assistant.class.getName());
        try {
            String roleName = resolver.getRoleName(sourceName, association);
            assertNotNull("Unable to resolve valid unidirectional association", roleName);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to resolve valid unidirectional association: " + ex.getMessage());
        }
    }
    
    
    public void testValidBidirectionalAssociation() {
        String sourceName = Project.class.getName();
        Association association = new Association();
        association.setName(Employee.class.getName());
        try {
            String roleName = resolver.getRoleName(sourceName, association);
            assertNotNull("Unable to resolve valid bidirectional association", roleName);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to resolve valid bidirectional association: " + ex.getMessage());
        }
    }
    
    
    public void testNonExistantAssociation() {
        String sourceName = Project.class.getName();
        Association association = new Association();
        association.setName(Computer.class.getName());
        try {
            String roleName = resolver.getRoleName(sourceName, association);
            assertNull("Role name should not have resolved! (found " + roleName + ")", roleName);
        } catch (QueryProcessingException ex) {
            // expected
        }
    }
    
    
    public void testInvalidUnidirectionalAssociation() {
        String sourceName = Assistant.class.getName();
        Association association = new Association();
        association.setName(Professor.class.getName());
        try {
            String roleName = resolver.getRoleName(sourceName, association);
            assertNull("Role name resolver should not have resolved! (found " + roleName + ")", roleName);
        } catch (Exception ex) {
            // expected
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(BeanTypeDiscoveryTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
