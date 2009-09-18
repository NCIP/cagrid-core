package gov.nih.nci.cagrid.introduce.portal;

import gov.nih.nci.cagrid.introduce.portal.discoverytools.NamespaceTypeToolsComponent;

import java.util.List;
import java.util.Properties;

import org.jdom.Element;

public class NamespaceTypeToolDescriptor {
	private static final String PROPERTIES = "properties";
	private static final String PROPERTY = "property";
	
	private String classname;
	private String type;
	private String displayName;
	
	private Properties properties;
	
	public NamespaceTypeToolDescriptor(Element el){
		properties = new Properties();
		classname = el.getAttributeValue("class");
		type = el.getAttributeValue("type");
		displayName = el.getAttributeValue("displayName");
		Element propertiesEl = el.getChild(PROPERTIES, el.getNamespace());
		if (propertiesEl != null) {
			List propertyElArr = propertiesEl.getChildren(PROPERTY,el.getNamespace());
			for(int i =0; i < propertyElArr.size(); i++){
				Element propEl = (Element)propertyElArr.get(i);
				String key = propEl.getAttributeValue("key");
				String value = propEl.getAttributeValue("value");
				this.properties.put(key, value);
			}
		}
	}
	
	public String getClassname(){
		return this.classname;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getProperty(String key){
		return properties.getProperty(key);
	}
	
	public NamespaceTypeToolsComponent getNamespaceTypeToolComponent() throws Exception{
		Class c = Class.forName(getClassname());
		Object obj = c.newInstance();
		return (NamespaceTypeToolsComponent)obj;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	
}
