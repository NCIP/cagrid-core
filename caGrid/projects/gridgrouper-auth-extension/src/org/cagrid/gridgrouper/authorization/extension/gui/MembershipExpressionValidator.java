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
package org.cagrid.gridgrouper.authorization.extension.gui;

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;

public class MembershipExpressionValidator {
    
    public static void validateMembeshipExpression(MembershipExpression exp) throws Exception {
        if(exp.getLogicRelation()==null || (exp.getMembershipQuery()==null && exp.getMembershipExpression()==null)){
            throw new Exception("Invalid gridgrouper expression!");
        }
        if(exp.getMembershipExpression()!=null){
          for(int i = 0; i < exp.getMembershipExpression().length; i ++){
              validateMembeshipExpression(exp.getMembershipExpression(i));
          }
        }
    }

}
