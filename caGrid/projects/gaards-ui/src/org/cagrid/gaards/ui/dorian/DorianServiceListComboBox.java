/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.ui.dorian;

import java.util.List;

import javax.swing.JComboBox;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class DorianServiceListComboBox extends JComboBox {

    private static final long serialVersionUID = 1L;

    private static DorianHandle lastSelectedService;


    public DorianServiceListComboBox() {
        List<DorianHandle> services = ServicesManager.getInstance().getDorianServices();
        for (int i = 0; i < services.size(); i++) {
            this.addItem(services.get(i));
        }
        if (lastSelectedService == null) {
            lastSelectedService = getSelectedService();
        } else {
            this.setSelectedItem(lastSelectedService);
        }
        if (getSelectedService() != null) {
            setToolTipText(getSelectedService().getServiceURL());
        }
        this.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                lastSelectedService = getSelectedService();
                if (getSelectedService() != null) {
                    setToolTipText(getSelectedService().getServiceURL());
                }
            }
        });
    }


    public DorianHandle getSelectedService() {
        return (DorianHandle) getSelectedItem();
    }

}
