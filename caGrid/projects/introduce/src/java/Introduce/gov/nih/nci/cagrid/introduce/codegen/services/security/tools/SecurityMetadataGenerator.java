package gov.nih.nci.cagrid.introduce.codegen.services.security.tools;

import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.security.AnonymousCommunication;
import gov.nih.nci.cagrid.introduce.beans.security.CommunicationMethod;
import gov.nih.nci.cagrid.introduce.beans.security.MethodSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.SecureConversation;
import gov.nih.nci.cagrid.introduce.beans.security.SecureMessage;
import gov.nih.nci.cagrid.introduce.beans.security.SecuritySetting;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.TransportLevelSecurity;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.metadata.security.CommunicationMechanism;
import gov.nih.nci.cagrid.metadata.security.GSISecureConversation;
import gov.nih.nci.cagrid.metadata.security.GSISecureMessage;
import gov.nih.nci.cagrid.metadata.security.GSITransport;
import gov.nih.nci.cagrid.metadata.security.None;
import gov.nih.nci.cagrid.metadata.security.Operation;
import gov.nih.nci.cagrid.metadata.security.ProtectionLevelType;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadataOperations;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @created Jun 22, 2005 Exp $
 */
public class SecurityMetadataGenerator {
	public static ServiceSecurityMetadata getSecurityMetadata(SpecificServiceInformation info) throws Exception {
		ServiceSecurityMetadata metadata = new ServiceSecurityMetadata();
		CommunicationMechanism mechanism = new CommunicationMechanism();
		ServiceSecurity ss = info.getService().getServiceSecurity();

		if (ss == null) {
			mechanism.setAnonymousPermitted(true);
			mechanism.setNone(new None());
		} else {

			if ((ss.getAnonymousClients() != null) && (ss.getAnonymousClients().equals(AnonymousCommunication.No))) {
				mechanism.setAnonymousPermitted(false);
			} else {
				mechanism.setAnonymousPermitted(true);
			}

			if ((ss.getSecuritySetting() != null) && (ss.getSecuritySetting().equals(SecuritySetting.Custom))) {
				mechanism.setGSISecureConversation(getSecureConversation(ss.getSecureConversation()));
				mechanism.setGSISecureMessage(getSecureMessage(ss.getSecureMessage()));
				mechanism.setGSITransport(getTransportSecurity(ss.getTransportLevelSecurity()));
			} else {
				mechanism.setNone(new None());
			}
		}
		metadata.setDefaultCommunicationMechanism(mechanism);
		ServiceType service = info.getService();
		MethodsType methods = service.getMethods();
		if (methods != null) {
			MethodType[] method = methods.getMethod();
			if (method != null) {
				List operations = new ArrayList();
				for (int i = 0; i < method.length; i++) {
					Operation o = getOperation(info.getService().getServiceSecurity(), method[i]);
					if (o != null) {
						operations.add(o);
					}
				}

				Operation[] ops = null;
				if (operations.size() > 0) {
					ops = new Operation[operations.size()];
					ops = (Operation[]) operations.toArray(ops);

				}

				ServiceSecurityMetadataOperations ssmo = new ServiceSecurityMetadataOperations();
				ssmo.setOperation(ops);
				metadata.setOperations(ssmo);

			}
		} else {
			ServiceSecurityMetadataOperations ssmo = new ServiceSecurityMetadataOperations();
			ssmo.setOperation(null);
			metadata.setOperations(ssmo);
		}
		return metadata;
	}


	private static Operation getOperation(ServiceSecurity service, MethodType method) throws Exception {
		try {

			MethodSecurity ms = method.getMethodSecurity();
			if (determineWriteMethod(service, ms)) {
				Operation o = new Operation();
				o.setName(method.getName());
				CommunicationMechanism mechanism = new CommunicationMechanism();

				if ((ms.getAnonymousClients() != null) && (ms.getAnonymousClients().equals(AnonymousCommunication.No))) {
					mechanism.setAnonymousPermitted(false);
				} else {
					mechanism.setAnonymousPermitted(true);
				}

				if ((ms.getSecuritySetting() != null) && (ms.getSecuritySetting().equals(SecuritySetting.Custom))) {
					mechanism.setGSISecureConversation(getSecureConversation(ms.getSecureConversation()));
					mechanism.setGSISecureMessage(getSecureMessage(ms.getSecureMessage()));
					mechanism.setGSITransport(getTransportSecurity(ms.getTransportLevelSecurity()));
				} else {
					mechanism.setNone(new None());
				}
				o.setCommunicationMechanism(mechanism);
				return o;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new Exception("Error writing the security metadata for the method " + method.getName() + ": "
				+ e.getMessage());
		}
	}


	private static GSISecureMessage getSecureMessage(SecureMessage comm) throws Exception {
		if (comm != null) {
			GSISecureMessage gsi = new GSISecureMessage();
			gsi.setProtectionLevel(getProtectionLevel("Secure Message", comm.getCommunicationMethod()));
			return gsi;
		} else {
			return null;
		}
	}


	private static GSISecureConversation getSecureConversation(SecureConversation comm) throws Exception {
		if (comm != null) {
			GSISecureConversation gsi = new GSISecureConversation();
			gsi.setProtectionLevel(getProtectionLevel("Secure Conversation", comm.getCommunicationMethod()));
			return gsi;
		} else {
			return null;
		}
	}


	private static GSITransport getTransportSecurity(TransportLevelSecurity comm) throws Exception {
		if (comm != null) {
			GSITransport gsi = new GSITransport();
			gsi.setProtectionLevel(getProtectionLevel("Secure Transport", comm.getCommunicationMethod()));
			return gsi;
		} else {
			return null;
		}
	}


	private static ProtectionLevelType getProtectionLevel(String type, CommunicationMethod comm) throws Exception {
		if (comm == null) {
			throw new Exception(type + " requires the specification of acceptable communication mechanism.");
		} else if (comm.equals(CommunicationMethod.Privacy)) {
			return ProtectionLevelType.privacy;
		} else if (comm.equals(CommunicationMethod.Integrity)) {
			return ProtectionLevelType.integrity;
		} else if (comm.equals(CommunicationMethod.Integrity_Or_Privacy)) {
			return ProtectionLevelType.either;
		} else {
			throw new Exception(type + " requires the specification of acceptable communication mechanism.");
		}
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
