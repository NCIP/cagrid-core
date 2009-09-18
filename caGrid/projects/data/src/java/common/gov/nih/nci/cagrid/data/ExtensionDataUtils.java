package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.axis.message.MessageElement;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

/** 
 *  ExtensionDataUtils
 *  Utilities for extension data management
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 14, 2006 
 * @version $Id$ 
 */
public class ExtensionDataUtils {
	
	public static Data getExtensionData(ExtensionTypeExtensionData data) throws Exception {
		MessageElement[] anys = data.get_any();
		MessageElement dataElement = null;
		for (int i = 0; anys != null && i < anys.length; i++) {
			if (anys[i].getQName().equals(Data.getTypeDesc().getXmlType())) {
				dataElement = anys[i];
				break;
			}
		}
		if (dataElement == null) {
            Data cleanData = new Data();
            dataElement = new MessageElement(Data.getTypeDesc().getXmlType(), cleanData);
			MessageElement[] newAnys = null;
			if (anys == null) {
				newAnys = new MessageElement[] {dataElement};
			} else {
				newAnys = new MessageElement[anys.length + 1];
				System.arraycopy(anys, 0, newAnys, 0, anys.length);
				newAnys[newAnys.length - 1] = dataElement;
			}
			data.set_any(newAnys);
		}
		StringWriter dataXml = new StringWriter();
		Utils.serializeObject(dataElement, dataElement.getQName(), dataXml);
		Data value = (Data) ObjectDeserializer.deserialize(
			new InputSource(new StringReader(dataXml.getBuffer().toString())), Data.class);
		return value;
	}
	
	
	public static void storeExtensionData(ExtensionTypeExtensionData extData, Data data) throws Exception {
		MessageElement element = new MessageElement(Data.getTypeDesc().getXmlType(), data);
		ExtensionTools.updateExtensionDataElement(extData, element);
	}
	
	
	public static String getQueryProcessorStubClassName(ServiceInformation info) {
		ServiceType mainService = CommonTools.getService(info.getServices(), 
			info.getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
		String basePackage = mainService.getPackageName();
		basePackage += ".stubs.cql";
		return basePackage + "." + DataServiceConstants.QUERY_PROCESSOR_STUB_NAME;		
	}
}
