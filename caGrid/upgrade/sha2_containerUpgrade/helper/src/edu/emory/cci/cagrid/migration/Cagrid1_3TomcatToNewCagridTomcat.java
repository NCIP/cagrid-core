package edu.emory.cci.cagrid.migration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;
import com.google.common.io.Files;

/**
 * Copy jars and the service directory for a deployed CaGrid service from the
 * old tomcat container to a new tomcat container, substituting newer caGrid
 * jars as seems appropriate. Also update the service's introduceDeployment.xml
 * file to reflect changes in file names.
 * 
 * @author Mark Grand
 */
public class Cagrid1_3TomcatToNewCagridTomcat {
	private static final String NEW_CAGRID_VERSION = "1.4.1";

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
		mapBuilder.put("caGrid-gridgrouper-client-1.3.jar", //
				new File(gtsLib, "caGrid-gridgrouper-client-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-common-1.3.jar", //
				new File(gtsLib, "caGrid-gridgrouper-common-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-service-1.3.jar", //
				new File(gtsLib, "caGrid-gridgrouper-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-service-1.3.jar", //
				new File(gtsLib, "caGrid-gridgrouper-service-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-stubs-1.3.jar", //
				new File(gtsLib, "caGrid-gridgrouper-stubs-" + NEW_CAGRID_VERSION + ".jar"));
		mapBuilder.put("caGrid-gridgrouper-tests-1.3.jar", //
				new File(gtsLib, "caGrid-gridgrouper-tests-" + NEW_CAGRID_VERSION + ".jar"));

		File repository = new File(cagridHome, "repository");
		// TODO
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
		File etcDir = new File(newWsrfDir, "etc");
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
		File introduceDeployment = new File(newServiceDir, "introduceDeployment.xml");
		ensureIsFile(introduceDeployment);
		makeBackupCopy(introduceDeployment, ".original");
		File newIntroduceDeployment = new File(newServiceDir, "introduceDeployment.xml.new");

		InputStream in = null;
		DataOutputStream out = null;
		try {
			in = new FileInputStream(introduceDeployment);
			in = new BufferedInputStream(in);
			PushbackInputStream pin = new PushbackInputStream(in);
			FileOutputStream fout = new FileOutputStream(newIntroduceDeployment);
			BufferedOutputStream bout = new BufferedOutputStream(fout);
			out = new DataOutputStream(bout);
			copyIntroduceJarStream(pin, out);
		} catch (Exception e) {
			try {
				newIntroduceDeployment.delete();
			} catch (Exception ee) {
				System.err.println("Error deleting output file while responding to another exception:");
				ee.printStackTrace();
			}
			throw e;
		} finally {
			if (in != null) {
				Closeables.closeQuietly(in);
			}
			if (out != null) {
				Closeables.closeQuietly(out);
			}
		}
		Files.move(newIntroduceDeployment, introduceDeployment);
	}

	/**
	 * Copy of introduceDeployment.xml from the input stream to the output
	 * stream with some possible modifications. For each .jar file listed, a
	 * determination is made if it identified .jar file has been superseded in
	 * the new version of caGrid.
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
	 * @param pin
	 *            The input stream to read from.
	 * @param out
	 *            The output stream to write to.
	 * @throws IOException
	 *             If there is a problem
	 */
	private void copyIntroduceJarStream(PushbackInputStream pin, DataOutputStream out) throws IOException {
		while (readPast(pin, out, "name=\"")) {
			String oldJarFileName = readUpto(pin, '"');
			String newJarFileName = processJarFile(oldJarFileName);
			out.writeBytes(newJarFileName);
		}
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
	 * @return the original jar file name if not superseded; otherwise the name
	 *         of the superseding jar file.
	 * @throws IOException
	 *             If there is a problem
	 */
	private String processJarFile(String oldJarFileName) throws IOException {
		File supersedingFile = supersededJarFileMap.get(oldJarFileName);
		if (supersedingFile == null) {
			File jarFile = new File(oldLibDir, oldJarFileName);
			copyFileToDirectory(jarFile, newLibDir);
			return oldJarFileName;
		} else {
			copyFileToDirectory(supersedingFile, newLibDir);
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

	/**
	 * Read characters from the input stream up to, but not including the next
	 * instance of the given character.
	 * 
	 * @param pin
	 *            the input stream to read from.
	 * @param target
	 *            the character to read up to.
	 * @return the read characters as a string.
	 * @throws IOException
	 *             if there is a problem.
	 */
	private String readUpto(PushbackInputStream pin, char target) throws IOException {
		StringBuffer buffer = new StringBuffer();
		while (true) {
			int c = pin.read();
			if (c == -1) {
				throw new IOException("Encountered end-of-file while reading a jar file name.");
			}
			if (c == target) {
				break;
			}
			buffer.append(target);
			pin.unread(target);
		}
		return buffer.toString();
	}

	/**
	 * Read character from then input stream and write the characters to the
	 * output stream until the most recently read characters match the target
	 * string.
	 * 
	 * @param in
	 *            The character stream to read from.
	 * @param out
	 *            The character stream to write to.
	 * @param target
	 *            the string to look for in the stream.
	 * @return true if this method returned because the most recently read
	 *         characters matched the target string. If this method stops
	 *         because the inputStream has seen end of file.
	 * @throws IOException
	 *             if there is a problem.
	 */
	private boolean readPast(InputStream in, OutputStream out, String target) throws IOException {
		int length = target.length();
		int[] buffer = new int[length];
		int firstBuffered = 0;
		int firstUnbuffered = 0;

		while (true) {
			int c = in.read();
			if (c == -1) {
				return false;
			}
			out.write(c);
			buffer[firstUnbuffered % length] = c;
			firstUnbuffered += 1;
			if (firstUnbuffered - firstBuffered > length) {
				firstBuffered = firstUnbuffered - length;
			}
			window: do {
				for (int i = 0; i < length; i++) {
					if (buffer[(firstBuffered + i) % length] != target.charAt(i)) {
						break window;
					}
				}
				return true;
			} while (false);
		}
	}

	private void ensureIsFile(File f) {
		if (!f.isFile()) {
			System.err.println(f.getAbsolutePath() + " does not exist or is not a regular file.");
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
			System.exit(2);
		}
	}

}
