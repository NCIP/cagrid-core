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
package gov.nih.nci.cagrid.data.cql2;

/**
 * Identifies the part of a CQL 2 query being extended
 * 
 * @author David
 */
public enum Cql2ExtensionPoint {

    OBJECT, ATTRIBUTE, MODIFIER, RESULT;
}
