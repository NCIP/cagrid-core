package org.cagrid.cadsr.util;


/** 
 *  UMLAttributeProblem
 *  Class for problems discovered in caDSR model attributes
 * 
 * @author David Ervin
 * 
 * @created Feb 8, 2008 9:54:09 AM
 * @version $Id: UMLAttributeProblem.java,v 1.1 2009-01-07 04:45:41 oster Exp $ 
 */
public class UMLAttributeProblem extends ModelProblem {

    private String attributeName = null;
    private String attributeDescription = null;
    private int[] errorIndices = null;
    
    
    public UMLAttributeProblem(String packageName, String className, String attributeName, 
        String attributeDesc, int[] errorIndices) {
        super(packageName, className);
        this.attributeName = attributeName;
        this.attributeDescription = attributeDesc;
        this.errorIndices = errorIndices;
    }
    
    
    public String getAttributeDescription() {
        return attributeDescription;
    }
    
    
    public String getAttributeName() {
        return attributeName;
    }
    

    public int[] getErrorIndices() {
        return errorIndices;
    }
    
    
    public void writeToBuffer(StringBuffer buff) {
        buff.append("UMLClassProblem:\n");
        buff.append("\tPackage: ").append(packageName).append("\n");
        buff.append("\tClass: ").append(className).append("\n");
        buff.append("\tAttribute: ").append(attributeName).append("\n");
        buff.append("\tDescription: ").append(attributeDescription).append("\n");
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
