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
