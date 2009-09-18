package org.cagrid.cadsr.util;


/** 
 *  UMLClassProblem
 *  Class for problems discovered in caDSR model attributes
 * 
 * @author David Ervin
 * 
 * @created Feb 8, 2008 9:54:09 AM
 * @version $Id: UMLClassProblem.java,v 1.1 2009-01-07 04:45:41 oster Exp $ 
 */
public class UMLClassProblem extends ModelProblem {
    
    protected String classDescription = null;
    protected int[] errorIndices = null;
    
    public UMLClassProblem(String packageName, String className, String classDesc, int[] errorIndices) {
        super(packageName, className);
        this.classDescription = classDesc;
        this.errorIndices = errorIndices;
    }
    
    
    public String getClassDescription() {
        return classDescription;
    }

    
    public int[] getErrorIndices() {
        return errorIndices;
    }
    
    
    public void writeToBuffer(StringBuffer buff) {
        buff.append("UMLClassProblem:\n");
        buff.append("\tPackage: ").append(packageName).append("\n");
        buff.append("\tClass: ").append(className).append("\n");
        buff.append("\tDescription: ").append(classDescription).append("\n");
        buff.append("\tErrors at char position(s): ");
        for (int i = 0; i < errorIndices.length; i++) {
            buff.append(errorIndices[i]);
            if (i + 1 < errorIndices.length) {
                buff.append(", ");
            }
        }
    }
    
    
    public String toString() {
        StringBuffer buff = new StringBuffer();
        writeToBuffer(buff);
        return buff.toString();
    }
}
