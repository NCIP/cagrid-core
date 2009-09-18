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
public class ManualRegistrationPolicy implements IdPRegistrationPolicy {

    public String getDescription() {
        return "This policy requires registering users, to be manually approved my an administrator";
    }


    public String getName() {
        return "Manual Registration";
    }


    public ApplicationReview register(Application a) throws DorianInternalFault, InvalidUserPropertyFault {
        ApplicationReview ar = new ApplicationReview();
        ar.setStatus(LocalUserStatus.Pending);
        ar.setRole(LocalUserRole.Non_Administrator);
        ar.setMessage("Your application will be reviewed by an administrator.");
        return ar;
    }

}
