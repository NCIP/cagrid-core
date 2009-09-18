package org.cagrid.gaards.ui.cds;

import java.util.List;

import javax.swing.JComboBox;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CDSListComboBox extends JComboBox {

    private static final long serialVersionUID = 1L;

    private static CDSHandle lastSelectedService;


    public CDSListComboBox() {
        List<CDSHandle> services = CDSUIUtils.getCDSServices();
        for (int i = 0; i < services.size(); i++) {
            this.addItem(services.get(i));
        }
        if (lastSelectedService == null) {
            lastSelectedService = getSelectedService();
        } else {
            this.setSelectedItem(lastSelectedService);
        }
        this.setEditable(false);

        setToolTipText(getSelectedService().getServiceURL());
        this.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                lastSelectedService = getSelectedService();
                setToolTipText(getSelectedService().getServiceURL());
            }
        });
    }


    public CDSHandle getSelectedService() {
        return (CDSHandle) getSelectedItem();
    }

}
