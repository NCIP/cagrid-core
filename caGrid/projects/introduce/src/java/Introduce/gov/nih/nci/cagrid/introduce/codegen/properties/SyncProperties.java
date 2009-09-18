package gov.nih.nci.cagrid.introduce.codegen.properties;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.JNDIConfigServicePropertiesTemplate;
import gov.nih.nci.cagrid.introduce.templates.JNDIConfigServiceResourcePropertiesTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;


/**
 * Keep the JNDI file in sync.....
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncProperties extends SyncTool {

	public SyncProperties(File baseDirectory, ServiceInformation info) {
		super(baseDirectory, info);
	}


	public void sync() throws SynchronizationException {

		Properties serviceProps = new Properties();
		// load up the service properties
		if (getServiceInformation().getServiceProperties() != null
			&& getServiceInformation().getServiceProperties().getProperty() != null) {
			for (int i = 0; i < getServiceInformation().getServiceProperties().getProperty().length; i++) {
				ServicePropertiesProperty prop = getServiceInformation().getServiceProperties().getProperty(i);
				if (prop.getValue() == null) {
					serviceProps.put(prop.getKey(), "");
				} else {
					serviceProps.put(prop.getKey(), prop.getValue());
				}
			}
		}

		// write the service propertis out
		try {
			serviceProps.store(new FileOutputStream(new File(getBaseDirectory().getAbsolutePath() + File.separator
				+ IntroduceConstants.INTRODUCE_SERVICE_PROPERTIES)), "service deployment properties");
		} catch (Exception ex) {
			throw new SynchronizationException(ex.getMessage(), ex);
		}

		// update the JNDI file to have all the right properties and thier
		// values....
		File jndiConfigF = new File(getBaseDirectory().getAbsolutePath() + File.separator + "jndi-config.xml");
		try {
			Document doc = XMLUtilities.fileNameToDocument(jndiConfigF.getAbsolutePath());
			List serviceEls = doc.getRootElement().getChildren("service", doc.getRootElement().getNamespace());
			for (int serviceI = 0; serviceI < serviceEls.size(); serviceI++) {
				Element serviceEl = (Element) serviceEls.get(serviceI);

				String serviceName = serviceEl.getAttributeValue("name");
				int startOfServiceName = serviceName.lastIndexOf("/");
				serviceName = serviceName.substring(startOfServiceName + 1);

				ServiceType service = CommonTools.getService(getServiceInformation().getServices(), serviceName);

				if (service == null) {
					service = CommonTools.getService(getServiceInformation().getServices(), serviceName + "Service");
					if (service == null) {
						throw new SynchronizationException(
							"Could not find service in the service information in SyncProperties: " + serviceName);
					}
				}

				List resourceEls = serviceEl.getChildren("resource", serviceEl.getNamespace());
				for (int resourceI = 0; resourceI < resourceEls.size(); resourceI++) {
					Element resourceEl = (Element) resourceEls.get(resourceI);
					if (serviceI == serviceEls.size() - 1
						&& resourceEl.getAttributeValue("name").equals("serviceconfiguration")) {
						// located a serviceconfiguration element, need to
						// populate it's attributes now...

						JNDIConfigServicePropertiesTemplate serviceConfTemp = new JNDIConfigServicePropertiesTemplate();
						String confXMLString = serviceConfTemp.generate(new SpecificServiceInformation(
							getServiceInformation(), service));
						Element newResourceEl = XMLUtilities.stringToDocument(confXMLString).getRootElement();
						serviceEl.removeContent(resourceEl);
						serviceEl.addContent(resourceI, newResourceEl.detach());
					} else if (resourceEl.getAttributeValue("name").equals("configuration")) {
						// located a configuration element, need to
						// populate it's attributes now...

						JNDIConfigServiceResourcePropertiesTemplate serviceResourceConfTemp = new JNDIConfigServiceResourcePropertiesTemplate();
						String confXMLString = serviceResourceConfTemp.generate(new SpecificServiceInformation(
							getServiceInformation(), service));
						Element newResourceEl = XMLUtilities.stringToDocument(confXMLString).getRootElement();
						serviceEl.removeContent(resourceEl);
						serviceEl.addContent(resourceI, newResourceEl.detach());
					}
				}
			}

			try {
				FileWriter fw = new FileWriter(jndiConfigF);
				fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(doc)));
				fw.close();
			} catch (IOException e) {
				throw new SynchronizationException(e.getMessage(), e);
			}

		} catch (Exception e) {
			throw new SynchronizationException(e.getMessage(), e);
		}

	}

}
