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
package org.cagrid.iso21090.model.tools;

import gov.nih.nci.ncicb.xmiinout.domain.UMLAbstractModifier;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDatatype;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.domain.UMLVisibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class IvlUmlClass implements UMLClass {
    
    private UMLPackage parentPackage = null;

    public IvlUmlClass(UMLPackage parent) {
        parentPackage = parent;
    }


    public UMLAbstractModifier getAbstractModifier() {
        return new UMLAbstractModifier() {
            
            public boolean isAbstract() {
                return false;
            }
        };
    }


    public Set<UMLAssociation> getAssociations() {
        return Collections.emptySet();
    }


    public UMLAttribute getAttribute(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    public List<UMLAttribute> getAttributes() {
        UMLAttribute a1 = getFakeAttribute("highClosed",
            new UMLDatatype() {
                public String getName() {
                    return "Boolean";
                }
            });
        UMLAttribute a2 = getFakeAttribute("lowClosed",
            new UMLDatatype() {
                public String getName() {
                    return "Boolean";
                }
            });
        
        ArrayList<UMLAttribute> atts = new ArrayList<UMLAttribute>();
        atts.add(a1);
        atts.add(a2);
        
        return atts;
    }


    public List<UMLGeneralization> getGeneralizations() {
        return Collections.emptyList();
    }


    public String getName() {
        return "IVL";
    }


    public UMLPackage getPackage() {
        return parentPackage;
    }


    public String getStereotype() {
        // TODO Auto-generated method stub
        return null;
    }


    public UMLVisibility getVisibility() {
        // TODO Auto-generated method stub
        return null;
    }


    public UMLTaggedValue addTaggedValue(String arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }


    public UMLTaggedValue getTaggedValue(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    public UMLTaggedValue getTaggedValue(String arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }


    public Collection<UMLTaggedValue> getTaggedValues() {
        // TODO Auto-generated method stub
        return null;
    }


    public void removeTaggedValue(String arg0) {
        // TODO Auto-generated method stub

    }


    public Set<UMLDependency> getDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    
    private UMLAttribute getFakeAttribute(final String name, final UMLDatatype datatype) {
        return new UMLAttribute() {
            
            public void removeTaggedValue(String arg0) {
                // TODO Auto-generated method stub
            }
            
        
            public Collection<UMLTaggedValue> getTaggedValues() {
                return Collections.emptyList();
            }
            
        
            public UMLTaggedValue getTaggedValue(String arg0, boolean arg1) {
                // TODO Auto-generated method stub
                return null;
            }
            
        
            public UMLTaggedValue getTaggedValue(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }
            
        
            public UMLTaggedValue addTaggedValue(String arg0, String arg1) {
                // TODO Auto-generated method stub
                return null;
            }
            
        
            public UMLVisibility getVisibility() {
                // TODO Auto-generated method stub
                return null;
            }
            
        
            public String getName() {
                return name;
            }
            
        
            public UMLDatatype getDatatype() {
                return datatype;
            }
        };
    }
}
