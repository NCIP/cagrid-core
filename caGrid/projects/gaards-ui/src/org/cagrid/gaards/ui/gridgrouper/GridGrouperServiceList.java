package org.cagrid.gaards.ui.gridgrouper;

import java.util.List;

import javax.swing.JComboBox;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGrouperServiceList extends JComboBox {

    private static final long serialVersionUID = 1L;

    private static GridGrouperHandle lastSelectedService;


    public GridGrouperServiceList() {
        List<GridGrouperHandle> services = GridGrouperUIUtils.getGridGrouperServices();
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


    public GridGrouperHandle getSelectedService() {
        return (GridGrouperHandle) getSelectedItem();
    }

}
