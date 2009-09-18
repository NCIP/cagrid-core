package gov.nih.nci.cagrid.workflow.service;


import gov.nih.nci.cagrid.workflow.stubs.types.WSDLReferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PDDGenerator {
	public static final String XMLNS_PDD = "http://schemas.active-endpoints.com/pdd/2005/09/pdd.xsd";

	public static final String XMLNS_BPEL = "http://schemas.xmlsoap.org/ws/2003/03/business-process/";

	public static final String XMLNS_WSDL = "http://schemas.xmlsoap.org/wsdl/";

	public static final String XMLNS_WSA = "http://schemas.xmlsoap.org/ws/2004/03/addressing";

	public static Document generatePDD(String workflowName, Document bpelDoc, 
			String serviceName, WSDLReferences[] wsdlRefArray) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document pdd = db.newDocument();
		Element root = pdd.createElementNS(XMLNS_PDD, "process");
		QName processQ = getBpelProcessName(bpelDoc);
		root.setAttribute("name", "bpelns:" + processQ.getLocalPart());
		root.setAttribute("xmlns", XMLNS_PDD);
		root.setAttribute("xmlns:bpelns", processQ.getNamespaceURI());
		root.setAttribute("location", workflowName + ".bpel");
		root.setAttribute("xmlns:wsa", XMLNS_WSA);
		pdd.appendChild(root);
		// Add partnerLinks
		parsePartnerLinks(root, bpelDoc, pdd, serviceName, wsdlRefArray);
		return pdd;
	}

	public static QName getBpelProcessName(Document bpelDoc) {
		Element root = bpelDoc.getDocumentElement();
		String processName = root.getAttribute("name");
		String processNS = root.getAttribute("targetNamespace");
		return new QName(processNS, processName);
	}

	public static void appendPartnerLinks(Document doc, Node root,
			Collection partnerLinks, String serviceName, Map wsdlRefs) {
		Element plinksNode = doc.createElementNS(XMLNS_PDD, "partnerLinks");
		root.appendChild(plinksNode);
		for (Iterator it = partnerLinks.iterator(); it.hasNext();) {
			PartnerLink plink = (PartnerLink) it.next();
			Element pNode = doc.createElementNS(XMLNS_PDD, "partnerLink");
			plinksNode.appendChild(pNode);
			pNode.setAttribute("name", plink.name);
			if (!"".equals(plink.partnerRole)) {
				Element pRoleNode = doc.createElementNS(XMLNS_PDD,
						"partnerRole");
				pRoleNode.setAttribute("endpointReference", "static");
				pRoleNode.setAttribute("invokeHandler", "default:Address");
				Element epRefNode = doc.createElement("wsa:EndpointReference");
				epRefNode.setAttribute("xmlns:wsa", XMLNS_WSA);
				Element epAddrNode = doc.createElement("wsa:Address");
				QName plinkType = plink.getLinkType();
				String nsuri = plinkType.getNamespaceURI();
				System.out.println("nsuri: " + nsuri);
				WSDLReferences ref = (WSDLReferences)wsdlRefs.get(nsuri);
				pNode.appendChild(pRoleNode);
				pRoleNode.appendChild(epRefNode);
				epRefNode.appendChild(epAddrNode);
				epAddrNode.appendChild(doc.createTextNode(ref.getServiceUrl().toString()));
			}
			if (!"".equals(plink.myRole)) {
				Element myRoleNode = doc.createElementNS(XMLNS_PDD, "myRole");
				myRoleNode.setAttribute("service",  serviceName);
				myRoleNode.setAttribute("allowedRoles", "");
				myRoleNode.setAttribute("binding", "MSG");
				pNode.appendChild(myRoleNode);
			} else {
				//TODO: Throw appropriate exceptions
				System.err
						.println("PartnerLink "
								+ plink.name
								+ " should have either myRole or partnerRole attribute!");
				continue;
			}
		}
	}

	private static void appendWsdlReferences(Document doc, Node root,
			Map wsdlRefs) {
		Element wsdlRefsNode = doc.createElementNS(XMLNS_PDD, "wsdlReferences");
		root.appendChild(wsdlRefsNode);
		for (Iterator it = wsdlRefs.keySet().iterator(); it.hasNext();) {
			String ns = (String) it.next();
			Element wsdlRefNode = doc.createElementNS(XMLNS_PDD, "wsdl");
			wsdlRefNode.setAttribute("namespace", ns);
			WSDLReferences ref = (WSDLReferences)wsdlRefs.get(ns);
			String location = ref.getWsdlLocation();
			wsdlRefNode
					.setAttribute(
							"location",location);
			wsdlRefsNode.appendChild(wsdlRefNode);
		}
	}

	private static HashMap mapWsdlRefs(WSDLReferences[] wsdlRefs) {
		HashMap map = new HashMap();
		for(int i=0;i<wsdlRefs.length;i++){
			String qName = wsdlRefs[i].getWsdlNamespace().toString();
			map.put(qName, wsdlRefs[i]);
		}
		return map;
	}
	private static void  parsePartnerLinks(Node root, Document bpelDoc,
			Document pdd, String serviceName, WSDLReferences wsdlRefs[]) {
		NodeList partnerLinksNL = bpelDoc.getElementsByTagNameNS(XMLNS_BPEL,
				"partnerLink");
		ArrayList partnerLinks = new ArrayList();
		Collection wsdlNamespaces = new HashSet();
		HashMap map = mapWsdlRefs(wsdlRefs);
		int len = partnerLinksNL.getLength();
		for (int i = 0; i < len; i++) {
			PartnerLink pl = new PartnerLink();
			Element el = (Element) partnerLinksNL.item(i);
			pl.myRole = el.getAttribute("myRole");
			pl.partnerRole = el.getAttribute("partnerRole");
			pl.name = el.getAttribute("name");
			String plt = el.getAttribute("partnerLinkType");
			StringTokenizer st = new StringTokenizer(plt, ":", false);
			String prefix = st.nextToken();
			String local = st.nextToken();
			String nsuri = resolveNS(el, prefix);
			if (null == nsuri) {
				System.err
						.println("Unable to resolve the partnerLinkType namespace for "
								+ prefix);
				continue;
			}
			pl.linkType = new QName(nsuri, local, prefix);
			wsdlNamespaces.add(nsuri);
			partnerLinks.add(pl);
		}
		appendPartnerLinks(pdd, root, partnerLinks, serviceName, map);
		appendWsdlReferences(pdd, root, map);
	}

	private static String resolveNS(final Node start, final String prefix) {
		if (null == prefix) {
			throw new IllegalArgumentException("Starting Node is required");
		}
		if (null == start) {
			return null;
		}
		Element el;
		try {
			el = (Element) start;
		} catch (ClassCastException e) {
			return null;
		}
		String nsuri;
		if ("".equals(prefix)) {
			nsuri = el.getAttribute("xmlns");
		} else {
			nsuri = el.getAttribute("xmlns:" + prefix);
		}
		if ("".equals(nsuri)) {
			return resolveNS(start.getParentNode(), prefix);
		}
		return nsuri;
	}

	public static String createPDD(String workflowName, String bpelFile, 
			String serviceName, WSDLReferences[] wsdlRefArray) throws Exception {
		File f = new File(bpelFile);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw e;
		}
		Document bpelDoc = db.parse(f);

		Document pdd = PDDGenerator.generatePDD(workflowName, 
				bpelDoc, serviceName, wsdlRefArray );
		File pddFile = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + workflowName + ".pdd");
		Writer writer = new BufferedWriter(new FileWriter(pddFile));
		XMLUtils.PrettyDocumentToWriter(pdd, writer);
		writer.close();
		return pddFile.getAbsolutePath();

	}

}
