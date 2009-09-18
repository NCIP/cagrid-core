package gov.nih.nci.cagrid.introduce.servicetasks.deployment.validator;

public abstract class DeploymentValidator {
    String baseDir = null;


    public DeploymentValidator(String baseDir) {
        this.baseDir = baseDir;
    }


    public abstract void validate() throws Exception;


    public String getBaseDir() {
        return this.baseDir;
    }

}
