package org.cagrid.cadsr.util;

/** 
 *  ModelProblems
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Feb 8, 2008 10:11:23 AM
 * @version $Id: ModelProblem.java,v 1.1 2009-01-07 04:45:41 oster Exp $ 
 */
public abstract class ModelProblem {

    protected String packageName = null;
    protected String className = null;
    
    
    public ModelProblem(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }
    

    public String getClassName() {
        return className;
    }


    public String getPackageName() {
        return packageName;
    }
    
    
    public abstract void writeToBuffer(StringBuffer buff);
}
