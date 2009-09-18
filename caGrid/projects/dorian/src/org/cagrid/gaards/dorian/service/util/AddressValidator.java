package org.cagrid.gaards.dorian.service.util;

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
        validateField("email", email);
        if (email.indexOf("@") == -1) {
            throw new IllegalArgumentException("Invalid email address specified.");
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
