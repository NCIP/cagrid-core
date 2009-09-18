package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** 
 *  DomainModelTypeTraverser
 *  TODO:DOCUMENT ME
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 9, 2006 
 * @version $Id$ 
 */
public class DomainModelTypeTraverser implements TypeTraverser {
	private Map<String, UMLClass> classesByName;
	private DomainModel model;
	
	public DomainModelTypeTraverser(DomainModel model) {
		this.model = model;
		this.classesByName = new HashMap<String, UMLClass>();
		UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
		for (int i = 0; i < classes.length; i++) {
			classesByName.put(classes[i].getPackageName() + "." + classes[i].getClassName(), classes[i]);
		}
	}
	

	public BaseType[] getBaseTypes() {
		BaseType[] types = new BaseType[classesByName.size()];
		int index = 0;
		Iterator nameIter = classesByName.keySet().iterator();
		while (nameIter.hasNext()) {
			types[index] = new BaseType((String) nameIter.next());
			index++;
		}
		return types;
	}


	public AssociatedType[] getAssociatedTypes(BaseType type) {
		List<AssociatedType> associatedTypes = new ArrayList<AssociatedType>();
		// get the uml class for the base type
		UMLClass typeClass = classesByName.get(type.getTypeName());
		// get associations from the model
		UMLAssociation[] associations = model.getExposedUMLAssociationCollection().getUMLAssociation();
		if (associations != null) {
			for (int i = 0; i < associations.length; i++) {
				UMLClassReference classRef = associations[i].getSourceUMLAssociationEdge()
					.getUMLAssociationEdge().getUMLClassReference();
				UMLClass refedClass = DomainModelUtils.getReferencedUMLClass(model, classRef);
				if (typeClass.equals(refedClass)) {
					// ref'ed source class is our base class, get the target
					UMLClassReference targetRef = associations[i].getTargetUMLAssociationEdge()
						.getUMLAssociationEdge().getUMLClassReference();
					UMLClass targetClass = DomainModelUtils.getReferencedUMLClass(model, targetRef);
					associatedTypes.add(new AssociatedType(targetClass.getPackageName() + "." + targetClass.getClassName(), 
						associations[i].getTargetUMLAssociationEdge().getUMLAssociationEdge().getRoleName()));
				}
			}
		}
		AssociatedType[] types = new AssociatedType[associatedTypes.size()];
		associatedTypes.toArray(types);
		return types;
	}


	public AttributeType[] getAttributes(BaseType type) {
		List<AttributeType> attributes = new ArrayList<AttributeType>();
		// get the UMLClass for the named base type
		UMLClass umlClass = classesByName.get(type.getTypeName());
		UMLAttribute[] umlAttributes = umlClass.getUmlAttributeCollection().getUMLAttribute();
		if (umlAttributes != null) {
			for (int i = 0; i < umlAttributes.length; i++) {
				String name = umlAttributes[i].getName();
				String datatype = umlAttributes[i].getDataTypeName();
				AttributeType attrib = new AttributeType(name, datatype);
				attributes.add(attrib);
			}
		}
		AttributeType[] atts = new AttributeType[attributes.size()];
		attributes.toArray(atts);
		return atts;
	}
}
