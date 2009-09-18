<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<div class="header">
<%
	if (request.getRemoteUser() != null) {
		out.println("<div class=\"authenticated\">");
		out.println("User: " + request.getRemoteUser());
		//Logout not yet implemented!
		out.println("&nbsp;&nbsp;");
		out.println("<a href=\"logout\">LOGOUT</a>");
		out.println("</div>");
	} else {
		out.println("Not logged in.");
	}
%>
</div>

