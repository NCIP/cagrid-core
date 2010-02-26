package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.data.client.DataServiceClient;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.cagrid.cql2.extensionsupport.SupportedExtensions;
import org.cagrid.dataservice.cql.support.Cql2SupportType;
import org.cagrid.dataservice.cql.support.QueryLanguageSupport;
import org.cagrid.dataservice.cql.support.QueryLanguageSupportCQL2Support;

public class CQL2SupportSerializationTest extends TestCase {
    
    public CQL2SupportSerializationTest(String name) {
        super(name);
    }
    
    
    private InputStream getWsdd() {
        return DataServiceClient.class.getResourceAsStream("client-config.wsdd");
    }
    
    
    private String serialize(QueryLanguageSupport support) {
        StringWriter writer = new StringWriter();
        try {
            InputStream wsdd = getWsdd();
            Utils.serializeObject(support, QueryLanguageSupport.getTypeDesc().getXmlType(), writer, wsdd);
            wsdd.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        return writer.getBuffer().toString();
    }
    
    
    private QueryLanguageSupport deserialize(String text) {
        QueryLanguageSupport support = null;
        try {
            InputStream wsdd = getWsdd();
            support = Utils.deserializeObject(new StringReader(text), QueryLanguageSupport.class, wsdd);
            wsdd.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        return support;
    }
    
    
    private void serializeAndCheck(QueryLanguageSupport expected) {
        String text = serialize(expected);
        QueryLanguageSupport des = deserialize(text);
        if (!expected.equals(des)) {
            try {
                System.err.println(XMLUtilities.formatXML(text));
            } catch (Exception ex) {
                // disregard that
            }
            fail("Expected query language support did not match deserialized!");
        }
    }
    
    
    public void testCql2SupportNoExtensions() {
        QueryLanguageSupport support = new QueryLanguageSupport();
        QueryLanguageSupportCQL2Support cql2Support = new QueryLanguageSupportCQL2Support();
        SupportedExtensions ext = new SupportedExtensions();
        cql2Support.setSupportedExtensions(ext);
        support.setCQL2Support(cql2Support);
        
        serializeAndCheck(support);
    }
    
    
    public void testCql2NotSupported() {
        QueryLanguageSupport support = new QueryLanguageSupport();
        support.setCQL2NotSupported(Cql2SupportType.ImplementationNotProvided);
        
        serializeAndCheck(support);
    }
    
    
    public void testCql2SupportWithExtensions() {
        QueryLanguageSupport support = new QueryLanguageSupport();
        QueryLanguageSupportCQL2Support cql2Support = new QueryLanguageSupportCQL2Support();
        SupportedExtensions ext = new SupportedExtensions();
        QName[] names = new QName[5];
        for (int i = 0; i < 5; i++) {
            names[i] = new QName("http://uri" + i, "" + i);
        }
        ext.setAttributeExtension(names);
        cql2Support.setSupportedExtensions(ext);
        support.setCQL2Support(cql2Support);
        
        serializeAndCheck(support);
    }
    

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
