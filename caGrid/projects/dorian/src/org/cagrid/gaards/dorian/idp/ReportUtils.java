package org.cagrid.gaards.dorian.idp;

public class ReportUtils {

    public static String generateReport(LocalUser original, LocalUser updated) {
        StringBuffer sb = new StringBuffer();
        sb.append("The following changes were made to the local user, " + original.getUserId() + ": \n");
        int count = 0;

        if (!original.getPassword().equals(updated.getPassword())) {
            count = count + 1;
            sb.append(count + ". Password changed.\n");
        }

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

        if ((updated.getOrganization() != null) && (!updated.getOrganization().equals(original.getOrganization()))) {
            count = count + 1;
            sb.append(count + ". Organization changed from " + original.getOrganization() + " to "
                + updated.getOrganization() + ".\n");
        }

        if ((updated.getAddress() != null) && (!updated.getAddress().equals(original.getAddress()))) {
            count = count + 1;
            sb
                .append(count + ". Address changed from " + original.getAddress() + " to " + updated.getAddress()
                    + ".\n");
        }

        if ((updated.getAddress2() != null) && (!updated.getAddress2().equals(original.getAddress2()))) {
            count = count + 1;
            sb.append(count + ". Address2 changed from " + original.getAddress2() + " to " + updated.getAddress2()
                + ".\n");
        }

        if ((updated.getCity() != null) && (!updated.getCity().equals(original.getCity()))) {
            count = count + 1;
            sb.append(count + ". City changed from " + original.getCity() + " to " + updated.getCity() + ".\n");
        }

        if ((updated.getState() != null) && (!updated.getState().equals(original.getState()))) {
            count = count + 1;
            sb.append(count + ". State changed from " + original.getState() + " to " + updated.getState() + ".\n");
        }

        if ((updated.getCountry() != null) && (!updated.getCountry().equals(original.getCountry()))) {
            count = count + 1;
            sb
                .append(count + ". Country changed from " + original.getCountry() + " to " + updated.getCountry()
                    + ".\n");
        }

        if ((updated.getZipcode() != null) && (!updated.getZipcode().equals(original.getZipcode()))) {
            count = count + 1;
            sb.append(count + ". Zip code changed from " + original.getZipcode() + " to " + updated.getZipcode()
                + ".\n");
        }

        if ((updated.getPhoneNumber() != null) && (!updated.getPhoneNumber().equals(original.getPhoneNumber()))) {
            count = count + 1;
            sb.append(count + ". Phone number changed from " + original.getPhoneNumber() + " to "
                + updated.getPhoneNumber() + ".\n");
        }

        if ((updated.getStatus() != null) && (!updated.getStatus().equals(original.getStatus()))) {
            count = count + 1;
            sb.append(count + ". Status changed from " + original.getStatus().getValue() + " to "
                + updated.getStatus().getValue() + ".\n");
        }

        if ((updated.getRole() != null) && (!updated.getRole().equals(original.getRole()))) {
            count = count + 1;
            sb.append(count + ". Role changed from " + original.getRole().getValue() + " to "
                + updated.getRole().getValue() + ".\n");

        }
        return sb.toString();
    }
}
