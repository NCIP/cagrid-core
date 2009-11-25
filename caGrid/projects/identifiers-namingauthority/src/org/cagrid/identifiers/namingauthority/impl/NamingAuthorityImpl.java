package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.dao.IdentifierMetadataDao;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;


public class NamingAuthorityImpl extends NamingAuthority {

    private IdentifierMetadataDao identifierDao = null;


    public void initialize() {
        super.initialize();
    }


    @Override
    public URI createIdentifier(IdentifierValues ivalues) throws Exception {

        URI identifier = generateIdentifier();
        
        this.identifierDao.saveIdentifierValues( identifier, ivalues );

        return IdentifierUtil.build(getConfiguration().getPrefix(), identifier);
    }


    @Override
    public IdentifierValues resolveIdentifier(URI identifier) throws InvalidIdentifierException {
  
        URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);

        IdentifierValues result = null;
        try {
        	result = this.identifierDao.getIdentifierValues( localIdentifier );
        } catch(InvalidIdentifierException e) {
        	throw new InvalidIdentifierException("The specified  identifier (" + identifier + ") was not found.");
        }

        return result;
    }


    public void setIdentifierDao(IdentifierMetadataDao identifierDao) {
        this.identifierDao = identifierDao;
    }


    public IdentifierMetadataDao getIdentifierDao() {
        return identifierDao;
    }
}
