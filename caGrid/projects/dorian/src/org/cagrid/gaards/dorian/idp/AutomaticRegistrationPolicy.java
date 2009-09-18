package org.cagrid.gaards.dorian.idp;

import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class AutomaticRegistrationPolicy implements IdPRegistrationPolicy {

    public String getDescription() {
        return "This policy automatically approves user when they register.";
    }


    public String getName() {
        return "Automatic Registration";
    }


    public ApplicationReview register(Application a) throws DorianInternalFault, InvalidUserPropertyFault {
        ApplicationReview ar = new ApplicationReview();
        ar.setStatus(LocalUserStatus.Active);
        ar.setRole(LocalUserRole.Non_Administrator);
        ar.setMessage("Your account was approved, your current account status is " + LocalUserStatus.Active + ".");
        return ar;
    }

}
