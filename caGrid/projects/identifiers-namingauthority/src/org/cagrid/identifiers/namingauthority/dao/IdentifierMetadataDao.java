package org.cagrid.identifiers.namingauthority.dao;

import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;


public class IdentifierMetadataDao extends AbstractDao<IdentifierMetadata> {

    @Override
    public Class<IdentifierMetadata> domainClass() {
        return IdentifierMetadata.class;
    }

    // public IdentifierValuesImpl getValues( String identifier ) {
    //        
    // Session session = dbFactory.getCurrentSession();
    // session.beginTransaction();
    // List<IdentifierValue> values = session.
    // createQuery("from IdentifierValue as iv where iv.name = :name").
    // setParameter("name", identifier).list();
    // session.getTransaction().commit();
    //        
    // if (values.size() == 0)
    // return null;
    //        
    // IdentifierValuesImpl ivs = new IdentifierValuesImpl();
    // for( int i=0; i < values.size(); i++ ) {
    // ivs.add(values.get(i).getType(), values.get(i).getData());
    // }
    //        
    // return ivs;
    // }

}
