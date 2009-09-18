/*
 * Created on Oct 10, 2006
 */
package gov.nci.nih.cagrid.tests.core.util;

import gov.nci.nih.cagrid.tests.core.steps.ServiceCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.ServiceInvokeStep;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;


public class ServiceHelper {
    protected String serviceName;
    protected File testDir;
    protected File introduceDir;
    protected File tempDir;
    protected File serviceDir;
    protected IntroduceServiceInfo serviceInfo;
    protected GlobusHelper globus;
    protected EndpointReferenceType endpoint;
    protected ServiceCreateStep createServiceStep;
    protected File metadataFile;
    protected File cadsrServiceDir;


    public ServiceHelper(String serviceName) {
        this(serviceName, null);
    }


    public ServiceHelper(String serviceName, File serviceDir) {
        super();

        // service name
        this.serviceName = serviceName;
        if (serviceDir != null) {
            this.serviceDir = serviceDir;
        }

        // test dir (home of the service test)
        this.testDir = new File("test" + File.separator + "resources" + File.separator + "services" + File.separator
            + serviceName);
        // introduce dir
        this.introduceDir = new File(System.getProperty("introduce.dir", ".." + File.separator + ".." + File.separator
            + ".." + File.separator + "caGrid" + File.separator + "projects" + File.separator + "introduce"));

        // create temp dir
        try {
            String tempRoot = System.getProperty("temp.dir");
            this.tempDir = FileUtils.createTempDir("Service", "dir", tempRoot == null ? null : new File(tempRoot));
        } catch (IOException e) {
            throw new IllegalArgumentException("could not create temp dir", e);
        }

        // parse introduce service info
        try {
            // this.serviceDir = serviceDir;
            // if (this.serviceDir == null) this.serviceDir = testDir;
            File introduceXml = new File(this.testDir, "introduce.xml");
            if (!introduceXml.exists()) {
                introduceXml = new File(serviceDir, "introduce.xml");
            }
            this.serviceInfo = new IntroduceServiceInfo(introduceXml);
        } catch (Exception e) {
            throw new IllegalArgumentException("could not parse introduce.xml", e);
        }

        // set globus helper and port
        this.globus = new GlobusHelper(this.serviceInfo.isTransportSecurity(), this.tempDir, null);

        // set endpoint
        try {
            this.endpoint = this.globus.getServiceEPR("cagrid/" + this.serviceInfo.getServiceName());
        } catch (MalformedURIException e) {
            throw new IllegalArgumentException("endpoint badly formed");
        }

        // set metadataFile
        this.metadataFile = new File(this.testDir, "etc" + File.separator
            + IntroduceServiceInfo.INTRODUCE_SERVICEMETADATA_FILENAME);

       
        this.cadsrServiceDir = new File(System.getProperty("cadsr.dir", ".." + File.separator + ".." + File.separator
            + ".." + File.separator + "caGrid" + File.separator + "projects" + File.separator + "cadsr"));
    }


    public ArrayList<ServiceInvokeStep> getInvokeSteps() throws Exception {
        File methodsDir = new File(this.testDir, "test" + File.separator + "resources");
        return getInvokeSteps(this.serviceDir, this.testDir, methodsDir, this.endpoint);
    }


    public static ArrayList<ServiceInvokeStep> getInvokeSteps(File serviceDir, File testDir, File methodsDir,
        EndpointReferenceType endpoint) throws Exception {
        ArrayList<ServiceInvokeStep> steps = new ArrayList<ServiceInvokeStep>();

        File[] dirs = getInvokeDirs(methodsDir);

        // add steps
        for (File methodDir : dirs) {
            steps.add(new ServiceInvokeStep(serviceDir, testDir, methodDir,
                ServiceInvokeStep.parseParamName(methodDir), endpoint.getAddress().toString()));
        }

        return steps;
    }


    public static File[] getInvokeDirs(File methodsDir) {
        // get directory filters
        final HashSet<String> filterSet = new HashSet<String>();
        String filterString = System.getProperty("test.methods", "");
        StringTokenizer st = new StringTokenizer(filterString, ", \t\r\n");
        while (st.hasMoreTokens()) {
            filterSet.add(st.nextToken());
        }

        // get directories
        File[] dirs = methodsDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (!file.isDirectory() || !file.getName().matches("\\d+_\\w+")) {
                    return false;
                }
                if (filterSet.size() == 0) {
                    return true;
                }
                return filterSet.contains(String.valueOf(ServiceInvokeStep.parseParamPos(file)));
            }
        });

        // sort directories
        ArrayList<File> dirList = new ArrayList<File>(dirs.length);
        for (File dir : dirs) {
            dirList.add(dir);
        }
        Collections.sort(dirList, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return ServiceInvokeStep.parseParamPos(f1) - ServiceInvokeStep.parseParamPos(f2);
            }
        });
        dirs = dirList.toArray(new File[0]);

        return dirs;
    }


    public String getServiceName() {
        return this.serviceName;
    }


    public File getCadsrServiceDir() {
        return this.cadsrServiceDir;
    }


    public ServiceCreateStep getCreateServiceStep() {
        // set createServiceStep
        try {
            this.createServiceStep = new ServiceCreateStep(this.introduceDir, this.testDir, this.tempDir);
        } catch (Exception e) {
            throw new RuntimeException("could not instantiate CreateServiceStep", e);
        }
        this.serviceDir = this.createServiceStep.getServiceDir();
        return this.createServiceStep;
    }


    public EndpointReferenceType getEndpoint() {
        return this.endpoint;
    }


    public GlobusHelper getGlobus() {
        return this.globus;
    }


    public File getIntroduceDir() {
        return this.introduceDir;
    }


    public File getMetadataFile() {
        return this.metadataFile;
    }


    public File getServiceDir() {
        return this.serviceDir;
    }


    public IntroduceServiceInfo getServiceInfo() {
        return this.serviceInfo;
    }


    public File getTempDir() {
        return this.tempDir;
    }


    public File getTestDir() {
        return this.testDir;
    }
}
