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
package org.cagrid.gaards.dorian.client;

import java.io.StringWriter;

import org.cagrid.gaards.dorian.policy.DorianPolicy;


public class PolicyDriver {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            GridUserClient client = new GridUserClient("https://localhost:8443/wsrf/services/cagrid/Dorian");
            DorianPolicy policy = client.getPolicy();
            StringWriter writer = new StringWriter();
            gov.nih.nci.cagrid.common.Utils.serializeObject(policy, DorianBaseClient.POLICY, writer, PolicyDriver.class
                .getResourceAsStream("/client-config.wsdd"));;
            String xml = writer.toString();
            System.out.println(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
