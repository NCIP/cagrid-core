package org.cagrid.data.sdkquery44.translator.cql2;

import java.util.List;

import org.cagrid.data.sdkquery44.translator.TypesInformationException;
import org.cagrid.data.sdkquery44.translator.TypesInformationResolver;

public interface Cql2TypesInformationResolver extends TypesInformationResolver {

    public List<ClassAssociation> getAssociationsFromClass(String parentClassname) throws TypesInformationException;
}
