package gov.nih.nci.cagrid.workflow.test.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import gov.nih.nci.cagrid.workflow.test.system.steps.CheckTavernaWorkflowServiceStep;

public class TavernaWorkflowServiceStory extends ServiceStoryBase {

	private static final String SERVICE_TEMP_PATH = "tmp/TempTWS";
	private static final String RESULTS_TEMP_PATH = "tmp/results";
	private static final String TWS_URL_PATH = "cagrid/TavernaWorkflowService";
	private static final String PATH_TO_TWS_PROJECT = "../../../caGrid/projects/TavernaWorkflowServices";
	public static final String TWS_DIR_PROPERTY = "tws.service.dir";


	public TavernaWorkflowServiceStory(ServiceContainer container) {
		super(container);
	}

	public TavernaWorkflowServiceStory() {

		// init the container
		try {
			this.setContainer(ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to create container: " + ex.getMessage());
		}
	}


	@Override
	public String getName() {
		return getDescription();
	}


	@Override
	public String getDescription() {
		return "Taverna Workflow Service Test";
	}


	protected File getTWSDir() {
		String value = System.getProperty(TWS_DIR_PROPERTY, PATH_TO_TWS_PROJECT);
		assertNotNull("System property " + TWS_DIR_PROPERTY + " was not set!", value);
		File dir = new File(value);
		return dir;
	}


	@Override
	protected void storyTearDown() throws Throwable {

		Thread.sleep(5000);
		StopContainerStep step2 = new StopContainerStep(getContainer());
		try {
			step2.runStep();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		DestroyContainerStep step3 = new DestroyContainerStep(getContainer());
		try {
			step3.runStep();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Vector steps() {
		Vector<Step> steps = new Vector<Step>();
		File tempGMEServiceDir = new File(SERVICE_TEMP_PATH);

		// SETUP
		steps.add(new UnpackContainerStep(getContainer()));
		steps.add(new CopyServiceStep(getTWSDir(), tempGMEServiceDir));



		// CONFIGURE
		steps.add (new ChangeServicePropertyFile());
		steps.add(new BuildTaverna2());


		DeployServiceStep deployStep = new DeployServiceStep(getContainer(), tempGMEServiceDir.getAbsolutePath(),
				Arrays.asList(new String[]{"-Dno.deployment.validation=true"}));
		steps.add(deployStep);
		steps.add(new StartContainerStep(getContainer()));

		EndpointReferenceType epr = null;
		try {
			epr = getContainer().getServiceEPR(TWS_URL_PATH);
		} catch (MalformedURIException e) {
			e.printStackTrace();
			fail("Error constructing client:" + e.getMessage());
		}

		// TEST
		steps.add(new CheckTavernaWorkflowServiceStep(epr));

		// retrieve failures

		return steps;
	}

	private class ChangeServicePropertyFile extends Step {

		public ChangeServicePropertyFile (){

		}

		@Override
		public void runStep() throws Throwable {

			Properties props = new Properties();
			File path = new File (SERVICE_TEMP_PATH);
			String absPath = path.getAbsolutePath();

			try {
				props.load(new FileInputStream(absPath + "/service.properties"));
				System.out.println(" >> Service Properites File : \n >> " + absPath + "/service.properties");
				props.setProperty("tavernaDir", absPath + "/taverna");
				props.setProperty("baseRepositoryDir", absPath + "/taverna/target/repository");
				props.store(new FileOutputStream(absPath + "/service.properties"), null);

				System.out.println(" >> File Changed to:\n");
				System.out.println(" >> " + props.getProperty("tavernaDir"));
				System.out.println(" >> " + props.getProperty("baseRepositoryDir"));

			} catch (FileNotFoundException e1) {
				System.err.println("File " + absPath + "/service.properties NOT found.");
				e1.printStackTrace();
			} catch (IOException e1) {
				System.err.println("Unable to open file : " + absPath + "/service.properties");
				e1.printStackTrace();
			} 

		}

	}


	private class BuildTaverna2 extends Step
	{
		public BuildTaverna2(){
		}
		@Override
		public void runStep() throws Throwable {
			String output="";
			try {
				String antHome = System.getenv("ANT_HOME");
				if (antHome == null || antHome.equals("")) {
					throw new ContainerException("ANT_HOME not set");
				}
				File ant = new File(antHome, "bin" + File.separator + "ant");

				List<String> command = new ArrayList<String>();

				// Using ant executable.
				if (System.getProperty("os.name").toLowerCase().contains("win")) {
					command.add("cmd");
					command.add("/c");
					command.add(ant + ".bat");
				} else {
					command.add(ant.toString());
				}

				command.add("taverna-package");
				ProcessBuilder builder = new ProcessBuilder(command);
				builder.redirectErrorStream(true);
				File buildFile = new File(SERVICE_TEMP_PATH);
				builder.directory(buildFile);

				Process process;
				process = builder.start();
				
				System.out.println(" >> Downloading Taverna jars from repository, please wait..");
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				String line;
				while ((line = br.readLine()) != null) {
					//System.out.println(line);
					output = output + line;
				}
				process.waitFor();

			} catch (IOException e) {
				System.err.println("\nErorr in running Ant Task");
				System.out.println(output);
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.err.println("Process Interrupted");
				e.printStackTrace();
			}



		}
	}

}
