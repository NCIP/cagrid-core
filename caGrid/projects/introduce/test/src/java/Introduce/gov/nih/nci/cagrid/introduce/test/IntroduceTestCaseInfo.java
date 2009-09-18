package gov.nih.nci.cagrid.introduce.test;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;

import java.io.File;


/**
 * BaseTestCaseInfo
 * 
 * @author oster
 * @created Apr 9, 2007 4:30:09 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class IntroduceTestCaseInfo extends TestCaseInfo {

    protected String name;
    protected String dir;
    protected String packageName;
    protected String packageDir;
    protected String namespaceDomain;


    public IntroduceTestCaseInfo() {

    }


    public IntroduceTestCaseInfo(String name, String dir, String packageName, String namespaceDomain) {
        this.name = name;
        this.dir = dir;
        this.packageName = packageName;
        this.namespaceDomain = namespaceDomain;
    }


    public String getDir() {
        return this.dir;
    }


    public void setDir(String dir) {
        this.dir = dir;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getNamespace() {
        return this.namespaceDomain;
    }


    public void setNamespaceDomain(String namespaceDomain) {
        this.namespaceDomain = namespaceDomain;
    }


    public String getPackageName() {
        return this.packageName;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public String getPackageDir() {
        return getPackageName().replace('.', File.separatorChar);
    }


    public String getResourceFrameworkType() {
        return IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE + "," + IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE;
    }
}
