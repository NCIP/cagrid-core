<%@ page session="true"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form:form name="fm1" method="post" id="fm1" cssClass="sidebarSection" commandName="${commandName}" htmlEscape="true">
	<form:errors path="*" cssClass="errors" id="status" element="div" />
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
		<tr>
			<td valign="top">
				<div class="sidebarTitle"></br>
					<font size="1em"><spring:message code="screen.welcome.instructions"/><br>
					<spring:message code="screen.welcome.instructions1"/><br>
					<spring:message code="screen.welcome.instructions2"/><br>
					<spring:message code="screen.welcome.instructions3"/><br>
					<spring:message code="screen.welcome.instructions4"/><br></font></br>
				</div>
			</td>
		</tr>
		<tr>
			<td valign="top" height="100%">
			<table border="0" cellspacing="0" cellpadding="10" width="100%" height="100%">
				<tr>
				<td valign="top">
				 <div class="row">
					<label for="dorianName">
						<spring:message code="screen.welcome.label.credential.provider" /><br>
					</label>
					<spring:message code="screen.welcome.label.credential.provider.accesskey" var="dorianNameAccessKey" />
					<input type="hidden" name="lt" value="${flowExecutionKey}" /> 					
					<form:select cssClass="required" cssErrorClass="error" id="dorianName" tabindex="1" path="dorianName"
					accesskey="${dorianNameAccessKey}" htmlEscape="true" onchange="submitSelectDorianForm();">
					<form:option value="-" label="-- Please Select" />
					<form:options items="${doriansInformationList}"
								itemValue="displayName" itemLabel="displayName"/>
					</form:select>
					
					<SCRIPT type="text/javascript">
							function submitSelectDorianForm() {
								document.getElementById("_eventId").value="selectDorian";
								document.fm1.submit();
							}
					</SCRIPT>
				</div>
				<div class="row">
					<label for="authenticationServiceURL">
						<spring:message code="screen.welcome.label.organization" />
					</label>
					<spring:message code="screen.welcome.label.organization.accesskey" var="authenticationServiceAccessKey" /> 
					</br>
					<input type="hidden" name="lt" value="${flowExecutionKey}" /> 
					<form:select
						cssClass="required" cssErrorClass="error"
						id="authenticationServiceURL" tabindex="2"
						path="authenticationServiceURL"
						accesskey="${authenticationServiceAccessKey}" htmlEscape="true" onchange="submitAuthenticationServiceForm();">
					<form:option value="-" label="-- Please Select" />
					<form:options items="${authenticationServiceInformationList}"
						itemValue="authenticationServiceURL"
						itemLabel="authenticationServiceName" />
					</form:select>
				
					<SCRIPT type="text/javascript">
							function submitAuthenticationServiceForm() {
								document.getElementById("_eventId").value="selectAuthenticationService";
								document.fm1.submit();
							}
					</SCRIPT>
				</div>
				<input type="hidden" name="lt" value="${flowExecutionKey}" />  
				<c:choose>			
					<c:when test="${fn:length(authenticationServiceProfileInformationList)==1}">
						<div class="row">
							<label for="authenticationServiceProfile">
								<spring:message code="screen.welcome.label.credential.type" />
							</label> 
								<spring:message code="screen.welcome.label.credential.type.accesskey" var="authenticationServiceAccessKey" />
							</br>
							<font size="1em"><form:input path="authenticationServiceProfile" disabled="true"/></font>
						</div>
					</c:when>
		
					<c:when test="${fn:length(authenticationServiceProfileInformationList)==0}">
						<img height="100" width="1">
					</c:when>	
		
					<c:otherwise>
						<div class="row">
							<label for="authenticationServiceProfile">
								<spring:message code="screen.welcome.label.credential.type" />
							</label> 
							<spring:message code="screen.welcome.label.credential.type.accesskey" var="authenticationServiceAccessKey" />
							</br>
							<form:select
								cssClass="required" cssErrorClass="error"
								id="authenticationServiceProfile" tabindex="3"
								path="authenticationServiceProfile"
								accesskey="${authenticationServiceProfileAccessKey}" htmlEscape="true" onchange="submitAuthenticationProfile();">				
								<form:option value="-" label="-- Please Select" />
								<form:options items="${authenticationServiceProfileInformationList}" itemValue="localPart" itemLabel="localPart" />
							</form:select>
							<SCRIPT type="text/javascript">
								 function submitAuthenticationProfile() {
									document.getElementById("_eventId").value="selectAuthenticationProfile";
									document.fm1.submit();		
								 }
							</SCRIPT>
						 </div>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${basicAuthentication==authenticationServiceProfile}">
							<div class="row">
								<label for="username">
									<spring:message code="screen.welcome.label.netid" />
								</label> 
								<c:if test="${not empty sessionScope.openIdLocalId}">
									<strong>${sessionScope.openIdLocalId}</strong>
									<input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
								</c:if> 
								<c:if test="${empty sessionScope.openIdLocalId}">
									<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
									</br>
								<font size="1em"><form:input cssClass="required" cssErrorClass="error" id="username" 
										size="20" tabindex="4" accesskey="${userNameAccessKey}"
										path="username" autocomplete="false" htmlEscape="true" /></font>
								</c:if>
							</div>
				
							<div class="row">
								<label for="password">
									<spring:message code="screen.welcome.label.password" />
								</label> 
								<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" /> 
								</br>
								<font size="1em"><form:password cssClass="required"
									cssErrorClass="error" id="password" size="20" tabindex="5"
									path="password" accesskey="${passwordAccessKey}" htmlEscape="true"
									autocomplete="off" /></font>
							</div>
						</c:when>
						<c:when test="${oneTimePassword == authenticationServiceProfile}">
							<div class="row">
								<label for="username"> 
									<spring:message code="screen.welcome.label.netid" />
								</label>
								<c:if
									test="${not empty sessionScope.openIdLocalId}">
									<strong>${sessionScope.openIdLocalId}</strong>
									<input type="hidden" id="username" name="username"
										value="${sessionScope.openIdLocalId}" />
								</c:if>
								<c:if test="${empty sessionScope.openIdLocalId}">
									<spring:message code="screen.welcome.label.netid.accesskey"
										var="userNameAccessKey" />
									</br>
									<font size="1em"><form:input cssClass="required" cssErrorClass="error" id="username"
										size="20" tabindex="4" accesskey="${userNameAccessKey}"
										path="username" autocomplete="false" htmlEscape="true" /></font>
								</c:if>
							</div>
				
							<div class="row">
								<label for="onetimepassword"> 
									<spring:message code="screen.welcome.label.onetimepassword" />
								</label>
								<spring:message code="screen.welcome.label.onetimepassword.accesskey" var="onetimepasswordAccessKey" /> 
								</br>
								<font size="1em"><form:password
									cssClass="required" cssErrorClass="error" id="onetimepassword"
									size="20" tabindex="5" path="onetimepassword"
									accesskey="${onetimepasswordAccessKey}" htmlEscape="true"
									autocomplete="off" /></font>
							</div>
						</c:when>
						<c:otherwise>
							<img height="152" width="0"/>
						</c:otherwise>
					</c:choose>
					<div class="row">
						<input id="warn" name="warn" value="true" tabindex="6"
						accesskey="<spring:message code="screen.welcome.label.warn.accesskey" />"
						type="checkbox" /> 
						<label for="warn"><spring:message code="screen.welcome.label.warn" />
						</label>
					</div>
					<div class="row btn-row">
						<input type="hidden" name="lt" value="${flowExecutionKey}" />
						<input type="hidden" name="_eventId" id="_eventId" value="submit" />
						<input class="sidebarLogin" name="submitButton"
							accesskey="l"
							value="<spring:message code="screen.welcome.button.login" />"
							tabindex="7" type="submit" /> 
						
						<input class="sidebarLogin" name="reset" accesskey="c"
							value="<spring:message code="screen.welcome.button.clear" />"
						    tabindex="8" type="reset" />
				   	</div>
				</td>
				</tr>
			</table>
		 </td>
	   </tr>
	</table>
</form:form>