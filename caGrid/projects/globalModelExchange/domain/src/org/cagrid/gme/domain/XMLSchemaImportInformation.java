package org.cagrid.gme.domain;

import java.util.HashSet;
import java.util.Set;


/**
 * Contains information about the imports of a particular XMLSchema, identified
 * by it's targetNamespace (represented by an XMLSchemaNamespace). NOTE: the
 * hashcode of this Class only considers the targetNamespace, so one should not
 * put multiple instances of this Class, referring to the same XMLSchema but
 * with different imports, in a Set.
 */
public class XMLSchemaImportInformation {
    private XMLSchemaNamespace targetNamespace;
    private Set<XMLSchemaNamespace> imports = new HashSet<XMLSchemaNamespace>();


    public XMLSchemaNamespace getTargetNamespace() {
        return this.targetNamespace;
    }


    public void setTargetNamespace(XMLSchemaNamespace targetNamespace) {
        this.targetNamespace = targetNamespace;
    }


    public Set<XMLSchemaNamespace> getImports() {
        return this.imports;
    }


    public void setImports(Set<XMLSchemaNamespace> imports) {
        this.imports = imports;
        // don't allow null List
        if (this.imports == null) {
            this.imports = new HashSet<XMLSchemaNamespace>();
        }
    }


    /**
     * Only considers the targetNamespace, so one should not put multiple
     * instances of this Class, referring to the same XMLSchema but with
     * different imports, in a Set.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.targetNamespace == null) ? 0 : this.targetNamespace.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        XMLSchemaImportInformation other = (XMLSchemaImportInformation) obj;
        if (this.imports == null) {
            if (other.imports != null) {
                return false;
            }
        } else if (!this.imports.equals(other.imports)) {
            return false;
        }
        if (this.targetNamespace == null) {
            if (other.targetNamespace != null) {
                return false;
            }
        } else if (!this.targetNamespace.equals(other.targetNamespace)) {
            return false;
        }
        return true;
    }

}