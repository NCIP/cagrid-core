package org.cagrid.gme.domain;

import java.util.HashSet;
import java.util.Set;


/**
 * This contains a collection of XMLSchemas, indexed by their respective
 * targetNamespaces, as well as a List of the targetNamespaces each of them
 * imports (also index by the importing schema's targetNamespace). This
 * information can be used to reconstruct a graph of schemas and their
 * relationships to each other. It could be processed by a library like JUNG
 * (http://jung.sourceforge.net/).
 */
public class XMLSchemaBundle {
    // Castor supports the use of Maps, but would force an awkward and
    // inefficient XML representation as the information that is used as the
    // "key" is also contained within the value (i.e the namespace).

    // I also tried to use Sets in the interface, and Maps in the
    // implementation, but Castor assumes it can directly modify the Sets
    // returned by the getters (which doesn't work when I return a new Set as a
    // view of the Map)
    private Set<XMLSchema> xmlSchemaCollection = new HashSet<XMLSchema>();
    private Set<XMLSchemaImportInformation> importInformation = new HashSet<XMLSchemaImportInformation>();;


    /**
     * @return the Set of XMLSchemas
     */
    public Set<XMLSchema> getXMLSchemas() {
        return this.xmlSchemaCollection;
    }


    /**
     * Sets the new Set of XMLSchemas in the bundle. NOTE: this Set must be
     * consistent with the importInformation Set, in that every namespace
     * referenced in the ImportInformation Set must have a corresponding
     * XMLSchema, with that targetNamespace, in this Set.
     * 
     * @param xmlSchemaCollection
     *            the new Set of schemas
     */
    public void setXMLSchemas(Set<XMLSchema> xmlSchemaCollection) {
        // disallow null
        if (xmlSchemaCollection == null) {
            this.xmlSchemaCollection = new HashSet<XMLSchema>();
        } else {
            this.xmlSchemaCollection = xmlSchemaCollection;
        }
    }


    /**
     * @return the Set of ImportInformation
     */
    public Set<XMLSchemaImportInformation> getImportInformation() {
        return this.importInformation;
    }


    /**
     * Sets the new Set of XMLSchemaImportInformation in the bundle. NOTE: this
     * Set must be consistent with the XMLSchema Set, in that every namespace
     * referenced in the ImportInformation Set must have a corresponding
     * XMLSchema, with that targetNamespace, in the XMLSchema Set.
     * 
     * @param importInformation
     *            the new Set of XMLSchemaImportInformation
     */
    public void setImportInformation(Set<XMLSchemaImportInformation> importInformation) {
        // disallow null
        if (importInformation == null) {
            this.importInformation = new HashSet<XMLSchemaImportInformation>();
        } else {
            this.importInformation = importInformation;
        }
    }


    /**
     * @return a Set of all of the targetNamespaces (represented as
     *         XMLSchemaNamespaces) of schemas in the Set
     */
    public Set<XMLSchemaNamespace> getXMLSchemaTargetNamespaces() {
        assert this.xmlSchemaCollection != null;

        Set<XMLSchemaNamespace> result = new HashSet<XMLSchemaNamespace>(this.xmlSchemaCollection.size());
        for (XMLSchema s : this.xmlSchemaCollection) {
            result.add(new XMLSchemaNamespace(s.getTargetNamespace()));
        }
        return result;
    }


    /**
     * Utility accessor for retrieving an XMLSchema by its targetNamespace
     * 
     * @param targetNamespace
     *            the targetNamespace of the XMLSchema that is desired
     * @return the XMLSchema with the corresponding targetNamespace, or null, if
     *         it does not exist in the Set
     */
    public XMLSchema getXMLSchemaForTargetNamespace(XMLSchemaNamespace targetNamespace) {
        assert this.xmlSchemaCollection != null;

        for (XMLSchema s : this.xmlSchemaCollection) {
            if (s.getTargetNamespace().equals(targetNamespace.getURI())) {
                return s;
            }
        }
        return null;
    }


    /**
     * Utility accessor for retrieving an XMLSchema's imports by its
     * targetNamespace
     * 
     * @param targetNamespace
     *            the targetNamespace of the XMLSchema that is desired
     * @return the XMLSchema with the corresponding targetNamespace, or null, if
     *         it does not exist in the Set (which means there are no imports
     *         for it)
     */
    public XMLSchemaImportInformation getImportInformationForTargetNamespace(XMLSchemaNamespace targetNamespace) {
        assert this.importInformation != null;

        for (XMLSchemaImportInformation ii : this.importInformation) {
            if (ii.getTargetNamespace().equals(targetNamespace)) {
                return ii;
            }
        }
        return null;
    }


    /**
     * Utility accessor for retrieving the set of imported XMLSchemas for an
     * XMLSchema identified by its targetNamespace
     * 
     * @param targetNamespace
     *            the targetNamespace of the XMLSchema for which the imported
     *            Schemas are desired
     * @return a Set of XMLSchema which are imported by the XMLSchema with the
     *         given targetNamespace, or null, if no such Schema exists
     */
    public Set<XMLSchema> getImportedXMLSchemasForTargetNamespace(XMLSchemaNamespace targetNamespace) {
        assert this.importInformation != null;
        assert this.xmlSchemaCollection != null;

        XMLSchemaImportInformation schemaImportInformation = getImportInformationForTargetNamespace(targetNamespace);
        if (schemaImportInformation == null) {
            return null;
        }

        Set<XMLSchema> results = new HashSet<XMLSchema>();
        // walk the imported Schemas, and build up a Set containing the actual
        // XMLSchema instances
        for (XMLSchemaNamespace namespace : schemaImportInformation.getImports()) {
            XMLSchema schema = getXMLSchemaForTargetNamespace(namespace);
            assert schema != null;
            results.add(schema);
        }

        // return the created set
        return results;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.importInformation == null) ? 0 : this.importInformation.hashCode());
        result = prime * result + ((this.xmlSchemaCollection == null) ? 0 : this.xmlSchemaCollection.hashCode());
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
        XMLSchemaBundle other = (XMLSchemaBundle) obj;
        if (this.importInformation == null) {
            if (other.importInformation != null) {
                return false;
            }
        } else if (!this.importInformation.equals(other.importInformation)) {
            return false;
        }
        if (this.xmlSchemaCollection == null) {
            if (other.xmlSchemaCollection != null) {
                return false;
            }
        } else if (!this.xmlSchemaCollection.equals(other.xmlSchemaCollection)) {
            return false;
        }
        return true;
    }

}
