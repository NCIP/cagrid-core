<jsp:directive.include file="includes/top.jsp" />
<tr>
	<td height="100%"><a name="content" />
	<table summary="" cellpadding="0" cellspacing="0" border="0"
		height="100%">
		<div id="status" class="errors">
		<h2><spring:message code="screen.service.sso.error.header" /></h2>
		<p><spring:message code="screen.service.sso.error.message"
			arguments="<%=request.getContextPath() + "/login?" + request.getQueryString()%>" /></p>
		</div>
	</table>
</tr>
<jsp:directive.include file="includes/bottom.jsp" />
