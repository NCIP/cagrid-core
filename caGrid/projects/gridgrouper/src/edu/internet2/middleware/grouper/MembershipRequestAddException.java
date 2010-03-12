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
