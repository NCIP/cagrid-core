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
package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.dorian.federation.FederationAudit;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 */
public class FederationAuditComboBox extends JComboBox {
    private static Log log = LogFactory.getLog(FederationAuditComboBox.class);

    private static final long serialVersionUID = 1L;

    public static String ANY = "Any";

    private List<FederationAudit> list;


    public FederationAuditComboBox() {
        list = new ArrayList<FederationAudit>();

        Field[] fields = FederationAudit.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            if (FederationAudit.class.isAssignableFrom(fields[i].getType())) {
                try {
                    FederationAudit o = (FederationAudit) fields[i].get(null);
                    list.add(o);
                } catch (Exception e) {
                    FaultUtil.logFault(log, e);
                }
            }
        }
        this.addItem(ANY);
        for (int i = 0; i < list.size(); i++) {
            this.addItem(list.get(i));
        }
        this.setSelectedItem(ANY);
    }


    public FederationAuditComboBox(List<FederationAudit> list) {
        this.addItem(ANY);
        for (int i = 0; i < list.size(); i++) {
            this.addItem(list.get(i));
        }
        this.setSelectedItem(ANY);
    }


    public void setToAny() {
        setSelectedItem(ANY);
    }


    public FederationAudit getSelectedAuditType() {
        if (getSelectedItem().getClass().isAssignableFrom(FederationAudit.class)) {
            return (FederationAudit) getSelectedItem();
        } else {
            return null;
        }
    }
}
