package org.cagrid.iso21090.sdkquery.translator.cql2;

import java.util.List;

import org.cagrid.iso21090.sdkquery.translator.TypesInformationException;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationResolver;

public interface Cql2TypesInformationResolver extends TypesInformationResolver {

    public List<ClassAssociation> getAssociationsFromClass(String parentClassname) throws TypesInformationException;
}
