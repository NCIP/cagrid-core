package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Util to compare two domain models see if they're "equal"
 * 
 * @author David
 */
public class DomainModelComparator {
    
    
    public static class DomainModelComparisonResult {
        private String message = null;
        private boolean equal = false;
        
        public DomainModelComparisonResult(String message, boolean equal) {
            this.message = message;
            this.equal = equal;
        }
        
        
        public String getMessage() {
            return message;
        }
        
        
        public boolean modelsAreEqual() {
            return equal;
        }
    }
    

    public static DomainModelComparisonResult testModelEquivalence(DomainModel model1, DomainModel model2) {
        DomainModelComparisonResult result = null;
        result = compareModels(model1, model2);
        return result;
    }
    
    
    private static DomainModelComparisonResult compareModels(DomainModel goldModel, DomainModel testModel) {
        // project short name and version must match
        if (!Utils.equals(goldModel.getProjectShortName(), testModel.getProjectShortName())) {
            return new DomainModelComparisonResult("Project short names did not match: expected " 
                + goldModel.getProjectShortName() + " but was " + testModel.getProjectShortName(), false);
        }
        if (!Utils.equals(goldModel.getProjectVersion(), testModel.getProjectVersion())) {
            return new DomainModelComparisonResult("Project versions did not match: expected "
                + goldModel.getProjectVersion() + " but was " + testModel.getProjectVersion(), false);
        }
        
        // start comparing classes
        UMLClass[] goldClasses = goldModel.getExposedUMLClassCollection().getUMLClass();
        UMLClass[] testClasses = testModel.getExposedUMLClassCollection().getUMLClass();
        return compareClasses(goldClasses, testClasses);
    }
    
    
    private static DomainModelComparisonResult compareClasses(UMLClass[] goldClassses, UMLClass[] testClasses) {
        if (goldClassses.length != testClasses.length) {
            return new DomainModelComparisonResult("Mismatched number of classes: expected " 
                + goldClassses.length + " but was " + testClasses.length, false);
        }
        // put classes into lists and sort
        List<UMLClass> goldList = Arrays.asList(goldClassses);
        List<UMLClass> testList = Arrays.asList(testClasses);
        Comparator<UMLClass> classComparator = new Comparator<UMLClass>() {
            public int compare(UMLClass c1, UMLClass c2) {
                String s1 = getClassString(c1);
                String s2 = getClassString(c2);
                return s1.compareTo(s2);
            }
        };
        Collections.sort(goldList, classComparator);
        Collections.sort(testList, classComparator);
        for (int i = 0; i < goldList.size(); i++) {
            UMLClass goldClass = goldList.get(i);
            UMLClass testClass = testList.get(i);
            String goldString = getClassString(goldClass);
            String testString = getClassString(testClass);
            if (!Utils.equals(goldString, testString)) {
                return new DomainModelComparisonResult(
                    "Class " + goldString + " found in gold model but not test model", false);
            }
            // compare class's semantic metadata
            SemanticMetadata[] goldMetadata = goldClass.getSemanticMetadata();
            SemanticMetadata[] testMetadata = testClass.getSemanticMetadata();
            if (goldMetadata != null && testMetadata == null) {
                return new DomainModelComparisonResult("Gold class " + goldString + " has semantic metadata but test class does not", false);
            } else if (goldMetadata == null && testMetadata != null) {
                return new DomainModelComparisonResult("Gold class " + goldString + " has no semantic metadata, but test class does", false);
            } else if (goldMetadata != null && testMetadata != null) {
                DomainModelComparisonResult result = compareSemanticMetadata(goldString, goldMetadata, testMetadata);
                if (!result.modelsAreEqual()) {
                    return result;
                }
            }
            // compare attributes
            UMLAttribute[] goldAttributes = goldClass.getUmlAttributeCollection().getUMLAttribute();
            UMLAttribute[] testAttributes = testClass.getUmlAttributeCollection().getUMLAttribute();
            if (goldAttributes != null && testAttributes == null) {
                return new DomainModelComparisonResult("Gold class " + goldString + " has attributes but test class does not", false);
            } else if (goldAttributes == null && testAttributes != null) {
                return new DomainModelComparisonResult("Gold class " + goldString + " has no attributes but test class does", false);
            } else if (goldAttributes != null && testAttributes != null) {
                DomainModelComparisonResult result = compareAttributes(goldString, goldAttributes, testAttributes);
                if (!result.modelsAreEqual()) {
                    return result;
                }
            }
        }
        return new DomainModelComparisonResult("", true);
    }
    
    
    private static DomainModelComparisonResult compareSemanticMetadata(String className, SemanticMetadata[] goldMetadata, SemanticMetadata[] testMetadata) {
        if (goldMetadata.length != testMetadata.length) {
            return new DomainModelComparisonResult(
                "Mismatched number of semantic metadata for class " + className 
                + ": expected " + goldMetadata.length + " but found " + testMetadata.length, false);
        }
        // turn into lists and sort metadata
        List<SemanticMetadata> goldList = Arrays.asList(goldMetadata);
        List<SemanticMetadata> testList = Arrays.asList(testMetadata);
        Comparator<SemanticMetadata> semanticComparator = new Comparator<SemanticMetadata>() {
            public int compare(SemanticMetadata md1, SemanticMetadata md2) {
                String s1 = getMetadataString(md1);
                String s2 = getMetadataString(md2);
                return s1.compareTo(s2);
            }
        };
        Collections.sort(goldList, semanticComparator);
        Collections.sort(testList, semanticComparator);
        for (int i = 0; i < goldList.size(); i++) {
            SemanticMetadata goldMd = goldList.get(i);
            SemanticMetadata testMd = testList.get(i);
            String goldString = getMetadataString(goldMd);
            String testString = getMetadataString(testMd);
            if (!Utils.equals(goldString, testString)) {
                return new DomainModelComparisonResult("Gold class " + className + " semantic metadata does not match test class semantic metadata", false);
            }
        }
        return new DomainModelComparisonResult("", true);
    }
    
    
    private static DomainModelComparisonResult compareAttributes(String className, UMLAttribute[] goldAttributes, UMLAttribute[] testAttributes) {
        if (goldAttributes.length != testAttributes.length) {
            return new DomainModelComparisonResult("Mismatched number of attributes for class " + className
                + ": expected " + goldAttributes.length + " but found " + testAttributes.length, false);
        }
        // list and sort
        List<UMLAttribute> goldList = Arrays.asList(goldAttributes);
        List<UMLAttribute> testList = Arrays.asList(testAttributes);
        Comparator<UMLAttribute> attributeComparator = new Comparator<UMLAttribute>() {
            public int compare(UMLAttribute a1, UMLAttribute a2) {
                String s1 = getAttributeString(a1);
                String s2 = getAttributeString(a2);
                return s1.compareTo(s2);
            }
        };
        Collections.sort(goldList, attributeComparator);
        Collections.sort(testList, attributeComparator);
        for (int i = 0; i < goldList.size(); i++) {
            UMLAttribute goldAtt = goldList.get(i);
            UMLAttribute testAtt = testList.get(i);
            String goldString = getAttributeString(goldAtt);
            String testString = getAttributeString(testAtt);
            if (!Utils.equals(goldString, testString)) {
                return new DomainModelComparisonResult("Gold attribute " + className + "." + goldAtt.getName() 
                    + " did not match test attribute", false);
            }
        }
        return new DomainModelComparisonResult("", true);
    }
    
    
    private static String getClassString(UMLClass c) {
        return "" + c.getPackageName() + "." + c.getClassName();
    }
    
    
    private static String getMetadataString(SemanticMetadata md) {
        return "" + md.getConceptCode() + ":" + md.getConceptDefinition() 
            + ":" + md.getConceptName() + ":" + md.getOrder() + ":" + md.getOrderLevel();
    }
    
    
    private static String getAttributeString(UMLAttribute att) {
        return "" + att.getName() + ":" + att.getDataTypeName() + ":" + att.getPublicID();
    }
}
