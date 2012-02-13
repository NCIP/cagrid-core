package gov.nih.nci.cagrid.introduce.upgrade.common;

import java.util.ArrayList;
import java.util.List;

    
public class StatusBase {
    public class Issue {
        private String issue;
        private String resolution;
        
        public Issue(String issue, String resolution){
            this.issue = issue;
            this.resolution = resolution;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }
    }
    public static final String UPGRADE_OK = "Upgrade OK";
    public static final String UPGRADE_FAIL = "Upgrade FAILED";
    public static final String UPGRADE_NOT_AVAILABLE = "Upgrade Not Available";
    public static final String UPGRADE_TYPE_INTRODUCE = "Introduce";
    public static final String UPGRADE_TYPE_EXTENSION = "Extension";
    
    
    private String type = "";
    private String fromVersion = "";
    private String toVersion = "";
    private String status = "";
    private StringBuilder description = null;
    private String name = "";
    private List issues;
 
    
    public StatusBase(String name, String type, String fromVersion, String toVersion){
        this.name = name;
        this.type = type;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.description = new StringBuilder();
        this.issues = new ArrayList();
    }
    
    public StatusBase() {
        this.description = new StringBuilder();
        this.issues = new ArrayList();
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description.toString();
    }
    
    public void addDescriptionLine(String line) {
        this.description.append("\t"+ line).append("\n");
    }
    
    public void addIssue(String issue, String resolution){
        this.issues.add(new Issue(issue,resolution));
    }
    
    public List getIssues(){
        return this.issues;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public String getToVersion() {
        return toVersion;
    }

    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
