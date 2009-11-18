package org.cagrid.identifiers.namingauthority;

/*
 * import junit.framework.TestResult; import junit.framework.TestSuite; import
 * junit.textui.TestRunner; import java.io.File; import java.io.FileReader;
 * import java.io.FileWriter; import java.io.InputStream; import
 * java.io.InputStreamReader; import java.io.Reader;
 */

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class NATestCase_OLD extends TestCase {
    private static Log log = LogFactory.getLog(NATestCase_OLD.class);

    private static String url1 = "http://cagrid.org";
    private static String url2 = "http://www.osu.edu";
    private static String epr1 = "end point reference 1";

    private static final IdentifierValues identifierValues;

    static {
        identifierValues = new IdentifierValues();
        identifierValues.add("URL", url1);
        identifierValues.add("URL", url2);
        identifierValues.add("EPR", epr1);
    }


    private boolean compare(IdentifierValues a, IdentifierValues b) {
        if (a == null && b == null) {
            return true;
        }
        if ((a == null && b != null) || (a != null && b == null)) {
            return true;
        }

        if (!a.getValues().keySet().equals(b.getValues().keySet())) {
            return false;
        }

        // keys (types) are the same, compare values now

        for (String type : a.getTypes()) {
            String[] aValues = a.getValues(type);
            String[] bValues = b.getValues(type);

            Arrays.sort(aValues);
            Arrays.sort(bValues);

            if (!Arrays.equals(aValues, bValues)) {
                return false;
            }
        }

        return true;
    }


    // ////////////////////////////////////////////////////////////////////////
    // TEST CASES START
    // ////////////////////////////////////////////////////////////////////////

    public void testIdenfierValues() {

        // //////////////////////////////////////////////////////////////////
        // Test getTypes()
        // //////////////////////////////////////////////////////////////////
        String[] types = identifierValues.getTypes();
        if (types.length != 2) {
            fail("Expected two data types (URL, EPR). Got " + types.length);
        }

        List<String> typeList = Arrays.asList(types);
        if (!typeList.contains("URL")) {
            fail("No URL found in getTypes() list");
        }
        if (!typeList.contains("EPR")) {
            fail("No EPR found in getTypes() list");
        }

        // //////////////////////////////////////////////////////////////////
        // Test getValues()
        // //////////////////////////////////////////////////////////////////
        Map<String, List<String>> map = identifierValues.getValues();
        if (!map.containsKey("URL")) {
            fail("No URL key found in map");
        }

        if (!map.containsKey("EPR")) {
            fail("No EPR key found in map");
        }

        // //////////////////////////////////////////////////////////////////
        // Test getValues(String type)
        // //////////////////////////////////////////////////////////////////
        String[] data = identifierValues.getValues("URL");
        if (data.length != 2) {
            fail("Expected 2 URLs, found " + data.length);
        }
        List<String> dataList = Arrays.asList(data);
        if (!dataList.contains(url1)) {
            fail(url1 + " not found in the data list");
        }
        if (!dataList.contains(url2)) {
            fail(url2 + " not found in the data list");
        }

        data = identifierValues.getValues("EPR");
        if (data.length != 1) {
            fail("Expected 1 EPR, found " + data.length);
        }
        if (!data[0].equals(epr1)) {
            fail(epr1 + " not found in the data list");
        }

        log.info("testIdenfierValues passed");
    }


    public void testNamingAuthority() throws Exception {
        NamingAuthority na = new NamingAuthorityLoader(new File("WebContent/WEB-INF/applicationContext-na.xml"),
            new File("test/resources/na.test.properties")).getNamingAuthority();

        // ////////////////////////////////////////////////////////
        // Test NA's initialize
        // ////////////////////////////////////////////////////////
        na.initialize();

        // ////////////////////////////////////////////////////////
        // Test NA's createIdentifier
        // ////////////////////////////////////////////////////////
        URI identifier = na.createIdentifier(identifierValues);
        log.info("createIdentifier: " + identifier);

        // ////////////////////////////////////////////////////////
        // Test NA's generateIdentifier
        // ////////////////////////////////////////////////////////
        log.debug("generateIdentifier: " + na.generateIdentifier());

        // ////////////////////////////////////////////////////////
        // Test NA's getConfiguration
        // ////////////////////////////////////////////////////////
        NamingAuthorityConfig config = na.getConfiguration();
        if (config == null) {
            fail("Retrieved NULL config from getConfiguration()");
        } else {
            log.info("prefix  = " + config.getPrefix());
            log.info("gridsvc = " + config.getGridSvcUrl());
        }

        // ////////////////////////////////////////////////////////
        // Test NA's getIdentifierGenerator
        // ////////////////////////////////////////////////////////
        IdentifierGenerator idGenerator = na.getIdentifierGenerator();
        if (idGenerator == null) {
            fail("Retrieved NULL IdentifierGenerator");
        } else {
            log.info("IdentifierGenerator.generate: " + idGenerator.generate(config));
        }

        // ////////////////////////////////////////////////////////
        // Test NA's resolveIdentifier
        // ////////////////////////////////////////////////////////
        IdentifierValues values = (IdentifierValues) na.resolveIdentifier(identifier);
        if (!compare(values, identifierValues)) {
            fail("Retrieved IdentifierValuesImpl does not match original values");
        }
    }


    public static void main(String[] args) {
        junit.textui.TestRunner.run(NATestCase_OLD.class);
    }
}
