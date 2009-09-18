<jsp:directive.include file="includes/top.jsp" />
<tr>
	<td height="100%"><a name="content" />
	<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
		<div id="msg" class="success">
			<h2><spring:message code="screen.logout.header" /></h2>
	
			<p><spring:message code="screen.logout.success" /></p>
			<p><spring:message code="screen.logout.security" /></p>
	
			<%--
				 Implementation of support for the "url" parameter to logout as recommended in CAS spec section 2.3.1.
				 A service sending a user to CAS for logout can specify this parameter to suggest that we offer
				 the user a particular link out from the logout UI once logout is completed.  We do that here.
				--%> <c:if test="${not empty param['url']}">
				<p><spring:message code="screen.logout.redirect"
					arguments="${param['url']}" /></p>
			</c:if>
		</div>
	</table>
</tr>
<jsp:directive.include file="includes/bottom.jsp" />
