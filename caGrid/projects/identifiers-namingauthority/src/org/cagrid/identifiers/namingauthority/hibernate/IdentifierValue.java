package org.cagrid.identifiers.namingauthority.hibernate;

public class IdentifierValue {
    private Long id;
    private String name;
    private String type;
    private String data;
    
    public IdentifierValue() {}
    

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getData() {
    	return data;
    }
    
    public void setData(String data) {
    	this.data = data;
    }
}
