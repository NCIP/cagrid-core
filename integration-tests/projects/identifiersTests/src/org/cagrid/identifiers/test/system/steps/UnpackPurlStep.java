package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerPorts;
import gov.nih.nci.cagrid.testing.system.deployment.PortFactory;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cagrid.identifiers.test.system.IdentifiersTestInfo;
import org.jdom.Element;

public class UnpackPurlStep extends Step {
    
	private IdentifiersTestInfo testInfo;
	
	public UnpackPurlStep(IdentifiersTestInfo info) {
		this.testInfo = info;
		
	}

	@Override
	public void runStep() throws Exception {
       
		try {
			ZipUtilities.unzip(new File(IdentifiersTestInfo.PURLZ_ZIP), 
					testInfo.getPurlzDirectory());
			
			writeBootloaderConfig();
			
			ContainerPorts ports = PortFactory.getContainerPorts();
			testInfo.setPurlzPort(ports.getPort());
			
			setServerPort();
			
		} catch (IOException ex) {
			throw new Exception("Error unziping purlz: " + ex.getMessage(), ex);
		}
		
		if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            // make files in /bin directory executable if not on windows platform
		    List<String> command = new ArrayList<String>();
		    command.add("chmod");
		    command.add("a+rwx");
		    command.add("dexter.sh");
		    command.add("start.sh");
		    command.add("netkernel");

			String[] commandArray = command.toArray(new String[command.size()]);
			Process chmodProcess = null;
			try {
				chmodProcess = Runtime.getRuntime().exec(
						commandArray,
						null,
						new File(testInfo.getPurlzDirectory().getAbsolutePath(), "bin"));
				new StreamGobbler(chmodProcess.getInputStream(),
						StreamGobbler.TYPE_OUT,System.out).start();
				new StreamGobbler(chmodProcess.getErrorStream(),
						StreamGobbler.TYPE_OUT,System.err).start();
				chmodProcess.waitFor();
			} catch (Exception ex) {
				throw new Exception("Error invoking chmod process: "
						+ ex.getMessage(), ex);
			}
		}
	}
	
	private void writeBootloaderConfig() throws IOException {
		String bootloaderJar = testInfo.getPurlzDirectory().getAbsolutePath() + 
			File.separator + IdentifiersTestInfo.PURLZ_BOOTLOADER;
		
		File bootloaderCfg = new File(testInfo.getPurlzDirectory().getAbsolutePath() + 
				File.separator + IdentifiersTestInfo.PURLZ_BOOTLOADER_CFG);
		Writer output = new BufferedWriter(new FileWriter(bootloaderCfg));
    	output.write(bootloaderJar);
    	output.close();
	}
	
	private void setServerPort() throws Exception {
        
		File serverConfigFile = new File(
            testInfo.getPurlzDirectory().getAbsolutePath() + 
            IdentifiersTestInfo.PURLZ_TRANSPORT_FILE);
		
		Element configRoot = XMLUtilities.fileNameToDocument(
				serverConfigFile.getAbsolutePath()).getRootElement();

		Iterator configureElementIterator = configRoot.getChildren("Configure",
				configRoot.getNamespace()).iterator();
		while (configureElementIterator.hasNext()) {
			Element configureElement = (Element) configureElementIterator.next();
			Iterator descIterator = configureElement.getDescendants();
			while (descIterator.hasNext()) {
				Object o  = (Object) descIterator.next();
				if (o instanceof Element) {
					Element e = (Element)o;
					if (e.getName().equals("Set") &&
						e.getAttributeValue("name").equals("Port")) {
							e.setText(testInfo.getPurlzPort().toString());
							String xml = XMLUtilities.formatXML(XMLUtilities
								.elementToString(configRoot));
							Utils.stringBufferToFile(new StringBuffer(xml), serverConfigFile
								.getAbsolutePath());
							return;
					}
				}
			}
		}
		throw new Exception("Unable to set PURLZ port");
	}
}
