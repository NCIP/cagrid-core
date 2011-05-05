package edu.emory.cci.cagrid.migration;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * Copy jars and the service directory for a deployed CaGrid service from the
 * old tomcat container to a new tomcat container, substituting newer caGrid
 * jars as seems appropriate. Also update the service's introduceDeployment.xml
 * file to reflect changes in file names.
 * 
 * @author Mark Grand
 */
@SuppressWarnings("deprecation")
public class Cagrid1_3TomcatToNewCagridTomcat {
	private static final String NEW_CAGRID_VERSION = "1.4.1";

	private static final File OBSOLETE = new File("");

	private static Map<String, File> supersededJarFileMap;

	// initialize map from jar file names to superseding files.
	private static void init(String cagrid_home) throws IOException {
		File cagridHome = new File(cagrid_home);
		if (!cagridHome.isDirectory()) {
			String msg = cagridHome.getAbsolutePath() + " does not exist or is not a directory.";
			throw new IOException(msg);
		}
		File integrationRepository = new File(cagridHome, "integration-repository");
		File cagridIntegrationRepository = new File(integrationRepository, "caGrid");

		File tavernaWorkflowService = new File(cagridIntegrationRepository, "TavernaWorkflowService");
		File tavernaWorkflowServiceLib = new File(tavernaWorkflowService, NEW_CAGRID_VERSION);

		// Build the map of superseded .jar file names to superseding .jar files
		// using Google's ImmutableMap to catch to possible bug of having
		// multiple values for the same key.
		ImmutableMap.Builder<String, File> mapBuilder = ImmutableMap.builder();

		mapBuilder.put("caGrid-TavernaWorkflowService-client-1.3.jar", //
				new File(tavernaWorkflowServiceLib, "caGrid-TavernaWorkflowService-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-TavernaWorkflowService-common-1.3.jar", //
				new File(tavernaWorkflowServiceLib, "caGrid-TavernaWorkflowService-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-TavernaWorkflowService-service-1.3.jar", //
				new File(tavernaWorkflowServiceLib, "caGrid-TavernaWorkflowService-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-TavernaWorkflowService-stubs-1.3.jar", //
				new File(tavernaWorkflowServiceLib, "caGrid-TavernaWorkflowService-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-TavernaWorkflowService-tests-1.3.jar", //
				new File(tavernaWorkflowServiceLib, "caGrid-TavernaWorkflowService-tests-" + NEW_CAGRID_VERSION + ".jar"));

		// WorkflowFactoryService is no longer supported and so is not
		// superseded.

		File advertisement = new File(cagridIntegrationRepository, "advertisement");
		File advertisementLib = new File(advertisement, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-advertisement-1.3.jar", //
				new File(advertisementLib, "caGrid-advertisement-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-advertisement-tests-1.3.jar", //
				new File(advertisementLib, "caGrid-advertisement-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File authenticationService = new File(cagridIntegrationRepository, "authentication-service");
		File authenticationServiceLib = new File(authenticationService, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-authentication-client-1.3.jar", //
				new File(authenticationServiceLib, "caGrid-authentication-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-authentication-common-1.3.jar", //
				new File(authenticationServiceLib, "caGrid-authentication-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-authentication-service-1.3.jar", //
				new File(authenticationServiceLib, "caGrid-authentication-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-authentication-stubs-1.3.jar", //
				new File(authenticationServiceLib, "caGrid-authentication-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-authentication-tests-1.3.jar", //
				new File(authenticationServiceLib, "caGrid-authentication-tests-" + NEW_CAGRID_VERSION + ".jar"));

		// authz was dropped in 1.4

		// bulkDataTransfer was dropped in 1.4

		File cabigextensions = new File(cagridIntegrationRepository, "authentication-service");
		File cabigextensionsLib = new File(cabigextensions, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-cabigextensions-common-1.3.jar", //
				new File(cabigextensionsLib, "caGrid-cabigextensions-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-cabigextensions-stubs-1.3.jar", //
				new File(cabigextensionsLib, "caGrid-cabigextensions-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-cabigextensions-tests-1.3.jar", //
				new File(cabigextensionsLib, "caGrid-cabigextensions-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File cadsr = new File(cagridIntegrationRepository, "cadsr");
		File cadsrLib = new File(cadsr, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-caDSR-1.3.jar", //
				new File(cadsrLib, "caGrid-caDSR-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-caDSR-tests-1.3.jar", //
				new File(cadsrLib, "caGrid-caDSR-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File cdsService = new File(cagridIntegrationRepository, "authentication-service");
		File cdsServiceLib = new File(cdsService, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-cds-client-1.3.jar", //
				new File(cdsServiceLib, "caGrid-cds-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-cds-common-1.3.jar", //
				new File(cdsServiceLib, "caGrid-cds-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-cds-service-1.3.jar", //
				new File(cdsServiceLib, "caGrid-cds-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-cds-stubs-1.3.jar", //
				new File(cdsServiceLib, "caGrid-cds-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-cds-tests-1.3.jar", //
				new File(cdsServiceLib, "caGrid-cds-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File core = new File(cagridIntegrationRepository, "core");
		File coreLib = new File(core, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-core-1.3.jar", //
				new File(coreLib, "caGrid-core-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-core-resources-1.3.jar", //
				new File(coreLib, "caGrid-core-resources-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-core-tests-1.3.jar", //
				new File(coreLib, "caGrid-core-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File cql = new File(cagridIntegrationRepository, "cql");
		File cqlLib = new File(cql, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-CQL-cql.1.0-1.3.jar", //
				new File(cqlLib, "caGrid-CQL-cql.1.0-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-CQL-cql.2.0-1.3.jar", //
				new File(cqlLib, "caGrid-CQL-cql.2.0-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-CQL-tests-1.3.jar", //
				new File(cqlLib, "caGrid-CQL-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File csmAuthExtension = new File(cagridIntegrationRepository, "csm-auth-extension");
		File csmAuthExtensionLib = new File(csmAuthExtension, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-csm-auth-extension-1.3.jar", //
				new File(csmAuthExtensionLib, "caGrid-csm-auth-extension-" + NEW_CAGRID_VERSION + ".jar"));

		File data = new File(cagridIntegrationRepository, "data");
		File dataLib = new File(data, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-data-common-1.3.jar", //
				new File(dataLib, "caGrid-data-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-data-cql-1.3.jar", //
				new File(dataLib, "caGrid-data-cql-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-data-service-1.3.jar", //
				new File(dataLib, "caGrid-data-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-data-stubs-1.3.jar", //
				new File(dataLib, "caGrid-data-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-data-tests-1.3.jar", //
				new File(dataLib, "caGrid-data-tests-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-data-utils-1.3.jar", //
				new File(dataLib, "caGrid-data-utils-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-data-validation-1.3.jar", //
				new File(dataLib, "caGrid-data-validation-" + NEW_CAGRID_VERSION + ".jar"));

		File dataExtensions = new File(cagridIntegrationRepository, "dataExtensions");
		File dataExtensionsLib = new File(dataExtensions, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-dataExtensionsLib-core-1.3.jar", //
				new File(dataExtensionsLib, "caGrid-dataExtensionsLib-core-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dataExtensionsLib-sdkstyle-1.3.jar", //
				new File(dataExtensionsLib, "caGrid-dataExtensionsLib-sdkstyle-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dataExtensionsLib-tests-1.3.jar", //
				new File(dataExtensionsLib, "caGrid-dataExtensionsLib-tests-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dataExtensionsLib-ui-1.3.jar", //
				new File(dataExtensionsLib, "caGrid-dataExtensionsLib-ui-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dataExtensionsLib-upgrades-1.3.jar", //
				new File(dataExtensionsLib, "caGrid-dataExtensionsLib-upgrades-" + NEW_CAGRID_VERSION + ".jar"));

		File discovery = new File(cagridIntegrationRepository, "discovery");
		File discoveryLib = new File(discovery, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-discovery-1.3.jar", //
				new File(discoveryLib, "caGrid-discovery-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-discovery-tests-1.3.jar", //
				new File(discoveryLib, "caGrid-discovery-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File dorian = new File(cagridIntegrationRepository, "dorian");
		File dorianLib = new File(dorian, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-dorian-client-1.3.jar", //
				new File(dorianLib, "caGrid-dorian-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dorian-common-1.3.jar", //
				new File(dorianLib, "caGrid-dorian-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dorian-service-1.3.jar", //
				new File(dorianLib, "caGrid-dorian-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dorian-stubs-1.3.jar", //
				new File(dorianLib, "caGrid-dorian-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-dorian-tests-1.3.jar", //
				new File(dorianLib, "caGrid-dorian-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File enforceAuthExtension = new File(cagridIntegrationRepository, "enforce-auth-extension");
		File enforceAuthExtensionLib = new File(enforceAuthExtension, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-enforce-auth-extension-1.3.jar", //
				new File(enforceAuthExtensionLib, "caGrid-enforce-auth-extension-" + NEW_CAGRID_VERSION + ".jar"));

		File fqp = new File(cagridIntegrationRepository, "fqp");
		File fqpLib = new File(fqp, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-fqp-client-1.3.jar", //
				new File(fqpLib, "caGrid-fqp-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-fqp-common-1.3.jar", //
				new File(fqpLib, "caGrid-fqp-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-fqp-service-1.3.jar", //
				new File(fqpLib, "caGrid-fqp-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-fqp-stubs-1.3.jar", //
				new File(fqpLib, "caGrid-fqp-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-fqp-tests-1.3.jar", //
				new File(fqpLib, "caGrid-fqp-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File gaardsCore = new File(cagridIntegrationRepository, "gaards-core");
		File gaardsCoreLib = new File(gaardsCore, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-gaards-core-1.3.jar", //
				new File(gaardsCoreLib, "caGrid-gaards-core-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gaards-core-tests-1.3.jar", //
				new File(gaardsCoreLib, "caGrid-gaards-core-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File gaardsUi = new File(cagridIntegrationRepository, "gaards-ui");
		File gaardsUiLib = new File(gaardsUi, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-gaards-ui-1.3.jar", //
				new File(gaardsUiLib, "caGrid-gaards-ui-" + NEW_CAGRID_VERSION + ".jar"));

		File globalModelExchange = new File(cagridIntegrationRepository, "globalModelExchange");
		File globalModelExchangeLib = new File(globalModelExchange, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-globalModelExchange-client-1.3.jar", //
				new File(globalModelExchangeLib, "caGrid-globalModelExchange-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-globalModelExchange-common-1.3.jar", //
				new File(globalModelExchangeLib, "caGrid-globalModelExchange-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-globalModelExchange-service-1.3.jar", //
				new File(globalModelExchangeLib, "caGrid-globalModelExchange-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-globalModelExchange-stubs-1.3.jar", //
				new File(globalModelExchangeLib, "caGrid-globalModelExchange-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-globalModelExchange-tests-1.3.jar", //
				new File(globalModelExchangeLib, "caGrid-globalModelExchange-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File globalModelExchangeUi = new File(cagridIntegrationRepository, "globalModelExchange-ui");
		File globalModelExchangeUiLib = new File(globalModelExchangeUi, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-gglobalModelExchange-ui-1.3.jar", //
				new File(globalModelExchangeUiLib, "caGrid-globalModelExchange-ui-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-globalModelExchange-ui-tests-1.3.jar", //
				new File(globalModelExchangeUiLib, "caGrid-globalModelExchange-ui-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File grape = new File(cagridIntegrationRepository, "grape");
		File grapeLib = new File(grape, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-grape-1.3.jar", //
				new File(grapeLib, "caGrid-grape-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-grape-resources-1.3.jar", //
				new File(grapeLib, "caGrid-grape-resources-" + NEW_CAGRID_VERSION + ".jar"));

		File graph = new File(cagridIntegrationRepository, "graph");
		File graphLib = new File(graph, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-graph-1.3.jar", //
				new File(graphLib, "caGrid-graph-" + NEW_CAGRID_VERSION + ".jar"));

		// gridftpauthz is dropped in 1.4+

		File gridgrouper = new File(cagridIntegrationRepository, "gridgrouper");
		File gridgrouperLib = new File(gridgrouper, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-gridgrouper-client-1.3.jar", //
				new File(gridgrouperLib, "caGrid-gridgrouper-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-common-1.3.jar", //
				new File(gridgrouperLib, "caGrid-gridgrouper-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-resources-1.3.jar", //
				new File(gridgrouperLib, "caGrid-gridgrouper-resources-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-service-1.3.jar", //
				new File(gridgrouperLib, "caGrid-gridgrouper-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-stubs-1.3.jar", //
				new File(gridgrouperLib, "caGrid-gridgrouper-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-tests-1.3.jar", //
				new File(gridgrouperLib, "caGrid-gridgrouper-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File gridgrouperAuthExtension = new File(cagridIntegrationRepository, "gridgrouper-auth-extension");
		File gridgrouperAuthExtensionLib = new File(gridgrouperAuthExtension, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-gridgrouper-auth-extension-1.3.jar", //
				new File(gridgrouperAuthExtensionLib, "caGrid-gridgrouper-auth-extension-" + NEW_CAGRID_VERSION + ".jar"));

		File gts = new File(cagridIntegrationRepository, "gts");
		File gtsLib = new File(gts, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-gts-client-1.3.jar", //
				new File(gtsLib, "caGrid-gts-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gts-common-1.3.jar", //
				new File(gtsLib, "caGrid-gts-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gts-service-1.3.jar", //
				new File(gtsLib, "caGrid-gts-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gts-stubs-1.3.jar", //
				new File(gtsLib, "caGrid-gts-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gts-tests-1.3.jar", //
				new File(gtsLib, "caGrid-gts-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File installer = new File(cagridIntegrationRepository, "installer");
		File installerLib = new File(installer, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-installer-1.3.jar", //
				new File(installerLib, "caGrid-installer-" + NEW_CAGRID_VERSION + ".jar"));

		File introduce = new File(cagridIntegrationRepository, "introduce");
		File introduceLib = new File(introduce, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-introduce-core-1.3.jar", //
				new File(introduceLib, "caGrid-introduce-core-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-introduce-core-tests-1.3.jar", //
				new File(introduceLib, "caGrid-introduce-core-tests-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-introduce-portal-1.3.jar", //
				new File(introduceLib, "caGrid-introduce-portal-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-introduce-portal-tests-1.3.jar", //
				new File(introduceLib, "caGrid-introduce-portal-tests-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-introduce-updater-1.3.jar", //
				new File(introduceLib, "caGrid-introduce-updater-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-introduce-updater-tests-1.3.jar", //
				new File(introduceLib, "caGrid-introduce-updater-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File introduceBuildtools = new File(cagridIntegrationRepository, "introduce-buildtools");
		File introduceBuildtoolsLib = new File(introduceBuildtools, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-introduce-buildTools-1.3.jar", //
				new File(introduceBuildtoolsLib, "caGrid-introduce-buildTools-" + NEW_CAGRID_VERSION + ".jar"));

		File introduceClienttools = new File(cagridIntegrationRepository, "introduce-clienttools");
		File introduceClienttoolsLib = new File(introduceClienttools, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-Introduce-ClientTools-Notification-1.3.jar", //
				new File(introduceClienttoolsLib, "caGrid-Introduce-ClientTools-Notification-" + NEW_CAGRID_VERSION + ".jar"));

		File introduceServicetools = new File(cagridIntegrationRepository, "introduce-servicetools");
		File introduceServicetoolsLib = new File(introduceServicetools, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-Introduce-serviceTools-1.3.jar", //
				new File(introduceServicetoolsLib, "caGrid-Introduce-serviceTools-" + NEW_CAGRID_VERSION + ".jar"));

		File metadata = new File(cagridIntegrationRepository, "metadata");
		File metadataLib = new File(metadata, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-metadata-common-1.3.jar", //
				new File(metadataLib, "caGrid-metadata-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-metadata-data-1.3.jar", //
				new File(metadataLib, "caGrid-metadata-data-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-metadata-security-1.3.jar", //
				new File(metadataLib, "caGrid-metadata-security-" + NEW_CAGRID_VERSION + ".jar"));

		File metadataValidator = new File(cagridIntegrationRepository, "metadata-validator");
		File metadataValidatorLib = new File(metadataValidator, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-metadata-validator-1.3.jar", //
				new File(metadataValidatorLib, "caGrid-metadata-validator-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-metadata-validator-tests-1.3.jar", //
				new File(metadataValidatorLib, "caGrid-metadata-validator-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File metadatautils = new File(cagridIntegrationRepository, "metadatautils");
		File metadatautilsLib = new File(metadatautils, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-metadatautils-1.3.jar", //
				new File(metadatautilsLib, "caGrid-metadatautils-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-metadatautils-tests-1.3.jar", //
				new File(metadatautilsLib, "caGrid-metadatautils-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File mms = new File(cagridIntegrationRepository, "mms");
		File mmsLib = new File(mms, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-mms-client-1.3.jar", //
				new File(mmsLib, "caGrid-mms-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-mms-common-1.3.jar", //
				new File(mmsLib, "caGrid-mms-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-mms-service-1.3.jar", //
				new File(mmsLib, "caGrid-mms-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-mms-stubs-1.3.jar", //
				new File(mmsLib, "caGrid-mms-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-mms-tests-1.3.jar", //
				new File(mmsLib, "caGrid-mms-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File opensaml = new File(cagridIntegrationRepository, "opensaml");
		File opensamlLib = new File(opensaml, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-opensaml-1.3.jar", //
				new File(opensamlLib, "caGrid-opensaml-" + NEW_CAGRID_VERSION + ".jar"));

		// sdkQuery is dropped in 1.4+

		// sdkQuery32 is dropped in 1.4+

		File sdkQuery4 = new File(cagridIntegrationRepository, "sdkQuery4");
		File sdkQuery4Lib = new File(sdkQuery4, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-sdkQuery4-beans-1.3.jar", //
				new File(sdkQuery4Lib, "caGrid-sdkQuery4-beans-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-sdkQuery4-processor-1.3.jar", //
				new File(sdkQuery4Lib, "caGrid-sdkQuery4-processor-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-sdkQuery4-style-1.3.jar", //
				new File(sdkQuery4Lib, "caGrid-sdkQuery4-style-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-sdkQuery4-tests-1.3.jar", //
				new File(sdkQuery4Lib, "caGrid-sdkQuery4-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File sdkQuery41 = new File(cagridIntegrationRepository, "sdkQuery41");
		File sdkQuery41Lib = new File(sdkQuery41, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-sdkQuery41-beans-1.3.jar", //
				new File(sdkQuery41Lib, "caGrid-sdkQuery41-beans-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-sdkQuery41-processor-1.3.jar", //
				new File(sdkQuery41Lib, "caGrid-sdkQuery41-processor-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-sdkQuery41-style-1.3.jar", //
				new File(sdkQuery41Lib, "caGrid-sdkQuery41-style-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-sdkQuery41-tests-1.3.jar", //
				new File(sdkQuery41Lib, "caGrid-sdkQuery41-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File serviceSecurityProvider = new File(cagridIntegrationRepository, "service-security-provider");
		File serviceSecurityProviderLib = new File(serviceSecurityProvider, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-ServiceSecurityProvider-client-1.3.jar", //
				new File(serviceSecurityProviderLib, "caGrid-ServiceSecurityProvider-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-ServiceSecurityProvider-common-1.3.jar", //
				new File(serviceSecurityProviderLib, "caGrid-ServiceSecurityProvider-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-ServiceSecurityProvider-service-1.3.jar", //
				new File(serviceSecurityProviderLib, "caGrid-ServiceSecurityProvider-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-ServiceSecurityProvider-stubs-1.3.jar", //
				new File(serviceSecurityProviderLib, "caGrid-ServiceSecurityProvider-stubs-" + NEW_CAGRID_VERSION + ".jar"));

		File serviceTools = new File(cagridIntegrationRepository, "service-tools");
		File serviceToolsLib = new File(serviceTools, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-service-tools-db-1.3.jar", //
				new File(serviceToolsLib, "caGrid-service-tools-db-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-service-tools-events-1.3.jar", //
				new File(serviceToolsLib, "caGrid-service-tools-events-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-service-tools-groups-1.3.jar", //
				new File(serviceToolsLib, "caGrid-service-tools-groups-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-service-tools-tests-1.3.jar", //
				new File(serviceToolsLib, "caGrid-service-tools-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File serviceWebappExtension = new File(cagridIntegrationRepository, "service-webapp-extension");
		File serviceWebappExtensionLib = new File(serviceWebappExtension, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-service-webapp-extension-1.3.jar", //
				new File(serviceWebappExtensionLib, "caGrid-service-webapp-extension-" + NEW_CAGRID_VERSION + ".jar"));

		File syncgts = new File(cagridIntegrationRepository, "syncgts");
		File syncgtsLib = new File(syncgts, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-syncgts-client-1.3.jar", //
				new File(syncgtsLib, "caGrid-syncgts-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-syncgts-common-1.3.jar", //
				new File(syncgtsLib, "caGrid-syncgts-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-syncgts-service-1.3.jar", //
				new File(syncgtsLib, "caGrid-syncgts-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-syncgts-stubs-1.3.jar", //
				new File(syncgtsLib, "caGrid-syncgts-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-syncgts-tests-1.3.jar", //
				new File(syncgtsLib, "caGrid-syncgts-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File testing = new File(cagridIntegrationRepository, "testing");
		File testingLib = new File(testing, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-testing-core-1.3.jar", //
				new File(testingLib, "caGrid-testing-core-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-testing-system-1.3.jar", //
				new File(testingLib, "caGrid-testing-system-" + NEW_CAGRID_VERSION + ".jar"));

		File tools = new File(cagridIntegrationRepository, "tools");
		File toolsLib = new File(tools, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-tools-stubs-1.3.jar", //
				new File(toolsLib, "caGrid-tools-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-tools-tests-1.3.jar", //
				new File(toolsLib, "caGrid-tools-tests-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-tools-validator-1.3.jar", //
				new File(toolsLib, "caGrid-tools-validator-" + NEW_CAGRID_VERSION + ".jar"));

		File transfer = new File(cagridIntegrationRepository, "transfer");
		File transferLib = new File(transfer, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-Transfer-client-1.3.jar", //
				new File(transferLib, "caGrid-Transfer-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-Transfer-common-1.3.jar", //
				new File(transferLib, "caGrid-Transfer-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-Transfer-service-1.3.jar", //
				new File(transferLib, "caGrid-Transfer-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-Transfer-stubs-1.3.jar", //
				new File(transferLib, "caGrid-Transfer-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-Transfer-tests-1.3.jar", //
				new File(transferLib, "caGrid-Transfer-tests-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-Transfer-webapp-1.3.jar", //
				new File(transferLib, "caGrid-Transfer-webapp-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-TransferExtension-1.3.jar", //
				new File(transferLib, "caGrid-TransferExtension-" + NEW_CAGRID_VERSION + ".jar"));

		File websso = new File(cagridIntegrationRepository, "websso");
		File webssoLib = new File(websso, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-websso-core-1.3.jar", //
				new File(webssoLib, "caGrid-websso-core-" + NEW_CAGRID_VERSION + ".jar"));

		File webssoClient = new File(cagridIntegrationRepository, "websso-client");
		File webssoClientLib = new File(webssoClient, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-testing-client-1.3.jar", //
				new File(webssoClientLib, "caGrid-websso-client-" + NEW_CAGRID_VERSION + ".jar"));

		File webssoClientAcegi = new File(cagridIntegrationRepository, "websso-client-acegi");
		File webssoClientAcegiLib = new File(webssoClientAcegi, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-testing-client-acegi-1.3.jar", //
				new File(webssoClientAcegiLib, "caGrid-websso-client-acegi-" + NEW_CAGRID_VERSION + ".jar"));

		File webssoClientCommon = new File(cagridIntegrationRepository, "websso-client-common");
		File webssoClientCommonLib = new File(webssoClientCommon, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-testing-client-common-1.3.jar", //
				new File(webssoClientCommonLib, "caGrid-websso-client-common-" + NEW_CAGRID_VERSION + ".jar"));

		// websso-client-common-conf.jar is dropped in caGrid 1.4+

		File webssoClientLiferay = new File(cagridIntegrationRepository, "websso-client-liferay");
		File webssoClientLiferayLib = new File(webssoClientLiferay, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-testing-client-liferay-1.3.jar", //
				new File(webssoClientLiferayLib, "caGrid-websso-client-liferay-" + NEW_CAGRID_VERSION + ".jar"));

		File wizard = new File(cagridIntegrationRepository, "wizard");
		File wizardLib = new File(wizard, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-wizard-1.3.jar", //
				new File(wizardLib, "caGrid-wizard-" + NEW_CAGRID_VERSION + ".jar"));

		File wsEnum = new File(cagridIntegrationRepository, "wsEnum");
		File wsEnumLib = new File(wsEnum, NEW_CAGRID_VERSION);
		mapBuilder.put("caGrid-wsEnum-1.3.jar", //
				new File(wsEnumLib, "caGrid-wsEnum-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-wsEnum-tests-1.3.jar", //
				new File(wsEnumLib, "caGrid-wsEnum-tests-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-wsEnum-stubs-1.3.jar", //
				new File(wsEnumLib, "caGrid-wsEnum-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-wsEnum-test-stubs-1.3.jar", //
				new File(wsEnumLib, "caGrid-wsEnum-test-stubs-" + NEW_CAGRID_VERSION + ".jar"));

		File repository = new File(cagridHome, "repository");

		File acegisecurityOrg = new File(repository, "acegisecurity");
		File acegisecurityModule = new File(acegisecurityOrg, "acegisecurity");
		File acegisecurityLib = new File(acegisecurityModule, "1.0.7");
		mapBuilder.put("acegi-security-1.0.4.jar", //
				new File(acegisecurityLib, "acegi-security-1.0.7.jar"));

		File antlrOrg = new File(repository, "antlr");
		File antlrModule = new File(antlrOrg, "antlr");
		File antlr2_7_6 = new File(antlrModule, "2.7.6");
		mapBuilder.put("antlr-2.7.5.jar", //
				new File(antlr2_7_6, "antlr-2.7.6.jar"));
		mapBuilder.put("antlr-2.7.6rc1.jar", //
				new File(antlr2_7_6, "antlr-2.7.6.jar"));

		File apache = new File(repository, "apache");

		File commonsCodec = new File(apache, "commons-codec");
		File commonsCodec1_3 = new File(commonsCodec, "1.3");
		mapBuilder.put("commons-codec-1.2.jar", //
				new File(commonsCodec1_3, "commons-codec-1.3.jar"));

		File commonsCollections = new File(apache, "commons-collections");
		File commonsCollections3_2 = new File(commonsCollections, "3.2");
		mapBuilder.put("commons-collections-2.1.1.jar", // 2.1.1 is so old it
														// may be incompatible
														// with 3.2
				new File(commonsCollections3_2, "commons-collections-3.2.jar"));
		mapBuilder.put("commons-collections-3.1.jar", //
				new File(commonsCollections3_2, "commons-collections-3.2.jar"));

		File commonsDbcp = new File(apache, "commons-dbcp");
		File commonsDbcp1_2_2 = new File(commonsDbcp, "1.2.2");
		mapBuilder.put("commons-dbcp-1.2.1.jar", //
				new File(commonsDbcp1_2_2, "commons-dbcp-1.2.2.jar"));

		File commonsLang = new File(apache, "commons-lang");
		File commonsLang2_2 = new File(commonsLang, "2.2");
		mapBuilder.put("commons-lang-1.0.1.jar", // Is this incompatible with
													// 2.x?
				new File(commonsLang2_2, "commons-lang-2.2.jar"));
		mapBuilder.put("commons-lang-2.0.jar", //
				new File(commonsLang2_2, "commons-lang-2.2.jar"));
		mapBuilder.put("commons-lang-2.1.jar", //
				new File(commonsLang2_2, "commons-lang-2.2.jar"));

		File commonsPool = new File(apache, "commons-pool");
		File commonsPool1_3 = new File(commonsPool, "1.3");
		mapBuilder.put("commons-pool-1.2.jar", //
				new File(commonsPool1_3, "commons-pool-1.3.jar"));

		File commonsHttpclient = new File(apache, "jarkarta-commons-httpclient");
		File commonsHttpclient3_0_1 = new File(commonsHttpclient, "3.0.1");
		mapBuilder.put("commons-httpclient-3.0.jar", //
				new File(commonsHttpclient3_0_1, "commons-httpclient-3.0.1.jar"));

		// Apache jcs is not present in 1.4+

		// Apache torque is not present in 1.4+

		File c3p0Org = new File(repository, "c3p0");
		File c3p0Module = new File(c3p0Org, "c3p0");
		File c3p0_0_9_1 = new File(c3p0Module, "0.9.1");
		mapBuilder.put("c3p0-0.8.5.2.jar", //
				new File(c3p0_0_9_1, "c3p0-0.9.1.jar"));
		mapBuilder.put("c3p0-0.9.0.jar", //
				new File(c3p0_0_9_1, "c3p0-0.9.1.jar"));

		File cacore = new File(repository, "cacore");
		File clm = new File(cacore, "clm");
		File clm4_1 = new File(clm, "4.1");
		mapBuilder.put("clm.jar", //
				new File(clm4_1, "clm-4.1.jar"));

		// CSM 3.2 is not supported for 1.3

		// SDK 3.2.1 is not supported

		File castorOrg = new File(repository, "castor");
		File castorModule = new File(castorOrg, "clm");
		File castor1_0_2 = new File(castorModule, "1.0.2");
		mapBuilder.put("castor-0.9.9.jar", //
				new File(castor1_0_2, "castor-1.0.2.jar"));

		File ehcacheOrg = new File(repository, "ehcache");
		File ehcacheModule = new File(ehcacheOrg, "ehcache");
		File ehcache1_6_2 = new File(ehcacheModule, "1.6.2");
		mapBuilder.put("ehcache-0.9.jar", //
				new File(ehcache1_6_2, "ehcache-1.6.2.jar"));
		mapBuilder.put("ehcache-1.1.jar", //
				new File(ehcache1_6_2, "ehcache-1.6.2.jar"));
		mapBuilder.put("ehcache-1.2.jar", //
				new File(ehcache1_6_2, "ehcache-1.6.2.jar"));
		mapBuilder.put("ehcache-1.2.3.jar", //
				new File(ehcache1_6_2, "ehcache-1.6.2.jar"));
		mapBuilder.put("ehcache-1.2.4.jar", //
				new File(ehcache1_6_2, "ehcache-1.6.2.jar"));

		mapBuilder.put("cog-jglobus.jar", OBSOLETE);

		File hibernateOrg = new File(repository, "hibernate");
		File hibernateModule = new File(hibernateOrg, "hibernate");
		File hibernate3_2_0ga = new File(hibernateModule, "3.2.0.ga");
		mapBuilder.put("hibernate3.jar", //
				new File(hibernate3_2_0ga, "hibernate3.jar"));

		File internet2 = new File(repository, "internet2");
		File grouper = new File(internet2, "grouper");
		File grouper1_1_0_1 = new File(grouper, "1.1.0.1");
		mapBuilder.put("grouper.jar", //
				new File(grouper1_1_0_1, "grouper.jar"));

		File jasig = new File(repository, "jasig");

		File casClientCore = new File(jasig, "cas-client-core");
		File casClientCore3_1_3 = new File(casClientCore, "3.1.3");
		mapBuilder.put("cas-client-core-3.0.jar", //
				new File(casClientCore3_1_3, "cas-client-core-3.1.3.jar"));

		File casServer = new File(jasig, "cas-server");
		File casServer3_2_2 = new File(casServer, "3.2.2");
		mapBuilder.put("cas-server-3.0.5.jar", //
				new File(casServer3_2_2, "cas-server-core-3.2.2.jar"));
		mapBuilder.put("cas-server-3.1.jar", //
				new File(casServer3_2_2, "cas-server-core-3.2.2.jar"));

		// Unsure if swapping the older person-directory-1.0.1.jar out for the
		// newer person-directory-1.1.2.jar would necessitate also including
		// person-directory-api-1.1.2.jar. Substituting 2 files for one is a
		// bigger code change than I want to make without first knowing if it is
		// needed.

		File junitOrg = new File(repository, "junit");
		File junitModule = new File(junitOrg, "junit");
		File junit4_8_2 = new File(junitModule, "4.8.2");
		mapBuilder.put("junit-4.4.jar", //
				new File(junit4_8_2, "junit-4.8.2.jar"));

		// Unsure if swapping the older asm-1.3.4.jar out for the
		// newer version 1.5.3 asm.jar would necessitate also dropping
		// asm-util-1.3.4.jar.

		File springframework = new File(repository, "springframework");
		File binding = new File(springframework, "binding");
		File springBinding1_0_5 = new File(binding, "1.0.5");
		mapBuilder.put("spring-binding-1.0.3.jar", //
				new File(springBinding1_0_5, "spring-binding-1.0.5.jar"));

		// Spring-ldap.*.jar has been dropped for 1.4+

		// 1.3 uses a variety of spring 2 jars. 1.4+ uses spring 3.0.
		// I am not sure how compatible these are.

		File webflow = new File(springframework, "webflow");
		File springWebflow1_0_5 = new File(webflow, "1.0.5");
		mapBuilder.put("spring-webflow-1.0.3.jar", //
				new File(springWebflow1_0_5, "spring-webflow-1.0.5.jar"));

		File sun = new File(repository, "sun");
		File persistenceApi = new File(sun, "persistence-api");
		File persistenceApi1_0_1_GA = new File(persistenceApi, "1.0.1.GA");
		mapBuilder.put("persistence-api-1.0.jar", //
				new File(persistenceApi1_0_1_GA, "ejb3-persistence.jar"));

		supersededJarFileMap = mapBuilder.build();
	}

	private File newServiceDir;
	private File newLibDir;
	private File oldLibDir;

	/**
	 * Constructor
	 * 
	 * @param oldServiceDir
	 * @param newTomcatDir
	 */
	public Cagrid1_3TomcatToNewCagridTomcat(File oldServiceDir, File newTomcatDir) {
		super();
		File oldEtcDir = oldServiceDir.getParentFile();
		File oldWebinfDir = oldEtcDir.getParentFile();
		oldLibDir = new File(oldWebinfDir, "lib");

		String serviceDirName = oldServiceDir.getName();
		File newWebappsDir = new File(newTomcatDir, "webapps");
		File newWsrfDir = new File(newWebappsDir, "wsrf");
		File newWebinfDir = new File(newWsrfDir, "WEB-INF");
		newLibDir = new File(newWebinfDir, "lib");
		File etcDir = new File(newWebinfDir, "etc");
		newServiceDir = new File(etcDir, serviceDirName);
	}

	/**
	 * @param args
	 *            A three element array containing the path of the caGrid
	 *            installation directory, path of the service directory in the
	 *            old tomcat container and the path of the new tomcat container.
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 3) {
				String msg = "The program requires three command-line arguments:\n" + "0: the path of the caGrid installation directory\n"
						+ "1: the path of the old tomcat's service directory\n" + "2: the path of the new tomcat container";
				System.err.println(msg);
				System.exit(1);
			}
			init(args[0]);
			File oldServiceDir = new File(args[1]);
			ensureIsDirectory(oldServiceDir);
			File newTomcatDir = new File(args[2]);
			ensureIsDirectory(newTomcatDir);
			Cagrid1_3TomcatToNewCagridTomcat instance = new Cagrid1_3TomcatToNewCagridTomcat(oldServiceDir, newTomcatDir);
			instance.copyIntroduceJarFile();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(99);
		}
	}

	/**
	 * Make a modified copy of introduceDeployment.xml. For each .jar file
	 * listed, a determination is made if it identified .jar file has been
	 * superseded in the new version of caGrid.
	 * <p>
	 * If the named jar file has been superseded, then the superseding file is
	 * copied from the CaGrid repository to the ../lib directory and then name
	 * of the superseding jar file replaces the name of the original in the
	 * introduceDeployment.xml file.
	 * <p>
	 * If the named jar file has NOT been superseded, then the named file is
	 * copied from the old container's lib directory to the new container's lib
	 * directory. The original name is kept in the introduceDeployment.xml file.
	 * 
	 * @throws Exception
	 *             if there is a problem
	 */
	private void copyIntroduceJarFile() throws Exception {
		System.out.println("Begining to process .jar files.");
		File introduceDeployment = new File(newServiceDir, "introduceDeployment.xml");
		ensureIsFile(introduceDeployment);
		makeBackupCopy(introduceDeployment, ".original");
		File newIntroduceDeployment = new File(newServiceDir, "introduceDeployment.xml.new");

		try {
			copyIntroduceJarStream(introduceDeployment, newIntroduceDeployment);
		} catch (Exception e) {
			try {
				newIntroduceDeployment.delete();
			} catch (Exception ee) {
				System.err.println("Error deleting output file while responding to another exception:");
				ee.printStackTrace();
			}
			throw e;
		}
		Files.move(newIntroduceDeployment, introduceDeployment);
		System.out.println(".jar file processing done.");
	}

	/**
	 * Copy of introduceDeployment.xml from the input file to the output file
	 * with some possible modifications. For each .jar file listed, a
	 * determination is made if the identified .jar file has been superseded in
	 * the new version of caGrid.
	 * <p>
	 * If a jar file has been superseded, then the superseding file is copied
	 * from the CaGrid repository to the ../lib directory and then name of the
	 * superseding jar file replaces the name of the original in the
	 * introduceDeployment.xml file.
	 * <p>
	 * If a jar file is obsolete, then it is not copied and its name is omitted
	 * from the copied introduceDeployment file.
	 * <p>
	 * If the named jar file has NOT been superseded, then the named file is
	 * copied from the old container's lib directory to the new container's lib
	 * directory. The original name is kept in the introduceDeployment.xml file.
	 * 
	 * @param in
	 *            The file to read from.
	 * @param out
	 *            The file to write to.
	 * @throws Exception
	 *             If there is a problem
	 */
	private void copyIntroduceJarStream(File in, File out) throws IOException, Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(in);
		try {
			Element deploymentElement = findDeploymentElement(document);
			NodeList jarsList = deploymentElement.getElementsByTagName("Jars");
			int jarsListLength = jarsList.getLength();
			System.out.println("Found " + jarsListLength + " lists of .jar files to process.");
			for (int jarsIndex = 0; jarsIndex < jarsListLength; jarsIndex++) {
				Element jarsElement = (Element) jarsList.item(jarsIndex);
				NodeList jarList = jarsElement.getElementsByTagName("Jar");
				int jarListLength = jarList.getLength();
				System.out.println("Found list of " + jarListLength + " .jar files to process.");
				for (int jarListIndex = 0; jarListIndex < jarListLength; jarListIndex++) {
					Element jarElement = (Element) jarList.item(jarListIndex);
					Attr nameAttr = findNameAttribute(jarElement);
					String oldName = nameAttr.getValue();
					String newName = processJarFile(oldName);
					if (newName == null) { // if obsolete
						jarsElement.removeChild(jarElement);
					} else {
						nameAttr.setValue(newName);
					}
				}
			}
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String msg;
			try {
				writeXML(baos, document);
				msg = "An error occurred while trying to process this XML from " + in.getAbsolutePath() + ":\n" + baos.toString();
			} catch (Exception ee) {
				ee.printStackTrace();
				msg = "An error occurred while trying to process XML from " + in.getAbsolutePath()
						+ " A separate error has prevented the in-memory version of the XML from being serialized into this message.";
			}
			throw new Exception(msg, e);
		}
		writeXML(out, document);
	}

	private void writeXML(File out, Document document) throws FileNotFoundException, IOException {
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(out));
		writeXML(outputStream, document);
	}

	private void writeXML(OutputStream outputStream, Document document) throws FileNotFoundException, IOException {
		OutputFormat format = new OutputFormat("XML", "ISO-8859-1", true);
		format.setIndent(2);
		format.setIndenting(true);
		format.setLineWidth(140);
		XMLSerializer serializer = new XMLSerializer(outputStream, format);
		serializer.asDOMSerializer();
		serializer.serialize(document);
	}

	private Attr findNameAttribute(Element jarElement) throws XMLParseException {
		Attr nameAttr = jarElement.getAttributeNode("name");
		if (nameAttr == null) {
			throw new XMLParseException("Jar element has not name attribute.");
		}
		return nameAttr;
	}

	private Element findDeploymentElement(Document document) throws XMLParseException {
		NodeList rootNodeList = document.getElementsByTagName("ns1:Deployment");
		if (rootNodeList.getLength() != 1) {
			throw new XMLParseException("Document contains " + rootNodeList.getLength() + " \"Deployment\" elements, but should contain exactly one.");
		}
		Node rootNode = rootNodeList.item(0);
		if (!(rootNode instanceof Element)) {
			throw new XMLParseException("Root of document is not an element");
		}
		Element deploymentElement = (Element) rootNode;
		String tagName = deploymentElement.getTagName();
		if (tagName == null) {
			throw new XMLParseException("Tag name of root element is null.");
		}
		if (!(tagName.equals("ns1:Deployment"))) {
			throw new XMLParseException("Expected tag of root element to be \"Deployment\" but it is " + deploymentElement.getTagName());
		}
		return deploymentElement;
	}

	/**
	 * Determine if the named jar file is superseded in the new version of
	 * caGrid.
	 * <p>
	 * If the named jar file has not been superseded, then copy it from
	 * oldLibDir to newLibDir and return its name.
	 * <p>
	 * If the named jar file has been superseded, then copy the superseding jar
	 * file from the caGrid repository to newLibDir and return the name of the
	 * superseding jar file.
	 * 
	 * @param oldJarFileName
	 *            The name of the jar file in question.
	 * @return null of the original jar is obsolete; the original jar file name
	 *         if not superseded; otherwise the name of the superseding jar
	 *         file.
	 * @throws IOException
	 *             If there is a problem
	 */
	private String processJarFile(String oldJarFileName) throws IOException {
		File supersedingFile = supersededJarFileMap.get(oldJarFileName);
		if (supersedingFile == null) {
			File jarFile = new File(oldLibDir, oldJarFileName);
			copyFileToDirectory(jarFile, newLibDir);
			System.out.println("Keeping " + oldJarFileName + " from old container.");System.out.flush();
			return oldJarFileName;
		} else if (supersedingFile == OBSOLETE) {
			System.out.println(oldJarFileName + " is obsolete and will not be copied.");System.out.flush();
			return null;
		} else {
			copyFileToDirectory(supersedingFile, newLibDir);
			System.out.println("Replacing " + oldJarFileName + " with " + supersedingFile.getAbsolutePath());System.out.flush();
			return supersedingFile.getName();
		}
	}

	/**
	 * copy the specified file to the specified directory.
	 * 
	 * @param sourceFile
	 *            The file to copy
	 * @param directory
	 *            The directory in which the like-named copy of the file should
	 *            be created.
	 * @throws IOException
	 *             If there is a problem.
	 */
	private void copyFileToDirectory(File sourceFile, File directory) throws IOException {
		String name = sourceFile.getName();
		File destinationFile = new File(directory, name);
		Files.copy(sourceFile, destinationFile);
	}

	private void ensureIsFile(File f) {
		if (!f.isFile()) {
			System.err.println(f.getAbsolutePath() + " does not exist or is not a regular file.");
			Thread.dumpStack();
			System.exit(3);
		}
	}

	/**
	 * Make a backup copy of the given file. The name of the backup copy will be
	 * the name of the original followed by the given suffix.
	 * 
	 * @param f
	 *            The file to be copied
	 * @param suffix
	 *            the suffix to be appended to the file name.
	 * @throws IOException
	 *             if there is a problem.
	 */
	private void makeBackupCopy(File f, String suffix) throws IOException {
		File backup = new File(f.getParent(), f.getName() + suffix);
		Files.copy(f, backup);
	}

	private static void ensureIsDirectory(File dir) {
		if (!dir.isDirectory()) {
			String msg = dir.getAbsolutePath() + " does not exist or is not a directory";
			System.err.println(msg);
			Thread.dumpStack();
			System.exit(2);
		}
	}

}
