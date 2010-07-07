package org.cagrid.dcql.utilities.encoding;

import java.net.URL;

import org.cagrid.cql.utilities.encoding.Cql2SerialzationHelper;
import org.castor.mapping.BindingType;
import org.castor.mapping.MappingUnmarshaller;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.ClassDescriptorResolverFactory;
import org.exolab.castor.xml.XMLClassDescriptorResolver;

public class Dcql2SerialzationHelper {

    public static final String MAPPING_LOCATION = "/org/cagrid/data/dcql/mapping/dcql2-castor-mapping.xml";
    
    private static XMLClassDescriptorResolver xmlDescriptorResovler = null;
    
    static synchronized URL getMappingURL() {
        return Dcql2SerialzationHelper.class.getResource(MAPPING_LOCATION);
    }
    
    
    private static Mapping getMapping() throws Exception {
        Mapping map = new Mapping();
        map.setEntityResolver(Cql2SerialzationHelper.getDtdResolver());
        map.loadMapping(getMappingURL());
        return map;
    }

    
    public static synchronized XMLClassDescriptorResolver getResolver() throws Exception {
        if (xmlDescriptorResovler == null) {
            xmlDescriptorResovler = (XMLClassDescriptorResolver) ClassDescriptorResolverFactory.createClassDescriptorResolver(BindingType.XML);
            MappingUnmarshaller mappingUnmarshaller = new MappingUnmarshaller();
            MappingLoader mappingLoader = mappingUnmarshaller.getMappingLoader(getMapping(), BindingType.XML);
            xmlDescriptorResovler.setMappingLoader(mappingLoader);
        }
        return xmlDescriptorResovler;
    }
}
