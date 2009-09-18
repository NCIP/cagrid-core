import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLClassCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.io.FileWriter;
import java.io.Writer;

import org.cagrid.mms.service.impl.cadsr.DomainModelBuilder;


public class DomainModelGenerationExample {

    public static void main(String[] args) {
        try {
            ApplicationService appService = ApplicationServiceProvider
                .getApplicationServiceFromUrl("http://cadsrapi.nci.nih.gov/cadsrapi40/");

            DomainModelBuilder builder = new DomainModelBuilder(appService);

            Project project = new Project();
            project.setVersion("3.2");
            project.setShortName("caCORE 3.2");
            System.out.println("Creating domain model for project: " + project.getShortName() + " (version:"
                + project.getVersion() + ")");

            long start = System.currentTimeMillis();

            // UNCOMMENT FOR: Whole project
            // DomainModel domainModel = builder.createDomainModel(project);

            // UNCOMMENT FOR: a single package
            DomainModel domainModel = builder.createDomainModelForPackages(project,
                new String[]{"gov.nih.nci.cabio.domain"});

            // UNCOMMENT FOR: a specific set of classes
            // String classNames[] = new String[]{Gene.class.getName(),
            // Taxon.class.getName()};
            // DomainModel domainModel =
            // builder.createDomainModelForClasses(project, classNames);

            // UNCOMMENT FOR: a specific set of classes, with excluded
            // associations
            // String classNames[] = new String[]{Gene.class.getName(),
            // Chromosome.class.getName(), Taxon.class.getName(),
            // Tissue.class.getName()};
            // UMLAssociationExclude exclude1 = new
            // UMLAssociationExclude(Chromosome.class.getName(), "chromosome",
            // Gene.class.getName(), "geneCollection");
            // UMLAssociationExclude exclude2 = new
            // UMLAssociationExclude(Tissue.class.getName(), "*", "*", "*");
            // UMLAssociationExclude associationExcludes[] = new
            // UMLAssociationExclude[]{exclude1, exclude2};
            // DomainModel domainModel =
            // builder.createDomainModelForClassesWithExcludes(project,
            // classNames,
            // associationExcludes);

            // work around for people getting the "illegal character" problem
            // for smart quotes
            replaceIllegalCharacters(domainModel);

            Writer writer = new FileWriter(project.getShortName().replace(" ", "_") + "-" + project.getVersion()
                + "_DomainModel.xml");
            MetadataUtils.serializeDomainModel(domainModel, writer);
            writer.close();

            double duration = (System.currentTimeMillis() - start) / 1000.0;
            System.out.println("Domain Model generation took:" + duration + " seconds.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * @param domainModel
     */
    private static void replaceIllegalCharacters(DomainModel domainModel) {
        StringBuilder sb = new StringBuilder();
        char[] badChars = {0x18, 0x19};
        DomainModelExposedUMLClassCollection classColl = domainModel.getExposedUMLClassCollection();
        UMLClass[] classes = classColl.getUMLClass();
        for (UMLClass klass : classes) {
            String classDesc = klass.getDescription();
            if (classDesc != null) {
                for (char element : badChars) {
                    if (classDesc.indexOf(element) > -1) {
                        sb.append("Class description " + klass.getClassName() + " contains bad character: " + classDesc
                            + "\n");
                        classDesc = classDesc.replace(element, ' ');
                    }
                }
                klass.setDescription(classDesc);
            }
            UMLClassUmlAttributeCollection attColl = klass.getUmlAttributeCollection();
            UMLAttribute[] atts = attColl.getUMLAttribute();
            for (UMLAttribute att : atts) {
                String desc = att.getDescription();
                if (desc != null) {
                    for (char element : badChars) {
                        if (desc.indexOf(element) > -1) {
                            sb.append("Attribute description " + klass.getClassName() + "." + att.getName()
                                + " contains bad character: " + desc + "\n");
                            desc = desc.replace(element, ' ');
                        }
                    }
                    att.setDescription(desc);
                }
                att.setSemanticMetadata(fixSemanticMetadata(sb, klass.getClassName() + "." + att.getName(), att
                    .getSemanticMetadata(), badChars));
            }
            klass.setSemanticMetadata(fixSemanticMetadata(sb, klass.getClassName(), klass.getSemanticMetadata(),
                badChars));
        }

        if (sb.length() > 0) {
            System.out.println("\n\n\n========== CHARACTER PROBLEMS WITH MODEL ==========");
            System.out.println(sb.toString());
        } else {
            System.out.println("No illegal characters found in model");
        }
    }


    private static SemanticMetadata[] fixSemanticMetadata(StringBuilder sb, String name, SemanticMetadata[] semMetColl,
        char[] badChars) {
        for (SemanticMetadata element : semMetColl) {
            String desc = element.getConceptDefinition();
            if (desc != null) {
                for (char element2 : badChars) {
                    if (desc.indexOf(element2) > -1) {
                        sb.append("SemanticMetadata " + name + " contains bad character: " + desc + "\n");
                        desc = desc.replace(element2, ' ');
                    }
                }
                element.setConceptDefinition(desc);
            }
        }
        return semMetColl;
    }

}
