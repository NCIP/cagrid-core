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
