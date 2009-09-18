package org.cagrid.gaards.authentication.common;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.OneTimePassword;

public class AuthenticationProfile {

	public static QName BASIC_AUTHENTICATION = new QName(
			"http://gaards.cagrid.org/authentication", "BasicAuthentication");
	public static QName ONE_TIME_PASSWORD = new QName(
			"http://gaards.cagrid.org/authentication",
			"OneTimePassword");

	public static boolean isValid(QName profile) {
		if (profile.equals(BASIC_AUTHENTICATION)) {
			return true;
		} else if (profile.equals(ONE_TIME_PASSWORD)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isValid(Set<QName> set) {
		if ((set == null) || (set.size() < 1)) {
			return false;
		}
		Iterator<QName> itr = set.iterator();
		while (itr.hasNext()) {
			if (!isValid(itr.next())) {
				return false;
			}
		}

		return true;
	}

	public static boolean isSupported(Set<QName> profiles, Credential c) {
		if ((profiles == null) || (profiles.size() < 1)) {
			return false;
		}
		Iterator<QName> itr = profiles.iterator();
		while (itr.hasNext()) {
			QName profile = itr.next();
			if ((profile.equals(BASIC_AUTHENTICATION))
					&& (c.getClass().equals(BasicAuthentication.class))) {
				return true;
			} else if ((profile
					.equals(ONE_TIME_PASSWORD))
					&& (c.getClass().equals(OneTimePassword.class))) {
				return true;
			}

		}
		return false;
	}
}
