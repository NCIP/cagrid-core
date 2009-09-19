/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nci.nih.cagrid.tests.core.util.IntroduceServiceInfo;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;


/**
 * This step deploys a service to a temporary globus container by running the
 * deployGlobus ant task in the service directory.
 * 
 * @author Patrick McConnell
 */
public class GlobusDeployServiceStep extends Step {
    private GlobusHelper globus;
    private File serviceDir;
    private String target;
    private List<String> args;


    public GlobusDeployServiceStep(GlobusHelper globus, File serviceDir) {
        this(globus, serviceDir, null);
    }


    public GlobusDeployServiceStep(GlobusHelper globus, File serviceDir, String target) {
        this(globus, null, serviceDir, target);

        this.globus = globus;
        this.serviceDir = serviceDir;
        this.target = target;
    }


    public GlobusDeployServiceStep(GlobusHelper globus, GlobusHelper secureGlobus, File serviceDir) {
        this(globus, secureGlobus, serviceDir, null);
    }


    public GlobusDeployServiceStep(GlobusHelper globus, GlobusHelper secureGlobus, File serviceDir, String target) {
        super();

        this.serviceDir = serviceDir;
        this.target = target;

        if (globus == null) {
            this.globus = globus;
        } else if (secureGlobus == null) {
            this.globus = secureGlobus;
        } else {
            try {
                IntroduceServiceInfo introduce = new IntroduceServiceInfo(new File(serviceDir, "introduce.xml"));
                if (introduce.isTransportSecurity()) {
                    this.globus = secureGlobus;
                } else {
                    this.globus = globus;
                }
            } catch (Exception e) {
                throw new RuntimeException("unable to get service info from introduce.xml", e);
            }
        }
    }


    @Override
    public void runStep() throws Throwable {
        if (this.target == null) {
            this.globus.deployService(this.serviceDir, this.args);
        } else {
            this.globus.deployService(this.serviceDir, this.target, this.args);
        }
    }


    public List<String> getArgs() {
        return this.args;
    }


    public void setArgs(List<String> args) {
        this.args = args;
    }
}