/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
