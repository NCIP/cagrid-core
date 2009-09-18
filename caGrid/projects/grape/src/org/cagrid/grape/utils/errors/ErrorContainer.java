package org.cagrid.grape.utils.errors;

import java.util.Date;


public class ErrorContainer {
    private Date errorDate;
	private String message;
	private String detail;
    private Throwable error;
	
	public ErrorContainer(String message, String detail, Throwable error) {
        this.errorDate = new Date();
		this.message = message;
		this.detail = detail;
        this.error = error;
	}
    
    
    public Date getErrorDate() {
        return errorDate;
    }
	
	
	public String getMessage() {
		return message;
	}
	
	
	public String getDetail() {
		return detail;
	}
    
    
    public Throwable getError() {
        return error;
    }
	
	
	public String toString() {
		return this.getMessage();
	}
}