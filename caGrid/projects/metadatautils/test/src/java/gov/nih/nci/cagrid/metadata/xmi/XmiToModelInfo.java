package gov.nih.nci.cagrid.metadata.xmi;

/** 
 *  XmiToModelInfo
 *  Container for XMI to Model test information
 * 
 * @author David Ervin
 * 
 * @created Oct 23, 2007 10:22:37 AM
 * @version $Id: XmiToModelInfo.java,v 1.1 2007-10-23 14:54:22 dervin Exp $ 
 */
public class XmiToModelInfo {

    private String xmiFilename;
    private String goldDomainModelFilename;
    private String projectShortName;
    private String projectVersion;
    
    public XmiToModelInfo(String xmiFilename, String goldDomainModelFilename,
        String projectShortName, String projectVersion) {
        this.xmiFilename = xmiFilename;
        this.goldDomainModelFilename = goldDomainModelFilename;
        this.projectShortName = projectShortName;
        this.projectVersion = projectVersion;
    }

    
    public String getGoldDomainModelFilename() {
        return goldDomainModelFilename;
    }

    
    public String getProjectShortName() {
        return projectShortName;
    }


    public String getProjectVersion() {
        return projectVersion;
    }

    
    public String getXmiFilename() {
        return xmiFilename;
    }      
}
