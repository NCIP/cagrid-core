package gov.nih.nci.cagrid.introduce.codegen.services.security.tools;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.security.CommunicationMethod;
import gov.nih.nci.cagrid.introduce.beans.security.MethodAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.MethodSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.ProxyCredential;
import gov.nih.nci.cagrid.introduce.beans.security.RunAsMode;
import gov.nih.nci.cagrid.introduce.beans.security.SecureConversation;
import gov.nih.nci.cagrid.introduce.beans.security.SecureMessage;
import gov.nih.nci.cagrid.introduce.beans.security.SecuritySetting;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceCredential;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.TransportLevelSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.X509Credential;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SecurityDescriptorGenerator {
	public static String getSecurityDescriptor(SpecificServiceInformation info) {
		try {
			StringBuffer xml = new StringBuffer();
			xml.append("<securityConfig xmlns=\"http://www.globus.org\">");

			// Sloppy Jalopy, need to have this accomplished when adding as
			// provider
			xml.append("<method name=\"getServiceSecurityMetadata\">\n");
			xml.append("    <auth-method>\n");
			xml.append("      <none/>\n");
			xml.append("    </auth-method>\n");
			xml.append("</method>\n");

			xml.append(writeServiceSettings(info.getService()));

			ServiceType service = info.getService();
			MethodsType methods = service.getMethods();
			if (methods != null) {
				MethodType[] method = methods.getMethod();
				if (method != null) {
					for (int i = 0; i < method.length; i++) {
						if (!method[i].getName().equals("getServiceSecurityMetadata")) {
							xml.append(writeMethodSettings(info.getService().getServiceSecurity(), method[i]));
						}
					}
				}
			}
			xml.append("</securityConfig>");
			try {
				return XMLUtilities.formatXML(xml.toString());
			} catch (Exception e) {
				return xml.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}


	private static String writeServiceSettings(ServiceType service) throws Exception {
		ServiceSecurity ss = service.getServiceSecurity();
		StringBuffer xml = new StringBuffer();
		boolean usedServiceAuth = false;
		boolean needMethodAuth = false;
		if (ss != null) {

			if (ss.getServiceCredentials() != null) {
				ServiceCredential cred = ss.getServiceCredentials();

				if (cred.getX509Credential() != null) {
					X509Credential x509 = cred.getX509Credential();
					if ((x509.getCertificateLocation() != null) && (x509.getPrivateKeyLocation() != null)) {
						xml.append("<credential>");
						xml.append("<key-file value=\"" + x509.getPrivateKeyLocation() + "\"/>");
						xml.append("<cert-file value=\"" + x509.getCertificateLocation() + "\"/>");
						xml.append("</credential>");
					}
				} else if (cred.getProxyCredential() != null) {
					ProxyCredential proxy = cred.getProxyCredential();
					if (proxy.getProxyLocation() != null) {
						xml.append("<proxy-file value=\"" + proxy.getProxyLocation() + "\"/>");
					}
				}

			}

			ServiceAuthorization auth = ss.getServiceAuthorization();
			if (auth != null) {
				if (auth.getGridMapAuthorization() != null) {
					xml.append("<authz value=\"gridmap\"/>");
					xml.append("<gridmap value=\"" + auth.getGridMapAuthorization().getGridMapFileLocation() + "\"/>");
					usedServiceAuth = true;
				} else if (auth.getCustomPDPChainAuthorization() !=null) {
				    xml.append("<authz value=\"" + auth.getCustomPDPChainAuthorization().getPDPChain() + "\" />");
				} else if (auth.getNoAuthorization()==null){
					xml.append("<authz value=\"" + service.getName().toLowerCase() + ":" + service.getPackageName()
						+ ".service.globus." + service.getName() + "Authorization" + "\" />");
					usedServiceAuth = true;
				} else {
				    //dont write an authz method
				}
			}
			if ((ss.getSecuritySetting() != null) && (ss.getSecuritySetting().equals(SecuritySetting.Custom))) {
				xml.append("<auth-method>");
				xml.append(getSecureConversationSettings(ss.getSecureConversation()));
				xml.append(getSecureMessageSettings(ss.getSecureMessage()));
				xml.append(getTransportLayerSecuritySettings(ss.getTransportLevelSecurity()));
				xml.append("</auth-method>");
				xml.append(getRunAsMode(ss.getRunAsMode()));
			} else {
				xml.append("<auth-method>");
				xml.append("<none/>");
				xml.append("</auth-method>");
			}

		} else {
			xml.append("<auth-method>");
			xml.append("<none/>");
			xml.append("</auth-method>");
		}

		if ((service.getMethods() != null) && (service.getMethods().getMethod() != null)) {
			for (int i = 0; i < service.getMethods().getMethod().length; i++) {
				MethodType method = service.getMethods().getMethod(i);
				if ((method.getMethodSecurity() != null)
					&& (method.getMethodSecurity().getMethodAuthorization() != null)) {
					MethodAuthorization methAuth = method.getMethodSecurity().getMethodAuthorization();
					if ((methAuth.getIntroducePDPAuthorization()!=null)) {
						needMethodAuth = true;
					}
				}
			}
		}

		if (!usedServiceAuth && needMethodAuth) {
			xml.append("<authz value=\"" + service.getName().toLowerCase() + ":" + service.getPackageName()
				+ ".service.globus." + service.getName() + "Authorization" + "\" />");
		} else if (!usedServiceAuth && !needMethodAuth) {
			xml.append("<authz value=\"none\" />");
		}

		return xml.toString();

	}


	private static String writeMethodSettings(ServiceSecurity service, MethodType method) throws Exception {
		try {
			MethodSecurity ms = method.getMethodSecurity();

			StringBuffer xml = new StringBuffer();
			if (determineWriteMethod(service, ms)) {
				xml.append("<method name=\"" + CommonTools.lowerCaseFirstCharacter(method.getName()) + "\">");
				if ((ms.getSecuritySetting() != null) && (ms.getSecuritySetting().equals(SecuritySetting.Custom))) {
					xml.append("<auth-method>");
					xml.append(getSecureConversationSettings(ms.getSecureConversation()));
					xml.append(getSecureMessageSettings(ms.getSecureMessage()));
					xml.append(getTransportLayerSecuritySettings(ms.getTransportLevelSecurity()));
					xml.append("</auth-method>");
					xml.append(getRunAsMode(ms.getRunAsMode()));
				} else {
					xml.append("<auth-method>");
					xml.append("<none/>");
					xml.append("</auth-method>");
				}
				xml.append("</method>");
			}
			return xml.toString();
		} catch (Exception e) {
			throw new Exception("Error configuring the security descriptor for the method " + method.getName() + ": "
				+ e.getMessage());
		}
	}


	private static String getRunAsMode(RunAsMode mode) throws Exception {
		StringBuffer xml = new StringBuffer();
		if (mode != null) {
			xml.append("<run-as>");
			if (mode.equals(RunAsMode.System)) {
				xml.append("<system-identity/>");
			} else if (mode.equals(RunAsMode.Service)) {
				xml.append("<service-identity/>");
			} else if (mode.equals(RunAsMode.Resource)) {
				xml.append("<resource-identity/>");
			} else if (mode.equals(RunAsMode.Caller)) {
				xml.append("<caller-identity/>");
			} else {
				throw new Exception("Unsupported run as provided.");
			}
			xml.append("</run-as>");
		}
		return xml.toString();
	}


	private static String getSecureConversationSettings(SecureConversation comm) throws Exception {
		StringBuffer xml = new StringBuffer();
		if (comm != null) {
			xml.append("<GSISecureConversation>");
			xml.append(getProtectionLevel("Secure Conversation", comm.getCommunicationMethod()));
			xml.append("</GSISecureConversation>");
		}
		return xml.toString();
	}


	private static String getSecureMessageSettings(SecureMessage comm) throws Exception {
		StringBuffer xml = new StringBuffer();
		if (comm != null) {
			xml.append("<GSISecureMessage>");
			xml.append(getProtectionLevel("Secure Message", comm.getCommunicationMethod()));
			xml.append("</GSISecureMessage>");
		}
		return xml.toString();
	}


	private static String getTransportLayerSecuritySettings(TransportLevelSecurity comm) throws Exception {
		StringBuffer xml = new StringBuffer();
		if (comm != null) {
			xml.append("<GSITransport>");
			xml.append(getProtectionLevel("Transport Layer Security", comm.getCommunicationMethod()));
			xml.append("</GSITransport>");
		}
		return xml.toString();
	}


	private static String getProtectionLevel(String type, CommunicationMethod comm) throws Exception {
		StringBuffer xml = new StringBuffer();
		xml.append("<protection-level>");
		if (comm == null) {
			throw new Exception(type + " requires the specification of acceptable communication mechanism.");
		} else if (comm.equals(CommunicationMethod.Privacy)) {
			xml.append("<privacy/>");
		} else if (comm.equals(CommunicationMethod.Integrity)) {
			xml.append("<integrity/>");
		} else if (comm.equals(CommunicationMethod.Integrity_Or_Privacy)) {
			xml.append("<privacy/>");
			xml.append("<integrity/>");
		} else {
			throw new Exception(type + " requires the specification of acceptable communication mechanism.");
		}
		xml.append("</protection-level>");
		return xml.toString();
	}


	private static boolean determineWriteMethod(ServiceSecurity service, MethodSecurity method) {

		if (method == null) {
			return false;
		}

		if (service != null) {

			if (!objectEquals(service.getSecuritySetting(), method.getSecuritySetting())) {
				return true;
			}
			if (communicationDiffers(service, method)) {
				return true;
			}
			if (!objectEquals(service.getAnonymousClients(), method.getAnonymousClients())) {
				return true;
			}

			if (!objectEquals(service.getRunAsMode(), method.getRunAsMode())) {
				return true;
			}

			return false;

		} else {
			return true;
		}
	}


	private static boolean communicationDiffers(ServiceSecurity service, MethodSecurity method) {
		if (service != null) {
			if (!objectEquals(service.getTransportLevelSecurity(), method.getTransportLevelSecurity())) {
				return true;
			}
			if (!objectEquals(service.getSecureConversation(), method.getSecureConversation())) {
				return true;
			}
			if (!objectEquals(service.getSecureMessage(), method.getSecureMessage())) {
				return true;
			}
			return false;

		} else {
			return true;
		}
	}


	private static boolean objectEquals(Object o1, Object o2) {
		if ((o1 == null) && (o2 == null)) {
			return true;
		} else if ((o1 != null) && (o2 != null) && (o1.equals(o2))) {
			return true;
		} else {
			return false;
		}
	}
}
