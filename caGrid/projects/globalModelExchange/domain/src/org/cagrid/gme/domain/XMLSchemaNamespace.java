package org.cagrid.gme.domain;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;


/**
 * This generally is just a wrapper for a URI, set in the context of being a
 * targetNamespace for an XML schema.
 */
@Embeddable
public class XMLSchemaNamespace {
    @Column(nullable = false, unique = true)
    @Type(type = "org.cagrid.gme.persistence.hibernate.types.URIUserType")
    private URI uri;


    // Needed for Castor to work
    public XMLSchemaNamespace() {

    }


    public XMLSchemaNamespace(String uri) throws URISyntaxException {
        this.uri = new URI(uri);
    }


    public XMLSchemaNamespace(URI uri) {
        this.uri = uri;
    }


    public URI getURI() {
        return this.uri;
    }


    public void setURI(URI uri) {
        this.uri = uri;
    }


    @Override
    public String toString() {
        return (this.uri == null) ? "null" : this.uri.toString();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.uri == null) ? 0 : this.uri.hashCode());
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
        final XMLSchemaNamespace other = (XMLSchemaNamespace) obj;
        if (this.uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!this.uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

}
