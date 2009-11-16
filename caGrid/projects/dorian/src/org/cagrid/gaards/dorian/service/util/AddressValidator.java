package org.cagrid.gaards.dorian.service.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import gov.nih.nci.cagrid.common.Utils;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class AddressValidator {

    public static void validateEmail(String email) throws IllegalArgumentException {
        validateField("Email", email);
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            if (!hasNameAndDomain(email)) {
                throw new IllegalArgumentException("Invalid email address specified.");
            }
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Invalid email address specified.");
        }
    }


    private static boolean hasNameAndDomain(String aEmailAddress) {
        String[] tokens = aEmailAddress.split("@");
        if (tokens.length == 2) {
            if ((Utils.clean(tokens[0]) != null) && (Utils.clean(tokens[0]) != null)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public static void validateField(String type, String name) throws IllegalArgumentException {
        name = Utils.clean(name);
        if (name == null) {
            throw new IllegalArgumentException("No " + type + " specified.");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("The " + type
                + " specified is too long, it must be less than 255 characters.");
        }
    }

}
