package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;


public abstract class Retriever {
    private String[] requiredTypes;


    public abstract Object retrieve(IdentifierValues ivs) throws Exception;


    public String[] getRequiredTypes() {
        return this.requiredTypes;
    }


    public void setRequiredTypes(String[] types) {
        this.requiredTypes = types;
    }


    protected void validateTypes(IdentifierValues ivs) throws Exception {
        for (String type : requiredTypes) {
            String[] values = ivs.getValues(type);
            if (values == null || values.length == 0)
                throw new Exception("No type [" + type + "] found in indetifier values");
        }
    }
}
