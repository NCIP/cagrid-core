package org.cagrid.cacore.sdk4x.cql2.processor;

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