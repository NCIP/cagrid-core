package gov.nih.nci.cagrid.introduce.codegen.base;

import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.etc.RegistrationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * SyncMethodsOnDeployment
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncBase extends SyncTool {

    private File etcDir;


    public SyncBase(File baseDirectory, ServiceInformation info) {
        super(baseDirectory, info);
        etcDir = new File(baseDirectory.getAbsolutePath() + File.separator + "etc");
    }


    public void sync() throws SynchronizationException {
        try {
            for (int i = 0; i < getServiceInformation().getServices().getService().length; i++) {
                RegistrationTemplate registrationT = new RegistrationTemplate();
                String registrationS = registrationT.generate(new SpecificServiceInformation(getServiceInformation(),getServiceInformation().getServices().getService(i)));
                File registrationF = new File(etcDir.getAbsolutePath() + File.separator + getServiceInformation().getServices().getService(i).getName() + "_registration.xml");
                FileWriter registrationFW = new FileWriter(registrationF);
                registrationFW.write(registrationS);
                registrationFW.close();
            }

        } catch (IOException e) {
            throw new SynchronizationException("Error writing file:" + e.getMessage(), e);
        }
    }

}
