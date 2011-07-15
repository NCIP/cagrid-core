package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.provider.ProviderTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.RunToolsTemplate;
import gov.nih.nci.cagrid.introduce.templates.client.ClientConfigTemplate;
import gov.nih.nci.cagrid.introduce.templates.client.ServiceClientBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.client.ServiceClientTemplate;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceConstantsBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceConstantsTemplate;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Upgrade a service created by introduce 1.2 to introduce 1.5 standards.
 */
public class Introduce_1_2__1_5_Upgrader extends IntroduceUpgraderBase {

	public Introduce_1_2__1_5_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation, String servicePath) throws Exception {
		super(status, serviceInformation, servicePath, "1.2", "1.5");
	}

	protected void upgrade() throws Exception {
		replaceOldBuildFilesWithNew();
		
		RunToolsTemplate runToolsT = new RunToolsTemplate();
		String runToolsS = runToolsT.generate(new SpecificServiceInformation(getServiceInformation(), getServiceInformation().getServices().getService(0)));
		File runToolsF = new File(getServicePath() + File.separator + "run-tools.xml");
		FileWriter runToolsFW = new FileWriter(runToolsF);
		runToolsFW.write(runToolsS);
		runToolsFW.close();
		getStatus().addDescriptionLine("replaced run-tools.xml, build.xml, and build-deploy.xml with new version");

		upgradeJars();
		fixDevBuildDeploy();
		fixSource();
		fixWSDD();
		fixSecurityOnMetadataAccessProviders();
		
        OrganizeImports oi = new OrganizeImports(new File(getServicePath()));
        oi.runStep();

		getStatus().setStatus(StatusBase.UPGRADE_OK);
	}

	protected void fixDevBuildDeploy() throws Exception {
		// if this service was upgraded from 1.1 to 1.2 the dev build deploy
		// will have a bug
		// preventing the undeployTomcat target to work

		StringBuffer devsb = Utils.fileToStringBuffer(new File(getServicePath() + File.separator + "dev-build-deploy.xml"));
		String newFileString = devsb.toString();
		newFileString = newFileString.replace("postUndeployyTomcat", "postUndeployTomcat");
		FileWriter fw = new FileWriter(new File(getServicePath() + File.separator + "dev-build-deploy.xml"));
		fw.write(newFileString);
		fw.close();

		getStatus().addDescriptionLine("fixed typo error created during upgrade from 1.1 to 1.2 with target undeployTomcat");

	}

	protected void fixSecurityOnMetadataAccessProviders() {
		for (int i = 0; i < getServiceInformation().getServices().getService().length; i++) {
			ServiceType service = getServiceInformation().getServices().getService(i);
			if (service.getResourceFrameworkOptions().getResourcePropertyManagement() != null) {
				ProviderTools.removeResourcePropertiesManagementResourceFrameworkOption(service, getServiceInformation());
				ProviderTools.addResourcePropertiesManagementResourceFrameworkOption(service, getServiceInformation());
			}
		}
	}

	protected void fixSource() throws Exception {
		File srcDir = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator + "src");
		for (int serviceI = 0; serviceI < getServiceInformation().getServices().getService().length; serviceI++) {
			ServiceType service = getServiceInformation().getServices().getService(serviceI);

			ServiceClientTemplate clientT = new ServiceClientTemplate();
			String clientS = clientT.generate(new SpecificServiceInformation(getServiceInformation(), service));
			File clientF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator + "client" + File.separator
					+ service.getName() + "Client.java");

			FileWriter clientFW = new FileWriter(clientF);
			clientFW.write(clientS);
			clientFW.close();

			ServiceClientBaseTemplate clientBaseT = new ServiceClientBaseTemplate();
			String clientBaseS = clientBaseT.generate(new SpecificServiceInformation(getServiceInformation(), service));
			File clientBaseF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator + "client"
					+ File.separator + service.getName() + "ClientBase.java");

			FileWriter clientBaseFW = new FileWriter(clientBaseF);
			clientBaseFW.write(clientBaseS);
			clientBaseFW.close();

			ClientConfigTemplate clientConfigT = new ClientConfigTemplate();
			String clientConfigS = clientConfigT.generate(new SpecificServiceInformation(getServiceInformation(), service));
			File clientConfigF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator + "client"
					+ File.separator + "client-config.wsdd");
			FileWriter clientConfigFW = new FileWriter(clientConfigF);
			clientConfigFW.write(clientConfigS);
			clientConfigFW.close();

			// add new constants base class and new constants class
			ServiceConstantsTemplate resourceContanstsT = new ServiceConstantsTemplate();
			String resourceContanstsS = resourceContanstsT.generate(new SpecificServiceInformation(getServiceInformation(), service));
			File resourceContanstsF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator + "common"
					+ File.separator + File.separator + service.getName() + "Constants.java");

			FileWriter resourceContanstsFW = new FileWriter(resourceContanstsF);
			resourceContanstsFW.write(resourceContanstsS);
			resourceContanstsFW.close();

			ServiceConstantsBaseTemplate resourcebContanstsT = new ServiceConstantsBaseTemplate();
			String resourcebContanstsS = resourcebContanstsT.generate(new SpecificServiceInformation(getServiceInformation(), service));
			File resourcebContanstsF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator + "common"
					+ File.separator + service.getName() + "ConstantsBase.java");

			FileWriter resourcebContanstsFW = new FileWriter(resourcebContanstsF);
			resourcebContanstsFW.write(resourcebContanstsS);
			resourcebContanstsFW.close();
		}

		getStatus().addDescriptionLine("Updated many source files to be better editable and extendable");
	}

	protected void fixWSDD() throws Exception {
		Document doc = XMLUtilities.fileNameToDocument(getServiceInformation().getBaseDirectory() + File.separator + "server-config.wsdd");
		List<Element> servicesEls = getServiceChildren(doc);
		for (int serviceI = 0; serviceI < servicesEls.size(); serviceI++) {
			Element serviceEl = (Element) servicesEls.get(serviceI);
			ServiceType service = CommonTools.getService(getServiceInformation().getServices(),
					serviceEl.getAttributeValue("name").substring(serviceEl.getAttributeValue("name").lastIndexOf("/") + 1));

			// need to add the service name att and the etc path att for each
			// service
			Element serviceName = new Element("parameter", Namespace.getNamespace("http://xml.apache.org/axis/wsdd/"));
			serviceName.setAttribute("name", service.getName().toLowerCase() + "-serviceName");
			serviceName.setAttribute("value", service.getName());

			Element serviceETC = new Element("parameter", Namespace.getNamespace("http://xml.apache.org/axis/wsdd/"));
			serviceETC.setAttribute("name", service.getName().toLowerCase() + "-etcDirectoryPath");
			serviceETC.setAttribute("value", "ETC-PATH");

			serviceEl.addContent(serviceETC);
			serviceEl.addContent(serviceName);

		}

		FileWriter fw = new FileWriter(getServiceInformation().getBaseDirectory() + File.separator + "server-config.wsdd");
		fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(doc)));
		fw.close();

		getStatus().addDescriptionLine("Regenerated service-config.wsdd");
	}

	/**
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Element> getServiceChildren(Document doc) {
		List<Element> c = doc.getRootElement().getChildren("service", Namespace.getNamespace("http://xml.apache.org/axis/wsdd/"));
		return c;
	}

	private void upgradeJars() throws Exception {
		removeOldCagridGlobusAndMobiusJars();
		copySkeletonJarsToLib();

		// remove the old introduce tools jar
		File serviceToolsLibDir = new File(new File(getServicePath(), "tools"), "lib");
		File skeletonToolsLibDir = new File(new File(SKELETON_DIR, "tools"), "lib");
		File serviceTasksJar = new File(serviceToolsLibDir, "caGrid-Introduce-serviceTasks-1.2.jar");
		serviceTasksJar.delete();

		// copy new libraries into tools (every thing in skeleton/tool/lib)
		File[] skeletonToolsLibs = skeletonToolsLibDir.listFiles(JAR_FILTER);
		for (int i = 0; i < skeletonToolsLibs.length; i++) {
			File out = new File(serviceToolsLibDir.getAbsolutePath() + File.separator + skeletonToolsLibs[i].getName());
			try {
				Utils.copyFile(skeletonToolsLibs[i], out);
				getStatus().addDescriptionLine(skeletonToolsLibs[i].getName() + " added");
			} catch (IOException ex) {
				throw new Exception("Error copying library (" + skeletonToolsLibs[i] + ") to service: " + ex.getMessage(), ex);
			}
		}

	}

}
