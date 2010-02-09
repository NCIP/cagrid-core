package org.cagrid.identifiers.namingauthority.hibernate;

import java.net.URI;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


@Entity
@GenericGenerator(name = "id-generator", strategy = "native")
@Table(name = "identifier_value_keys")
public class IdentifierValueKey {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, name = "value_key", length = 1024)
    private String key;

    @CollectionOfElements(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "identifier_value_key_data")
    @Column(name = "value", length = 16777215)
    private List<String> values;

    @Column(nullable = true, unique = false)
    @Type(type = "org.cagrid.identifiers.namingauthority.hibernate.URIUserType")
    private URI readWriteIdentifier;

    public URI getReadWriteIdentifier() {
    	return readWriteIdentifier;
    }
    
    public void setReadWriteIdentifier(URI identifier) {
    	this.readWriteIdentifier = identifier;
    }
    
    public List<String> getValues() {
        return values;
    }


    public void setValues(List<String> values) {
        this.values = values;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
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
        IdentifierValueKey other = (IdentifierValueKey) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        
        /* I NEED THE ABOVE ONLY, DOES ANYONE HAVE A PROBLEM WITH THAT?
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
         */
        
        return true;
    }


    private void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

}
