package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.dao.IdentifierMetadataDao;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValueKey;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;


public class NamingAuthorityImpl extends NamingAuthority {

    private IdentifierMetadataDao identifierDao = null;


    public void initialize() {
        super.initialize();
    }


    @Override
    public URI createIdentifier(IdentifierValues ivalues) throws Exception {

        // TODO: I think we should allow this so you can request "placeholder"
        // identifiers, and update them later
        // if (ivalues == null)
        // throw new Exception("Input IdentifierValues can't be null");

        URI identifier = generateIdentifier();

        IdentifierMetadata md = new IdentifierMetadata();
        md.setLocalIdentifier(identifier);
        List<IdentifierValueKey> values = new ArrayList<IdentifierValueKey>();
        md.setValues(values);

        if (ivalues != null) {
            String[] keys = ivalues.getTypes();
            for (String key : keys) {
                IdentifierValueKey vk = new IdentifierValueKey();
                vk.setKey(key);
                String[] data = ivalues.getValues(key);
                vk.setValues(Arrays.asList(data));
                values.add(vk);
            }
        }

        this.identifierDao.save(md);

        return IdentifierUtil.build(getConfiguration().getPrefix(), md.getLocalIdentifier());

    }


    @Override
    public IdentifierValues resolveIdentifier(URI identifier) throws InvalidIdentifierException {
        URI localURI = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);

        IdentifierMetadata template = new IdentifierMetadata();
        template.setLocalIdentifier(localURI);

        IdentifierMetadata md = this.identifierDao.getByExample(template);
        if (md == null) {
            throw new InvalidIdentifierException("The specified identifier (" + identifier + ") was not found.");
        }

        IdentifierValues result = null;
        if (md.getValues() != null && md.getValues().size() > 0) {
            result = new IdentifierValues();
            Map<String, List<String>> values = new HashMap<String, List<String>>();
            result.setValues(values);

            for (IdentifierValueKey vk : md.getValues()) {
                values.put(vk.getKey(), vk.getValues());
            }
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
