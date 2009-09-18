package com.cagrid.liferay.websso.client;

import com.liferay.portal.ContactFirstNameException;
import com.liferay.portal.ContactLastNameException;
import com.liferay.portal.PortalException;
import com.liferay.portal.ReservedUserEmailAddressException;
import com.liferay.portal.SystemException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.security.pwd.PwdToolkitUtil;
import com.liferay.portal.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.service.impl.UserLocalServiceImpl;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsUtil;

public class CaGridUserLocalServiceImpl extends UserLocalServiceImpl {

	protected void validate(long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, String firstName,
			String lastName, long organizationId, long locationId)
			throws PortalException, SystemException {

		if (!autoScreenName) {
			validateScreenName(companyId, screenName);
		}

		if (!autoPassword) {
			PasswordPolicy passwordPolicy = PasswordPolicyLocalServiceUtil
					.getDefaultPasswordPolicy(companyId);

			PwdToolkitUtil.validate(companyId, 0, password1, password2,
					passwordPolicy);
		}

		if (!Validator.isEmailAddress(emailAddress)) {
			throw new UserEmailAddressException();
		} else {
			// As per caGrid requirements portal can have duplicate email address for a particular company Id.			
			/*try {
				User user = UserUtil.findByC_EA(companyId, emailAddress);

				if (user != null) {
					throw new DuplicateUserEmailAddressException();
				}
			} catch (NoSuchUserException nsue) {
			}*/

			String[] reservedEmailAddresses = PrefsPropsUtil.getStringArray(
					companyId, PropsUtil.ADMIN_RESERVED_EMAIL_ADDRESSES);

			for (int i = 0; i < reservedEmailAddresses.length; i++) {
				if (emailAddress.equalsIgnoreCase(reservedEmailAddresses[i])) {
					throw new ReservedUserEmailAddressException();
				}
			}
		}

		if (Validator.isNull(firstName)) {
			throw new ContactFirstNameException();
		} else if (Validator.isNull(lastName)) {
			throw new ContactLastNameException();
		}
	}
}