package com.cagrid.liferay.websso.client;

import com.liferay.portal.NoSuchUserIdMapperException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserIdMapper;
import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.security.auth.AutoLoginException;
import com.liferay.portal.service.UserIdMapperLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsUtil;

import java.rmi.RemoteException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.websso.client.acegi.WebSSOUser;

/**
 * <a href="CaGridLiferayCASAutoLoginHook.java.html"><b><i>View Source</i></b></a>
 * 
 * Liferay portal provides a seperate hooking mechanism to fetch valid credentials from 
 * any third party security system.Inside this hook method ,we have to populate current user 
 * info into liferay if that's user information doesn't exist before.
 */
public class CaGridLiferayCASAutoLoginHook implements AutoLogin {

	private static Log _log = LogFactory.getLog(CaGridLiferayCASAutoLoginHook.class);
	public String[] login(HttpServletRequest req, HttpServletResponse res)
		throws AutoLoginException {
		try {
			String[] credentials = null;
			long companyId = PortalUtil.getCompanyId(req);
			if (!PrefsPropsUtil.getBoolean(companyId,PropsUtil.CAS_AUTH_ENABLED)) {
				return credentials;
			}
			WebSSOUser webssoUser = (WebSSOUser) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			User user = null;
			try {
				_log.debug("Users Company Id "+companyId+"  GridId "+webssoUser.getGridId());
				UserIdMapper userIdMapper = UserIdMapperLocalServiceUtil
						.getUserIdMapperByExternalUserId("", webssoUser.getGridId());				
				user = UserLocalServiceUtil.getUserById(userIdMapper.getUserId());
			} catch (NoSuchUserIdMapperException nusime) {
				_log.debug(nusime.getMessage());
				user = addUser(companyId, webssoUser);
				UserIdMapperLocalServiceUtil.updateUserIdMapper(user
						.getUserId(), null, null, webssoUser.getGridId());
			}
			credentials = new String[3];
			credentials[0] = String.valueOf(user.getUserId());
			credentials[1] = user.getPassword();
			credentials[2] = Boolean.TRUE.toString();
			return credentials;
		} catch (Exception e) {
			throw new AutoLoginException(e);
		}
	}
	
	/**
	 * Assumptions creating Grid User:
	 * GridId is always unique, Create UserIdMapper 
	 * Always screen name generator must be Auto generated.
	 * Duplicate email Ids can be present for a particular company Id for a particular user
	 */
	protected User addUser(long paramCompanyId, WebSSOUser webssoUser)
		throws PortalException, SystemException {
		
		long companyId=paramCompanyId;
		boolean autoPassword=true;
		String password1=null;
		String password2=null;
		boolean autoScreenName=true; // always must be true;
		String screenName="";
		String emailAdress=webssoUser.getEmailId();
		Locale locale=Locale.US;
		String firstName=webssoUser.getFirstName();
		String middleName=null;
		String lastName=webssoUser.getLastName();
		int prefix=0;
		int suffix=0;
		boolean male=true;
		int birthMonth=1;
		int birthDay=1;
		int birthYear=1900;
		String jobTitle=null;
		int organizationId=0;
		int locationId=0;
		boolean sendEmail=false;

		User user=null;
		try {
		 user = UserServiceUtil.addUser(companyId, autoPassword, password1,
					password2, autoScreenName, screenName, emailAdress, locale,
					firstName, middleName, lastName, prefix, suffix, male,
					birthMonth, birthDay, birthYear, jobTitle, organizationId,
					locationId, sendEmail);
			
		} catch (RemoteException e) {
			throw new SystemException(e);
		}
		return user;
	}	
}