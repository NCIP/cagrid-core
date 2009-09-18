<jsp:directive.include file="includes/top.jsp" />
<tr>
	<td height="100%"><a name="content" />
	<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
		<div class="info">
		<p><spring:message code="screen.confirmation.message"
			arguments="${param.service}${fn:indexOf(param.service, '?') eq -1 ? '?' : '&'}ticket=${serviceTicketId}" /></p>
		</div>
	</table>
</tr>
<jsp:directive.include file="includes/bottom.jsp" />
