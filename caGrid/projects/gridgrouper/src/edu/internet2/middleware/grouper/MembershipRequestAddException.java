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

public class MembershipRequestAddException extends Exception {
  public MembershipRequestAddException() { 
    super(); 
  }
  public MembershipRequestAddException(String msg) { 
    super(msg); 
  }
  public MembershipRequestAddException(String msg, Throwable cause) { 
    super(msg, cause); 
  }
  public MembershipRequestAddException(Throwable cause) { 
    super(cause); 
  }
}
