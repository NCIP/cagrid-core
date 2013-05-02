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
package gov.nih.nci.cagrid.data.cql.validation;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

/** 
 *  CqlDomainValidator
 *  Validates CQL against a domain model for validity
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 1, 2006 
 * @version $Id$ 
 */
public interface CqlDomainValidator {

	public void validateDomainModel(CQLQuery query, DomainModel model) throws MalformedQueryException;
}
