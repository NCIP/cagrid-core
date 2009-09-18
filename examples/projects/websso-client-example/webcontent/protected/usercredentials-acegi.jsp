<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="org.acegisecurity.context.SecurityContextHolder"%>
<%@page import="org.acegisecurity.Authentication"%>
<%@page import="org.cagrid.websso.client.acegi.WebSSOUser;"%>
<table summary="" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td class="dataTablePrimaryLabel" height="20">Single Sign On User Data</td>
	</tr>

	<tr>
		<td>
		<table summary="Enter summary of data here" cellpadding="3"
			cellspacing="0" border="0" class="dataTable" width="100%">
			<tr>
				<th class="dataTableHeader" scope="col" align="center">Grid
				Identity</th>
				<th class="dataTableHeader" scope="col" align="center">FirstName
				</th>
				<th class="dataTableHeader" scope="col" align="center">LastName
				</th>
				<th class="dataTableHeader" scope="col" align="center">Delegation
				Service EPR</th>
				<th class="dataTableHeader" scope="col" align="center">Email Id
				</th>
			</tr>
			<tr class="dataRowLight">
				<% WebSSOUser webSSOUser=(WebSSOUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				   if(webSSOUser!=null){			
				%>
				<td class="dataCellText"><%=webSSOUser.getGridId()%></td>
				<td class="dataCellText"><%=webSSOUser.getFirstName()%></td>
				<td class="dataCellText"><%=webSSOUser.getLastName()%></td>
				<td class="dataCellText"><%=webSSOUser.getDelegatedEPR()%></td>
				<td class="dataCellText"><%=webSSOUser.getEmailId()%></td>
				<% } %>
			</tr>
		</table>
		</td>
	</tr>

	<tr>
		<td height="40"></td>
	</tr>

	<tr>
		<td class="dataTablePrimaryLabel" height="20">Sample Application
		Server Information</td>
	</tr>
	
	</tr>

	<tr>
		<td>
		<table summary="Enter summary of data here" cellpadding="3"
			cellspacing="0" border="0" class="dataTable" width="100%">
			<tr>
				<th class="dataTableHeader" scope="col" align="center">Server Name</th>
				<th class="dataTableHeader" scope="col" align="center">Server IP Address</th>
				<th class="dataTableHeader" scope="col" align="center">Server Port</th>
			</tr>
			<tr class="dataRowLight">
				<td class="dataCellText"><%=request.getServerName()%></td>
				<td class="dataCellText"><%=request.getRemoteAddr()%></td>
				<td class="dataCellText"><%=request.getServerPort()%></td>
			</tr>
		</table>
		</td>
	</tr>
