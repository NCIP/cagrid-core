package org.cagrid.gaards.authentication.test;

import gov.nih.nci.security.authentication.principal.EmailIdPrincipal;
import gov.nih.nci.security.authentication.principal.FirstNamePrincipal;
import gov.nih.nci.security.authentication.principal.LastNamePrincipal;
import gov.nih.nci.security.authentication.principal.LoginIdPrincipal;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.common.InvalidCredentialException;
import org.cagrid.gaards.authentication.service.BasicAuthenticationSubjectProvider;

public class ExampleSubjectProvider1 extends BasicAuthenticationSubjectProvider {
	


	public ExampleSubjectProvider1() {
		super();
	}

	public Subject getSubject(Credential credential)
			throws InvalidCredentialException {
		String userId = null;
		if (credential instanceof BasicAuthentication) {
			BasicAuthentication c = (BasicAuthentication) credential;
			if (c.getPassword().equals("password")) {
				userId = c.getUserId();
			} else {
				throw new InvalidCredentialException(
						"Invalid password specified!!!");
			}
		}
		Set<Principal> principals = new HashSet<Principal>();
		principals.add(new LoginIdPrincipal(userId));
		principals.add(new FirstNamePrincipal(Constants.DEFAULT_FIRST_NAME));
		principals.add(new LastNamePrincipal(Constants.DEFAULT_LAST_NAME));
		principals.add(new EmailIdPrincipal(Constants.DEFAULT_EMAIL));
		Subject subject = new Subject(true, principals, new HashSet(),
				new HashSet());
		return subject;
	}
}
