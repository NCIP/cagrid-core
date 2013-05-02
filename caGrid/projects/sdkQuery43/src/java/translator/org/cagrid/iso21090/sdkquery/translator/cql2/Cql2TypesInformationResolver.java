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
package org.cagrid.iso21090.sdkquery.translator.cql2;

import java.util.List;

import org.cagrid.iso21090.sdkquery.translator.TypesInformationException;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationResolver;

public interface Cql2TypesInformationResolver extends TypesInformationResolver {

    public List<ClassAssociation> getAssociationsFromClass(String parentClassname) throws TypesInformationException;
}
