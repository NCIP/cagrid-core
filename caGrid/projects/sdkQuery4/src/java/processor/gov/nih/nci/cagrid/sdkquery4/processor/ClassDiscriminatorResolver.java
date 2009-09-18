package gov.nih.nci.cagrid.sdkquery4.processor;

/**
 * ClassDiscriminatorResolver
 * Used to determine the class discriminator value type and actual instance
 * which should be used when identifying a specific class within a hierarchy
 * for use with an HQL query and the special .class attribute
 * 
 * @author David
 *
 */
public interface ClassDiscriminatorResolver {

    
    public Object getClassDiscriminatorValue(String classname) throws Exception;
}
