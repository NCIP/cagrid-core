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
package edu.internet2.middleware.grouper;

public class MembershipRequestUpdateException extends Exception {
  public MembershipRequestUpdateException() { 
    super(); 
  }
  public MembershipRequestUpdateException(String msg) { 
    super(msg); 
  }
  public MembershipRequestUpdateException(String msg, Throwable cause) { 
    super(msg, cause); 
  }
  public MembershipRequestUpdateException(Throwable cause) { 
    super(cause); 
  }
}
