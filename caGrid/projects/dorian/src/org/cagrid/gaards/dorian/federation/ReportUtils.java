package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.Utils;

import java.util.Arrays;


public class ReportUtils {

    public static String generateReport(TrustedIdP original, TrustedIdP updated) {
        StringBuffer sb = new StringBuffer();
        sb.append("The following changes were made to the Trusted IdP, " + original.getName() + " (" + original.getId()
            + "): \n");

        int count = 0;

        if ((Utils.clean(updated.getDisplayName()) != null)
            && (!updated.getDisplayName().equals(original.getDisplayName()))) {
            count = count + 1;
            sb.append(count + ". Display Name changed from " + original.getDisplayName() + " to "
                + updated.getDisplayName() + ".\n");
        }
        if ((Utils.clean(updated.getUserPolicyClass()) != null)
            && (!updated.getUserPolicyClass().equals(original.getUserPolicyClass()))) {
            count = count + 1;
            sb.append(count + ". User policy changed from " + original.getUserPolicyClass() + " to "
                + updated.getUserPolicyClass() + ".\n");
        }

        if ((updated.getStatus() != null) && (!updated.getStatus().equals(original.getStatus()))) {
            count = count + 1;
            sb.append(count + ". Status changed from " + original.getStatus().getValue() + " to "
                + updated.getStatus().getValue() + ".\n");
        }

        if ((Utils.clean(updated.getIdPCertificate()) != null)
            && (!updated.getIdPCertificate().equals(original.getIdPCertificate()))) {
            count = count + 1;
            sb.append(count + ". Signing certificate changed.\n");
        }

        if (updated.getAuthenticationServiceURL() == null) {
            updated.setAuthenticationServiceURL("");
        }
        if (!updated.getAuthenticationServiceURL().equals(original.getAuthenticationServiceURL())) {
            count = count + 1;
            sb.append(count + ". Authentication Service URL changed from " + original.getAuthenticationServiceURL()
                + " to " + updated.getAuthenticationServiceURL() + ".\n");
        }

        if (updated.getAuthenticationServiceIdentity() == null) {
            updated.setAuthenticationServiceIdentity("");
        }

        if (!updated.getAuthenticationServiceIdentity().equals(original.getAuthenticationServiceIdentity())) {
            count = count + 1;
            sb.append(count + ". Authentication Service Identity changed from "
                + original.getAuthenticationServiceIdentity() + " to " + updated.getAuthenticationServiceIdentity()
                + ".\n");
        }

        if (updated.isPublish() != original.isPublish()) {
            count = count + 1;
            sb.append(count
                + ". The property specifying whether or not to publish the identity provider was changed from  "
                + original.isPublish() + " to " + updated.isPublish() + ".\n");
        }

        if ((updated.getUserIdAttributeDescriptor() != null)
            && (!updated.getUserIdAttributeDescriptor().equals(original.getUserIdAttributeDescriptor()))) {
            count = count + 1;
            sb.append(count + ". User Id Attribute changed from "
                + original.getUserIdAttributeDescriptor().getNamespaceURI() + ":"
                + original.getUserIdAttributeDescriptor().getName() + " to "
                + updated.getUserIdAttributeDescriptor().getNamespaceURI() + ":"
                + updated.getUserIdAttributeDescriptor().getName() + ".\n");
        }

        if ((updated.getFirstNameAttributeDescriptor() != null)
            && (!updated.getFirstNameAttributeDescriptor().equals(original.getFirstNameAttributeDescriptor()))) {
            count = count + 1;
            sb.append(count + ". First Name Attribute changed from "
                + original.getFirstNameAttributeDescriptor().getNamespaceURI() + ":"
                + original.getFirstNameAttributeDescriptor().getName() + " to "
                + updated.getFirstNameAttributeDescriptor().getNamespaceURI() + ":"
                + updated.getFirstNameAttributeDescriptor().getName() + ".\n");
        }

        if ((updated.getLastNameAttributeDescriptor() != null)
            && (!updated.getLastNameAttributeDescriptor().equals(original.getLastNameAttributeDescriptor()))) {
            count = count + 1;
            sb.append(count + ". Last Name Attribute changed from "
                + original.getLastNameAttributeDescriptor().getNamespaceURI() + ":"
                + original.getLastNameAttributeDescriptor().getName() + " to "
                + updated.getLastNameAttributeDescriptor().getNamespaceURI() + ":"
                + updated.getLastNameAttributeDescriptor().getName() + ".\n");

        }

        if ((updated.getEmailAttributeDescriptor() != null)
            && (!updated.getEmailAttributeDescriptor().equals(original.getEmailAttributeDescriptor()))) {
            count = count + 1;
            sb.append(count + ". Email Attribute changed from "

            + original.getEmailAttributeDescriptor().getNamespaceURI() + ":"
                + original.getEmailAttributeDescriptor().getName() + " to "
                + updated.getEmailAttributeDescriptor().getNamespaceURI() + ":"
                + updated.getEmailAttributeDescriptor().getName() + ".\n");

        }

        if (!Arrays.equals(original.getAuthenticationMethod(), updated.getAuthenticationMethod())) {
            count = count + 1;
            sb.append(count + ". Authentication methods changed from (");

            boolean first = true;
            if (original.getAuthenticationMethod() != null) {
                for (int i = 0; i < original.getAuthenticationMethod().length; i++) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(original.getAuthenticationMethod(i));
                    first = false;
                }
            }
            sb.append(") to (");

            first = true;

            if (updated.getAuthenticationMethod() != null) {
                for (int i = 0; i < updated.getAuthenticationMethod().length; i++) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(updated.getAuthenticationMethod(i));
                    first = false;
                }
            }

            sb.append(").");
        }

        return sb.toString();
    }


    public static String generateReport(GridUser original, GridUser updated) {
        StringBuffer sb = new StringBuffer();
        sb.append("The following changes were made to the Grid user, " + original.getGridId() + ": \n");
        int count = 0;
        if ((updated.getFirstName() != null) && (!updated.getFirstName().equals(original.getFirstName()))) {
            count = count + 1;
            sb.append(count + ". First Name changed from " + original.getFirstName() + " to " + updated.getFirstName()
                + ".\n");
        }

        if ((updated.getLastName() != null) && (!updated.getLastName().equals(original.getLastName()))) {
            count = count + 1;
            sb.append(count + ". Last Name changed from " + original.getLastName() + " to " + updated.getLastName()
                + ".\n");
        }

        if ((updated.getEmail() != null) && (!updated.getEmail().equals(original.getEmail()))) {
            count = count + 1;
            sb.append(count + ". Email changed from " + original.getEmail() + " to " + updated.getEmail() + ".\n");
        }

        if ((updated.getUserStatus() != null) && (!updated.getUserStatus().equals(original.getUserStatus()))) {
            count = count + 1;
            sb.append(count + ". Status changed from " + original.getUserStatus().getValue() + " to "
                + updated.getUserStatus().getValue() + ".\n");
        }
        return sb.toString();
    }


    public static String generateReport(HostCertificateRecord original, HostCertificateRecord updated) {
        StringBuffer sb = new StringBuffer();
        sb.append("The following changes were made to the host certificate, " + original.getHost() + "("
            + original.getId() + "): \n");
        int count = 0;

        if ((updated.getStatus() != null) && (!updated.getStatus().equals(original.getStatus()))) {
            count = count + 1;
            sb.append(count + ". Status changed from " + original.getStatus().getValue() + " to "
                + updated.getStatus().getValue() + ".\n");
        }

        if ((updated.getOwner() != null) && (!updated.getOwner().equals(original.getOwner()))) {
            count = count + 1;
            sb.append(count + ". Owner changed from " + original.getOwner() + " to " + updated.getOwner() + ".\n");
        }
        return sb.toString();
    }


    public static String generateReport(UserCertificateRecord original, UserCertificateRecord updated) {
        StringBuffer sb = new StringBuffer();
        sb.append("The following changes were made to the user certificate, " + original.getSerialNumber() + ": \n");
        int count = 0;

        if ((updated.getStatus() != null) && (!updated.getStatus().equals(original.getStatus()))) {
            count = count + 1;
            sb.append(count + ". Status changed from " + original.getStatus().getValue() + " to "
                + updated.getStatus().getValue() + ".\n");
        }

        if ((updated.getNotes() != null) && (!updated.getNotes().equals(original.getNotes()))) {
            count = count + 1;
            sb.append(count + ". Notes updated.\n");
        }

        return sb.toString();
    }

}
