/**
 * Wizard Framework Copyright 2004 - 2005 Andrew Pietsch This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA $Id:
 * WizardPane.java,v 1.1 2007/05/17 13:58:50 joshua Exp $
 */
package org.pietschy.wizard.pane;

import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.WizardStep;


/**
 * WizardPane: must be implemented by components that want to act as wizard
 * panes in collaboration with a {@link WizardPaneStep}.
 * 
 * @author Andrea Aime
 * @deprecated since 0.1.10. WizardStep is now an interface allowing
 *             implementation by any swing componenent. See
 *             {@link org.pietschy.wizard.PanelWizardStep}.
 */
public interface WizardPane {

    /**
     * Called to initialize the step. This method will be called when the wizard
     * is first initialising. You should get a reference to the
     * {@link org.pietschy.wizard.WizardStep} in order to call
     * {@link org.pietschy.wizard.WizardStep}#setComplete(boolean) when the step is filled with enough information,
     * and a reference to the model in order to update it on {@link #applyState()}.
     * 
     * @param WizardStep
     *            the step that uses this pane
     * @param model
     *            the model to which the step belongs.
     */
    public void init(WizardStep step, WizardModel model);


    /**
     * This method is called whenever the user presses next while this step is
     * active.
     * <p>
     * If this method will take a long time to complete, implementors should
     * consider executing the work and a separate thread calling
     * {@link WizardStep#setView()}with some kind of progress indicator.
     * <p>
     * This method will only be called if {@link WizardModel#isNextAvailable}
     * and {@link WizardStep#isComplete}return true.
     * 
     * @throws InvalidStateException
     *             if an error occurs and the wizard can't progress to the next
     *             step. By default the message of this exception will be
     *             displayed to the user. If you wish to prevent this behaviour
     *             please ensure {@link InvalidStateException#setShowUser}is
     *             called with a value of <tt>false</tt>.
     */
    public void applyState() throws InvalidStateException;


    /**
     * Called to prepare this step to display. Implementors should query the
     * model and configure their view appropriately.
     * <p>
     * This method will be called whenever the step is to be displayed,
     * regardless of whether the user pressed next or previous.
     */
    public void prepare();

}
