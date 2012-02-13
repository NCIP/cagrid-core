package gov.nih.nci.cagrid.introduce.codegen.serializers;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.encoding.SerializationContext;


/**
 * Reads client-config.wsdd and server-config.wsdd, and adds any typemappings
 * that are required.
 * 
 * &lt;typeMapping qname=&quot;(from Namespace namespace, and SchemaElement
 * type)&quot; languageSpecificType=&quot;java:(from Namespace
 * packageName+.+SchemaElement className)&quot; serializer=&quot;(from
 * SchemaElement serializer)&quot; deserializer=&quot;(from SchemaElement
 * deserializer)&quot; encodingStyle=&quot;&quot;/&gt;
 * 
 * editors will look for: &lt;!-- START INTRODUCE TYPEMAPPINGS --&gt;
 * 
 * if it exists, it dump the type mappings from its start, replacing evertying
 * in the file until finding: &lt;!-- END INTRODUCE TYPEMAPPINGS --&gt;
 * 
 * if the tag isn't found, the tags (and type mappings) will be added to the end
 * of the file (in appropriate location for server and client)
 * 
 * 
 * @author oster
 */
public class SyncSerialization extends SyncTool {
	public static final String WSDD_END_TAG = "</deployment>";

	public static final String MAPPING_HEADER = "<!-- START INTRODUCE TYPEMAPPINGS -->";

	public static final String MAPPING_FOOTER = "<!-- END INTRODUCE TYPEMAPPINGS -->";


	public SyncSerialization(File baseDirectory, ServiceInformation info) {
		super(baseDirectory, info);

	}


	/**
	 * @throws SynchronizationException
	 *             if unable to locate and write to client-config.wsdd and
	 *             server-config.wsdd
	 */
	public void sync() throws SynchronizationException {
		File clientWSDD;
		File serverWSDD;

		List mappingList = buildTypeMappings(getServiceInformation().getNamespaces().getNamespace());
		String replacement = "";
		if (mappingList.size() > 0) {
			StringBuffer mappingReplacement = new StringBuffer();
			Iterator iter = mappingList.iterator();
			while (iter.hasNext()) {
				WSDDTypeMapping mapping = (WSDDTypeMapping) iter.next();
				mappingReplacement.append(mappingToString(mapping) + "\n");
			}
			replacement = mappingReplacement.toString();
		}

		serverWSDD = new File(getBaseDirectory() + File.separator + "server-config.wsdd");

		if (!(serverWSDD.exists() && serverWSDD.canRead() && serverWSDD.canWrite())) {
			throw new SynchronizationException("Unable to locate or write to service wsdd files: " + serverWSDD);
		}

		editFile(serverWSDD, replacement);

		if (getServiceInformation().getServices() != null && getServiceInformation().getServices().getService() != null) {
			for (int i = 0; i < getServiceInformation().getServices().getService().length; i++) {
				ServiceType service = getServiceInformation().getServices().getService(i);

				clientWSDD = new File(getBaseDirectory().getAbsolutePath() + File.separator + "src" + File.separator
					+ CommonTools.getPackageDir(service) + File.separator + "client" + File.separator
					+ "client-config.wsdd");
				if (!(clientWSDD.exists() && clientWSDD.canRead() && clientWSDD.canWrite())) {
					throw new SynchronizationException("Unable to locate or write to client wsdd files: " + clientWSDD);
				}
				editFile(clientWSDD, replacement);
			}
		}
	}


	public static void editFile(File wsddFile, String replacement) throws SynchronizationException {
		StringBuffer contents = null;
		try {
			contents = Utils.fileToStringBuffer(wsddFile);
		} catch (Exception e) {
			throw new SynchronizationException("Unable to load file [" + wsddFile + "] contents:" + e.getMessage(), e);
		}
		// find where to replace, by looking for header
		int startInd = contents.indexOf(MAPPING_HEADER);
		int endInd = -1;
		if (startInd < 0) {
			// header isnt found, so write at end of file (don't even look for
			// footer)
			startInd = contents.indexOf(WSDD_END_TAG);
			endInd = startInd;
		} else {
			endInd = contents.indexOf(MAPPING_FOOTER);
			if (endInd < startInd && endInd > 0) {
				throw new SynchronizationException("Malformed wsdd file:" + wsddFile);
			} else if (endInd < 0) {
				// footer wasnt found, so write directly after end of start
				endInd = startInd + MAPPING_HEADER.length();
			} else {
				endInd += MAPPING_FOOTER.length();
			}
		}

		if (startInd < 0 || endInd < 0) {
			throw new SynchronizationException("Unable to parse file:" + wsddFile);
		}
		String newConents = "";
		if (Utils.clean(replacement) == null) {
			// clear out anything that was there
			newConents = contents.substring(0, startInd) + contents.substring(endInd);
		} else {
			// replace what was there with the new replacement
			newConents = contents.substring(0, startInd) + MAPPING_HEADER + "\n" + replacement + "\n" + MAPPING_FOOTER
				+ contents.substring(endInd);
		}

		FileWriter fw;
		try {
			fw = new FileWriter(wsddFile);
			fw.write(newConents);
			fw.close();
		} catch (IOException e) {
			throw new SynchronizationException("Problem rewriting file:" + e.getMessage(), e);
		}

	}


	public static List buildTypeMappings(NamespaceType[] namespaces) throws SynchronizationException {
		List mappings = new ArrayList();

		if (namespaces != null) {
			for (int i = 0; i < namespaces.length; i++) {
				NamespaceType ns = namespaces[i];
				SchemaElementType[] schemaElements = ns.getSchemaElement();
				if (schemaElements == null) {
					continue;
				}
				for (int j = 0; j < schemaElements.length; j++) {
					SchemaElementType typeDesc = schemaElements[j];
					if (typeContainsAllAttributes(typeDesc)) {
						WSDDTypeMapping mapping = new WSDDTypeMapping();
						mapping.setDeserializer(typeDesc.getDeserializer());
						mapping.setSerializer(typeDesc.getSerializer());
						mapping.setEncodingStyle("");
						mapping.setLanguageSpecificType(ns.getPackageName() + "." + typeDesc.getClassName());
						mapping.setQName(new QName(ns.getNamespace(), typeDesc.getType()));
						mappings.add(mapping);
					}
				}
			}
		}

		return mappings;
	}


	public static String mappingToString(WSDDTypeMapping mapping) throws SynchronizationException {
		StringWriter writer = new StringWriter();
		SerializationContext context = new SerializationContext(writer, null);
		context.setPretty(true);
		context.setSendDecl(false);
		try {
			mapping.writeToContext(context);
			writer.close();
		} catch (Exception e) {
			throw new SynchronizationException("Error writting type mappings out:" + e.getMessage(), e);
		}

		return writer.getBuffer().toString();
	}


	public static boolean typeContainsAllAttributes(SchemaElementType type) throws SynchronizationException {
		String ser = Utils.clean(type.getSerializer());
		String deser = Utils.clean(type.getDeserializer());

		if (ser != null && deser != null) {
			return true;
		} else if (ser != null || deser != null) {
			throw new SynchronizationException("Invalid SchemaElement[" + type.getType()
				+ "]! Must specify either ALL of  serializer[" + ser + "], deserializer[" + deser
				+ "], or NONE of them.");
		}

		return false;
	}
}
