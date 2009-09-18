package org.cagrid.gme.domain;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Type;


@Embeddable
public class XMLSchema {

    @Column(nullable = false)
    private XMLSchemaDocument rootDocument;

    @CollectionOfElements
    @JoinTable(name = "xmlschema_additionaldocuments", joinColumns = {@JoinColumn(name = "referencing_xmlschema_id")})
    private Set<XMLSchemaDocument> additionalSchemaDocuments = new HashSet<XMLSchemaDocument>();

    @Column(nullable = false, unique = true)
    @Type(type = "org.cagrid.gme.persistence.hibernate.types.URIUserType")
    private URI targetNamespace;


    public XMLSchema() {
    }


    /**
     * @return the rootDocument
     */
    public XMLSchemaDocument getRootDocument() {
        return rootDocument;
    }


    /**
     * @param rootDocument
     *            the rootDocument to set
     */
    public void setRootDocument(XMLSchemaDocument rootDocument) {
        this.rootDocument = rootDocument;
    }


    public Set<XMLSchemaDocument> getAdditionalSchemaDocuments() {
        return additionalSchemaDocuments;
    }


    public void setAdditionalSchemaDocuments(Set<XMLSchemaDocument> schemaDocuments) {
        this.additionalSchemaDocuments = schemaDocuments;
    }


    public URI getTargetNamespace() {
        return targetNamespace;
    }


    public void setTargetNamespace(URI targetNamespace) {
        this.targetNamespace = targetNamespace;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((targetNamespace == null) ? 0 : targetNamespace.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final XMLSchema other = (XMLSchema) obj;
        if (targetNamespace == null) {
            if (other.targetNamespace != null)
                return false;
        } else if (!targetNamespace.equals(other.getTargetNamespace()))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return this.targetNamespace.toString();
    }
}