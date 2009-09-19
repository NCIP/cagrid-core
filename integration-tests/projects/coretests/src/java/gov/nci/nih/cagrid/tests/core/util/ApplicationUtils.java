package gov.nci.nih.cagrid.tests.core.util;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;

import javax.xml.namespace.QName;

import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.dorian.idp.CountryCode;
import org.cagrid.gaards.dorian.idp.StateCode;


public class ApplicationUtils {
    private ApplicationUtils() {
    }


    public static Application readApplication(File file) throws Exception {
        return (Application) Utils.deserializeDocument(file.toString(), Application.class);
    }


    public static void writeApplication(Application application, File file) throws Exception {
        Utils.serializeDocument(file.toString(), application, new QName("application"));
    }


    public static Application generateExampleApplication() {
        Application application = new Application();

        application.setUserId("user1");
        application.setPassword("password1");
        application.setEmail("user1@someinsitution.edu");
        application.setPhoneNumber("800-800-8008");
        application.setFirstName("Test");
        application.setLastName("User1");
        application.setOrganization("Some Institution");
        application.setAddress("Box 1234");
        application.setCity("Some City");
        application.setState(StateCode.GA);
        application.setZipcode("98765");
        application.setCountry(CountryCode.US);

        return application;
    }


    public static void main(String[] args) throws Exception {
        // args = new String[] { "test" + File.separator + "resources" +
        // File.separator + "userApplications" + File.separator + "user.xml" };
        File file = new File(args[0]);

        Application application = generateExampleApplication();
        writeApplication(application, file);
        readApplication(file);
    }
}
