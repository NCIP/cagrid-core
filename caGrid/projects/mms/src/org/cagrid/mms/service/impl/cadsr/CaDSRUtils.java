package org.cagrid.mms.service.impl.cadsr;

import gov.nih.nci.cadsr.domain.DataElement;
import gov.nih.nci.cadsr.domain.ValueDomain;
import gov.nih.nci.cadsr.umlproject.domain.AttributeTypeMetadata;
import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.SemanticMetadata;
import gov.nih.nci.cadsr.umlproject.domain.TypeEnumerationMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLAttributeMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cagrid.metadata.common.Enumeration;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.common.ValueDomainEnumerationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author oster
 */
public class CaDSRUtils {

	protected static Log LOG = LogFactory.getLog(CaDSRUtils.class.getName());


	public static String getPackageName(UMLClassMetadata umlClass) {
		String pkg = "";
		String fqn = umlClass.getFullyQualifiedName();
		int ind = fqn.lastIndexOf(".");
		if (ind >= 0) {
			pkg = fqn.substring(0, ind);
		}

		return pkg;
	}


	public static Project findCompleteProject(ApplicationService appService, Project prototype)
		throws CaDSRGeneralException {
		if (prototype == null) {
			throw new CaDSRGeneralException("Null project not valid.");
		}

		// clear this out and refresh it (in case its stale)
		prototype.setId(null);

		List completeProjects = new ArrayList();
		Iterator projectIter = null;
		Project proj = null;
		try {
			projectIter = appService.search(Project.class, prototype).iterator();
		} catch (Exception ex) {
			throw new CaDSRGeneralException("Error retrieving complete project: " + ex.getMessage(), ex);
		}
		// should be ONLY ONE project from the caDSR
		while (projectIter.hasNext()) {
			completeProjects.add(projectIter.next());
		}
		if (completeProjects.size() == 1) {
			proj = (Project) completeProjects.get(0);
		} else if (completeProjects.size() == 0) {
			throw new CaDSRGeneralException("No project found in caDSR");
		} else {
			throw new CaDSRGeneralException("More than one project (" + completeProjects.size()
				+ ") found.  Prototype project is ambiguous");
		}

		return proj;
	}


	public static gov.nih.nci.cagrid.metadata.common.UMLClass convertClassToUMLClass(ApplicationService appService,
		String projectShortName, String projectVersion, UMLClassMetadata classMetadata) throws ApplicationException {
		gov.nih.nci.cagrid.metadata.common.UMLClass converted = null;
		if (classMetadata != null) {
			converted = new gov.nih.nci.cagrid.metadata.common.UMLClass();
			convertClass(appService, projectShortName, projectVersion, converted, classMetadata);
		}
		return converted;
	}


	private static void convertClass(ApplicationService appService, String projectShortName, String projectVersion,
		gov.nih.nci.cagrid.metadata.common.UMLClass result, UMLClassMetadata classMetadata) throws ApplicationException {
		if (classMetadata == null || result == null) {
			return;
		}

		result.setClassName(classMetadata.getName());
		result.setDescription(classMetadata.getDescription());
		if (result.getDescription() == null) {
			result.setDescription("");
		}
		result.setId(classMetadata.getId());
		result.setPackageName(CaDSRUtils.getPackageName(classMetadata));
		result.setProjectName(projectShortName);
		result.setProjectVersion(projectVersion);

		UMLAttribute[] attributes = createClassAttributes(appService, classMetadata);
		UMLClassUmlAttributeCollection attCol = new UMLClassUmlAttributeCollection();
		attCol.setUMLAttribute(attributes);
		result.setUmlAttributeCollection(attCol);

		gov.nih.nci.cagrid.metadata.common.SemanticMetadata[] smArray = semanticMetadataCollectionToArray(classMetadata
			.getSemanticMetadataCollection());
		result.setSemanticMetadata(smArray);

	}


	public static UMLClass convertClassToDataUMLClass(ApplicationService appService, String projectShortName,
		String projectVersion, UMLClassMetadata classMetadata) throws ApplicationException {
		UMLClass converted = null;
		if (classMetadata != null) {
			converted = new UMLClass();
			converted.setAllowableAsTarget(true);
			convertClass(appService, projectShortName, projectVersion, converted, classMetadata);
		}
		return converted;
	}


	public static UMLAttribute[] createClassAttributes(ApplicationService appService, UMLClassMetadata classMetadata)
		throws ApplicationException {
		long start = System.currentTimeMillis();

		HQLCriteria criteria = new HQLCriteria(
			"SELECT DISTINCT att, att.dataElement.valueDomain, att.attributeTypeMetadata, att.dataElement FROM UMLAttributeMetadata att "
				+ "LEFT JOIN FETCH att.semanticMetadataCollection "
				+ "WHERE att.UMLClassMetadata.id='"
				+ classMetadata.getId() + "'  ORDER BY att.name");

		List rList = appService.query(criteria, UMLAttributeMetadata.class.getName());

		Map attMap = new HashMap();
		int ind = 0;
		for (Iterator resultsIterator = rList.iterator(); resultsIterator.hasNext(); ind++) {
			long attstart = System.currentTimeMillis();
			Object[] result = (Object[]) resultsIterator.next();
			UMLAttributeMetadata attMD = (UMLAttributeMetadata) result[0];
			ValueDomain vd = (ValueDomain) result[1];
			AttributeTypeMetadata attTypemd = (AttributeTypeMetadata) result[2];
			DataElement de = (DataElement) result[3];

			// filter out duplicate attributes (caCORE bug in the materialized
			// view?)
			if (attMap.containsKey(de.getPublicID())) {
				continue;
			}

			// build the attribute
			UMLAttribute converted = new UMLAttribute();
			String description = attMD.getDescription();
			if (description == null) {
				description = "";
			}
			converted.setDescription(description);
			converted.setName(attMD.getName());
			if (de.getPublicID() != null) {
				converted.setPublicID(de.getPublicID().longValue());
			}
			if (de.getVersion() != null) {
				converted.setVersion(de.getVersion().floatValue());
			}
			converted.setDataTypeName(attTypemd.getValueDomainDataType());

			// add a value domain
			gov.nih.nci.cagrid.metadata.common.ValueDomain attVD = new gov.nih.nci.cagrid.metadata.common.ValueDomain();
			attVD.setLongName(vd.getLongName());
			attVD.setUnitOfMeasure(vd.getUOMName());
			converted.setValueDomain(attVD);

			// populate vd semantic md
			attVD.setSemanticMetadata(semanticMetadataCollectionToArray(attTypemd.getSemanticMetadataCollection()));

			// populate enumeration
			ValueDomainEnumerationCollection enumCollection = new ValueDomainEnumerationCollection();
			attVD.setEnumerationCollection(enumCollection);

			HQLCriteria enumcriteria = new HQLCriteria("SELECT DISTINCT enum FROM TypeEnumerationMetadata enum "
				+ "LEFT JOIN FETCH enum.semanticMetadataCollection "
				+ "WHERE enum.id in (SELECT e.id FROM TypeEnumerationMetadata e, AttributeTypeMetadata t"
				+ " WHERE t.typeEnumerationCollection.id=e.id AND t.id='" + attTypemd.getId() + "')");

			List enumRList = appService.query(enumcriteria, AttributeTypeMetadata.class.getName());

			Iterator typeEnumIter = enumRList.iterator();
			Enumeration enumArr[] = new Enumeration[enumRList.size()];
			int i = 0;
			while (typeEnumIter.hasNext()) {
				TypeEnumerationMetadata typeEnum = (TypeEnumerationMetadata) typeEnumIter.next();
				Enumeration enumer = new Enumeration();
				enumer.setPermissibleValue(typeEnum.getPermissibleValue());
				enumer.setValueMeaning(typeEnum.getValueMeaning());
				enumArr[i++] = enumer;
				// populate enumeration semantic md
				enumer.setSemanticMetadata(semanticMetadataCollectionToArray(typeEnum.getSemanticMetadataCollection()));
			}
			enumCollection.setEnumeration(enumArr);

			// populate att semantic md
			gov.nih.nci.cagrid.metadata.common.SemanticMetadata[] metadatas = semanticMetadataCollectionToArray(attMD
				.getSemanticMetadataCollection());
			converted.setSemanticMetadata(metadatas);

			attMap.put(de.getPublicID(), converted);
			LOG.debug("Converted attribute: " + attMD.getName() + " in " + (System.currentTimeMillis() - attstart)
				/ 1000.0 + " seconds.");

		}

		UMLAttribute[] attArr = new UMLAttribute[attMap.size()];
		attMap.values().toArray(attArr);
		double duration = (System.currentTimeMillis() - start) / 1000.0;
		LOG.info(classMetadata.getFullyQualifiedName() + " attribute conversion took " + duration + " seconds.");

		return attArr;
	}


	public static gov.nih.nci.cagrid.metadata.common.SemanticMetadata[] semanticMetadataCollectionToArray(
		Collection semanticMetadata) {
		gov.nih.nci.cagrid.metadata.common.SemanticMetadata[] smArray = new gov.nih.nci.cagrid.metadata.common.SemanticMetadata[semanticMetadata
			.size()];

		Iterator iter = semanticMetadata.iterator();
		int i = 0;
		while (iter.hasNext()) {
			SemanticMetadata sm = (SemanticMetadata) iter.next();
			gov.nih.nci.cagrid.metadata.common.SemanticMetadata converted = new gov.nih.nci.cagrid.metadata.common.SemanticMetadata();
			converted.setConceptCode(sm.getConceptCode());
			converted.setConceptDefinition(sm.getConceptDefinition());
			converted.setConceptName(sm.getConceptName());
			converted.setOrder(sm.getOrder());
			converted.setOrderLevel(sm.getOrderLevel());
			smArray[i++] = converted;

		}

		return smArray;
	}

}
