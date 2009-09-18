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
