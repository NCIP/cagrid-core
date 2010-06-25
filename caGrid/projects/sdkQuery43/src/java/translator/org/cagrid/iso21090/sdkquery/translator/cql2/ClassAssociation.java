package org.cagrid.iso21090.sdkquery.translator.cql2;

/**
 * ClassAssociation
 * Simple holder for tuples of information about an association 
 * from one class to another
 * 
 * @author David W. Ervin
 */
public class ClassAssociation {
    private String className;
    private String endName;


    public ClassAssociation(String className, String endName) {
        this.className = className;
        this.endName = endName;
    }


    public String getClassName() {
        return className;
    }


    public String getEndName() {
        return endName;
    }
}