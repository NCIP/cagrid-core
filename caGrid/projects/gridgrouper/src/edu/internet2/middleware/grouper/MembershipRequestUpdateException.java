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
