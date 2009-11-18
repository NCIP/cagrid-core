package org.cagrid.identifiers.namingauthority.hibernate;

import java.net.URI;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


@Entity
@GenericGenerator(name = "id-generator", strategy = "native")
@Table(name = "identifiers")
public class IdentifierMetadata {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @Type(type = "org.cagrid.identifiers.namingauthority.hibernate.URIUserType")
    private URI relativeIdentifier;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "identifier_id", nullable = false)
    private Collection<IdentifierValueKey> values;


    public Collection<IdentifierValueKey> getValues() {
        return this.values;
    }


    public void setValues(Collection<IdentifierValueKey> values) {
        this.values = values;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRelativeIdentifier() == null) ? 0 : getRelativeIdentifier().hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
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
        IdentifierMetadata other = (IdentifierMetadata) obj;
        if (getRelativeIdentifier() == null) {
            if (other.getRelativeIdentifier() != null)
                return false;
        } else if (!getRelativeIdentifier().equals(other.getRelativeIdentifier()))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }


    public void setRelativeIdentifier(URI relativeIdentifier) {
        this.relativeIdentifier = relativeIdentifier;
    }


    public URI getRelativeIdentifier() {
        return relativeIdentifier;
    }

}
