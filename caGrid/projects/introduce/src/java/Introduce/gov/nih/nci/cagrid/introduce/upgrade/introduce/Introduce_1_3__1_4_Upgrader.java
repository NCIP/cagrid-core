package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.RunToolsTemplate;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;


public class Introduce_1_3__1_4_Upgrader extends IntroduceUpgraderBase {

    public Introduce_1_3__1_4_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation,
        String servicePath) throws Exception {
        super(status, serviceInformation, servicePath, "1.3", "1.4");
    }


    private final class OldJarsFilter implements FileFilter {

        public boolean accept(File name) {
            String filename = name.getName();
            boolean core = filename.startsWith("caGrid-core") && filename.endsWith(".jar");
            boolean advertisement = filename.startsWith("caGrid-advertisement") && filename.endsWith(".jar");
            boolean metadata = filename.startsWith("caGrid-metadata-common") && filename.endsWith(".jar");
            boolean introduce = filename.startsWith("caGrid-Introduce") && filename.endsWith(".jar");
            boolean security = (filename.startsWith("caGrid-ServiceSecurityProvider") || filename
                .startsWith("caGrid-metadata-security"))
                && filename.endsWith(".jar");

            boolean gridGrouper = (filename.startsWith("caGrid-gridgrouper")) && filename.endsWith(".jar");
     
            boolean csm = (filename.startsWith("caGrid-authz-common")) && filename.endsWith(".jar");

            boolean otherSecurityJarsNotNeeded = (filename.startsWith("caGrid-gridca")) && filename.endsWith(".jar");

            boolean wsrf = (filename.startsWith("globus_wsrf_mds") || filename.startsWith("globus_wsrf_servicegroup"))
                && filename.endsWith(".jar");
            boolean mobius = filename.startsWith("mobius") && filename.endsWith(".jar");

            return core || advertisement || metadata || introduce || security || gridGrouper || csm || wsrf || mobius
                || otherSecurityJarsNotNeeded;
        }

    };


    protected void upgrade() throws Exception {

        // need to replace the build.xml
        Utils.copyFile(new File(getServicePath() + File.separator + "build.xml"), new File(getServicePath()
            + File.separator + "build.xml.OLD"));
        Utils.copyFile(new File(getServicePath() + File.separator + "build-deploy.xml"), new File(getServicePath()
            + File.separator + "build-deploy.xml.OLD"));
        Utils.copyFile(new File(getServicePath() + File.separator + "run-tools.xml"), new File(getServicePath()
            + File.separator + "run-tools.xml.OLD"));
        Utils.copyFile(new File("." + File.separator + "skeleton" + File.separator + "build.xml"), new File(
            getServicePath() + File.separator + "build.xml"));
        Utils.copyFile(new File("." + File.separator + "skeleton" + File.separator + "build-deploy.xml"), new File(
            getServicePath() + File.separator + "build-deploy.xml"));
        RunToolsTemplate runToolsT = new RunToolsTemplate();
        String runToolsS = runToolsT.generate(new SpecificServiceInformation(getServiceInformation(),getServiceInformation().getServices().getService(0)));
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

        getStatus().setStatus(StatusBase.UPGRADE_OK);
    }
    
    protected void fixDevBuildDeploy() throws Exception{

    }


    protected void fixSecurityOnMetadataAccessProviders() {

    }


    protected void fixSource() throws Exception {

    }


    protected void fixWSDD() throws Exception {

    }


    private void upgradeJars() throws Exception {

        OldJarsFilter oldDskeletonLibFilter = new OldJarsFilter();

        // locate the old libs in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");

        File[] serviceLibs = serviceLibDir.listFiles(oldDskeletonLibFilter);
        // delete the old libraries
        for (int i = 0; i < serviceLibs.length; i++) {
            boolean deleted = serviceLibs[i].delete();
            if (deleted) {
                getStatus().addDescriptionLine(serviceLibs[i].getName() + " removed");
            } else {
                getStatus().addDescriptionLine(serviceLibs[i].getName() + " could not be removed");
            }
        }

        FileFilter srcSkeletonLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return filename.endsWith(".jar");
            }
        };

        File skeletonLibDir = new File("skeleton" + File.separator + "lib");

        // copy new libraries in (every thing in skeleton/lib)
        File[] skeletonLibs = skeletonLibDir.listFiles(srcSkeletonLibFilter);
        for (int i = 0; i < skeletonLibs.length; i++) {
            File out = new File(serviceLibDir.getAbsolutePath() + File.separator + skeletonLibs[i].getName());
            try {
                Utils.copyFile(skeletonLibs[i], out);
                getStatus().addDescriptionLine(skeletonLibs[i].getName() + " added");
            } catch (IOException ex) {
                throw new Exception("Error copying library (" + skeletonLibs[i] + ") to service: " + ex.getMessage(),
                    ex);
            }
        }

        // remove the old introduce tools jar from 1.3
        File serviceToolsLibDir = new File(getServicePath() + File.separator + "tools" + File.separator + "lib");
        File skeletonToolsLibDir = new File("skeleton" + File.separator + "tools" + File.separator + "lib");
        File serviceTasksJar = new File(serviceToolsLibDir.getAbsolutePath() + File.separator
            + "caGrid-Introduce-buildTools-1.3.jar");
        serviceTasksJar.delete();
        
        // remove the old core jar from 1.3
        File coreJar = new File(serviceToolsLibDir.getAbsolutePath() + File.separator
            + "caGrid-core-1.3.jar");
        coreJar.delete();

        FileFilter srcSkeletonToolsLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return filename.endsWith(".jar");
            }
        };
        // copy new libraries into tools (every thing in skeleton/tool/lib)

        File[] skeletonToolsLibs = skeletonToolsLibDir.listFiles(srcSkeletonToolsLibFilter);
        for (int i = 0; i < skeletonToolsLibs.length; i++) {
            File out = new File(serviceToolsLibDir.getAbsolutePath() + File.separator + skeletonToolsLibs[i].getName());
            try {
                Utils.copyFile(skeletonToolsLibs[i], out);
                getStatus().addDescriptionLine(skeletonToolsLibs[i].getName() + " added");
            } catch (IOException ex) {
                throw new Exception("Error copying library (" + skeletonToolsLibs[i] + ") to service: "
                    + ex.getMessage(), ex);
            }
        }

    }

}
