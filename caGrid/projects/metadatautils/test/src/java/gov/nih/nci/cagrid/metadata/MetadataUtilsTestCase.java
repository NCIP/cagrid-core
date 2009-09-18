package gov.nih.nci.cagrid.metadata;

import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;


/**
 * @author oster
 */
public class MetadataUtilsTestCase extends TestCase {
    private static Log log = LogFactory.getLog(MetadataUtilsTestCase.class);
    
	private static final String DOMAIN_XML = "domainModel.xml";
	private static final String SERVICE_XML = "serviceMetadata.xml";


	public static void main(String[] args) {
		junit.textui.TestRunner.run(MetadataUtilsTestCase.class);
	}


	public void testServiceMetadataSerialization() {
		try {
			InputStream is = getClass().getResourceAsStream(SERVICE_XML);
			assertNotNull(is);

			ServiceMetadata model = MetadataUtils.deserializeServiceMetadata(new InputStreamReader(is));
			assertNotNull(model);

			File tmpFile = File.createTempFile("serviceMetadata", ".xml");
			tmpFile.deleteOnExit();
			FileWriter tmpFileWriter = new FileWriter(tmpFile);
			MetadataUtils.serializeServiceMetadata(model, tmpFileWriter);
			tmpFileWriter.close();
			log.debug("Wrote to file: " + tmpFile.getCanonicalPath());
			assertTrue(tmpFile.exists());

			Reader r = new FileReader(tmpFile);
			ServiceMetadata model2 = MetadataUtils.deserializeServiceMetadata(r);
			r.close();
			assertNotNull(model2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	public void testDomainModelSerialization() {
		try {
			InputStream is = getClass().getResourceAsStream(DOMAIN_XML);
			assertNotNull(is);

			DomainModel model = MetadataUtils.deserializeDomainModel(new InputStreamReader(is));
			assertNotNull(model);

			File tmpFile = File.createTempFile("domainModel", ".xml");
			tmpFile.deleteOnExit();
			FileWriter tmpFileWriter = new FileWriter(tmpFile);
			MetadataUtils.serializeDomainModel(model, tmpFileWriter);
			tmpFileWriter.close();
			log.debug("Wrote to file: " + tmpFile.getCanonicalPath());
			assertTrue(tmpFile.exists());

			Reader r = new FileReader(tmpFile);
			DomainModel model2 = MetadataUtils.deserializeDomainModel(r);
			r.close();
			assertNotNull(model2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
