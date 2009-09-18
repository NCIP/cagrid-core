package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;


/**
 * Used for organizing imports for wsdl
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SchemaInformation {

	private NamespaceType namespace;
	private SchemaElementType type;

	public SchemaInformation(NamespaceType namespace, SchemaElementType type){
		this.namespace = namespace;
		this.type = type;
		
	}

	public NamespaceType getNamespace() {
		return namespace;
	}

	public SchemaElementType getType() {
		return type;
	}

	public void setType(SchemaElementType type) {
		this.type = type;
	}

	public void setNamespace(NamespaceType namespace) {
		this.namespace = namespace;
	}

}