package org.cagrid.gaards.credentials;

public class DorianUserCredentialEntry extends X509CredentialEntry {

    private DorianUserCredentialDescriptor descriptor;


    public DorianUserCredentialEntry(DorianUserCredentialDescriptor des) throws Exception {
        super(des);
        this.descriptor = des;
    }


    public String getDisplayName() {
        if ((this.descriptor.getFirstName() == null) || (this.descriptor.getLastName() == null)
            || (this.descriptor.getOrganization() == null)) {
            return getIdentity();
        } else {
            return this.descriptor.getFirstName() + " " + this.descriptor.getLastName() + " ("
                + this.descriptor.getOrganization() + ")";
        }
    }


    public String getDescription() {
        StringBuffer sb = new StringBuffer();
        sb.append("Identity: " + this.descriptor.getIdentity());
        sb.append("\n");
        sb.append("First Name: " + this.descriptor.getFirstName());
        sb.append("\n");
        sb.append("Last Name: " + this.descriptor.getLastName());
        sb.append("\n");
        sb.append("Organization: " + this.descriptor.getOrganization());
        sb.append("\n");
        sb.append("Email: " + this.descriptor.getEmail());
        return sb.toString();
    }


    public String toString() {
        return getDisplayName();
    }


    public DorianUserCredentialDescriptor getDescriptor() {
        return descriptor;
    }


    public String getAuthenticationServiceURL() {
        return getDescriptor().getAuthenticationServiceURL();
    }


    public String getDorianURL() {
        return getDescriptor().getDorianURL();
    }


    public String getFirstName() {
        return getDescriptor().getFirstName();
    }


    public String getLastName() {
        return getDescriptor().getLastName();
    }


    public String getEmail() {
        return getDescriptor().getEmail();
    }


    public String getOrganization() {
        return getDescriptor().getOrganization();
    }

}
