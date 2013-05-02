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
