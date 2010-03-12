package org.cagrid.identifiers.namingauthority;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpProcessor {
	void setNamingAuthority( NamingAuthority na );
	NamingAuthority getNamingAuthority();
	void process(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
