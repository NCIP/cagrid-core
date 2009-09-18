package gov.nih.nci.cagrid.common;

import java.io.InputStream;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.FileProvider;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * ConfigurableObjectDeserializer Object Deserializer that allows for
 * configuration by a wsdd file
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Jun 23, 2006
 * @version $Id$
 */
public class ConfigurableObjectDeserializer extends ObjectDeserializer {

    public static <T> T toObject(InputSource source, Class<T> javaClass, InputStream wsdd) throws SAXException {
        // create a message context for the wsdd
        EngineConfiguration engineConfig = new FileProvider(wsdd);
        AxisClient axisClient = new AxisClient(engineConfig);
        MessageContext messageContext = new MessageContext(axisClient);
        messageContext.setEncodingStyle("");
        messageContext.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);

        ConfigurableObjectDeserializationContext desContext = new ConfigurableObjectDeserializationContext(
            messageContext, source, javaClass);

        return javaClass.cast(desContext.getValue());
    }
}
