/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.introduce.extensions.sdk.discovery;

/**
 * @author oster
 */
public class SDKGenerationInformation {

    private String projectName;
    private String namespacePrefix;

    private String xmiFile;
    private String packageIncludes;
    private String packageExcludes;


    public SDKGenerationInformation() {
        super();
    }


    public SDKGenerationInformation(String projectName, String namespacePrefix, String excludes, String includes,
        String file) {
        super();

        this.projectName = projectName;
        this.namespacePrefix = namespacePrefix;

        this.packageExcludes = excludes;
        this.packageIncludes = includes;
        this.xmiFile = file;
    }


    public String getPackageExcludes() {
        return this.packageExcludes;
    }


    public void setPackageExcludes(String packageExcludes) {
        this.packageExcludes = packageExcludes;
    }


    public String getPackageIncludes() {
        return this.packageIncludes;
    }


    public void setPackageIncludes(String packageIncludes) {
        this.packageIncludes = packageIncludes;
    }


    public String getXmiFile() {
        return this.xmiFile;
    }


    public void setXmiFile(String xmiFile) {
        this.xmiFile = xmiFile;
    }


    @Override
    public String toString() {
        return "Generation will read file: " + this.xmiFile + "\n\tfor project: " + this.projectName
            + " \nincluding packages: " + this.packageIncludes + "\n\texcluding packages: " + this.packageExcludes
            + "\ngenerating schemas under namespace prefix: " + this.namespacePrefix;
    }


    public String getNamespacePrefix() {
        return namespacePrefix;
    }


    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }


    public String getProjectName() {
        return projectName;
    }


    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
