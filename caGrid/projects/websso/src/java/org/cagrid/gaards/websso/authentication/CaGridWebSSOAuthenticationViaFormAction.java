package org.cagrid.gaards.websso.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.xml.namespace.QName;

import org.cagrid.gaards.websso.beans.AuthenticationServiceInformation;
import org.cagrid.gaards.websso.utils.WebSSOConstants;
import org.jasig.cas.web.flow.AuthenticationViaFormAction;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.validation.DataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class CaGridWebSSOAuthenticationViaFormAction extends
		AuthenticationViaFormAction {

	@Override
	public Event referenceData(RequestContext context) throws Exception{
		ServletContext servletContext = WebUtils.getHttpServletRequest(context)
				.getSession().getServletContext();
		WebApplicationContext ctx =
			WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		AuthenticationProfileServiceManager serviceManager=(AuthenticationProfileServiceManager)ctx.getBean(WebSSOConstants.SERVICE_MANAGER);
		context.getFlowScope().put("doriansInformationList",serviceManager.getDorianInformationList());
		
		context.getFlowScope().put("basicAuthentication",UsernamePasswordAuthenticationServiceURLCredentials.BASIC_AUTHENTICATION);
		context.getFlowScope().put("oneTimePassword",UsernamePasswordAuthenticationServiceURLCredentials.ONE_TIME_PASSWORD);
		
		if(context.getFlowScope().get("authenticationServiceInformationList")==null)
			context.getFlowScope().put("authenticationServiceInformationList",new ArrayList<AuthenticationServiceInformation>());
		if(context.getFlowScope().get("authenticationServiceProfileInformationList")==null)
		context.getFlowScope().put("authenticationServiceProfileInformationList",new ArrayList<QName>());
		if(context.getFlowScope().get("authenticationServiceProfile")==null)
		context.getFlowScope().put("authenticationServiceProfile","");
		return super.referenceData(context);
	}
	
	public Event authenticationServicesInformation(RequestContext context)
			throws Exception {		
		UsernamePasswordAuthenticationServiceURLCredentials credentials = bindUserCredentials(context);
		ServletContext servletContext = WebUtils.getHttpServletRequest(context).getSession().getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		AuthenticationProfileServiceManager serviceManager = (AuthenticationProfileServiceManager) ctx.getBean(WebSSOConstants.SERVICE_MANAGER);				
		List<AuthenticationServiceInformation> authenticationServiceInformationList = serviceManager.getAuthenticationServiceInformationList(credentials.getDorianName());
		context.getFlowScope().put("authenticationServiceInformationList",authenticationServiceInformationList);
		context.getFlowScope().put("authenticationServiceProfileInformationList",new ArrayList<QName>());
		context.getFlowScope().put("authenticationServiceProfile","");
		return super.referenceData(context);
	}
	
	@SuppressWarnings("unchecked")
	public Event authenticationServiceProfilesInformation(RequestContext context) throws Exception{
		UsernamePasswordAuthenticationServiceURLCredentials credentials = bindUserCredentials(context);
		ServletContext servletContext = WebUtils.getHttpServletRequest(context)
				.getSession().getServletContext();
		WebApplicationContext ctx =
			WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		List<AuthenticationServiceInformation> authenticationServices=(List<AuthenticationServiceInformation>)context.getFlowScope().get("authenticationServiceInformationList");
		AuthenticationProfileServiceManager serviceManager=(AuthenticationProfileServiceManager)ctx.getBean(WebSSOConstants.SERVICE_MANAGER);
		Set<QName> authenticationServiceProfiles=serviceManager.getAuthenticationProfilesList(authenticationServices,credentials.getAuthenticationServiceURL());
		
		if(authenticationServiceProfiles.size()==1){
			QName asprofile=authenticationServiceProfiles.iterator().next();
			credentials.setAuthenticationServiceProfile(asprofile.getLocalPart());
			context.getFlowScope().put("authenticationServiceProfile",asprofile.getLocalPart());				
		}else if(authenticationServiceProfiles.size()==0){
			context.getFlowScope().put("authenticationServiceProfile","");
		}
		context.getFlowScope().put("authenticationServiceProfileInformationList",authenticationServiceProfiles);
		return super.referenceData(context);
	}
		
	public Event authenticationLoginInputFieldsInformation(RequestContext context) throws Exception{
		UsernamePasswordAuthenticationServiceURLCredentials credentials=bindUserCredentials(context);
		context.getFlowScope().put("authenticationServiceProfile",credentials.getAuthenticationServiceProfile());
		return super.referenceData(context);
	}
	
	private UsernamePasswordAuthenticationServiceURLCredentials bindUserCredentials(
			RequestContext context) throws Exception {
		Object formObject = getFormObject(context);
		DataBinder binder = createBinder(context, formObject);
		doBind(context, binder);
		UsernamePasswordAuthenticationServiceURLCredentials credentials=(UsernamePasswordAuthenticationServiceURLCredentials)formObject;
		return credentials;
	}
}
