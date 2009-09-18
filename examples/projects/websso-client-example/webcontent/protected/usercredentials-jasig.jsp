<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

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
	 			<td class="dataCellText"><%=request.getSession().getAttribute("CAGRID_SSO_GRID_IDENTITY")%></td>
				<td class="dataCellText"><%=request.getSession().getAttribute("CAGRID_SSO_FIRST_NAME")%></td>
				<td class="dataCellText"><%=request.getSession().getAttribute("CAGRID_SSO_LAST_NAME")%></td>
				<td class="dataCellText"><%=request.getSession().getAttribute("CAGRID_SSO_DELEGATION_SERVICE_EPR")%></td>
				<td class="dataCellText"><%=request.getSession().getAttribute("CAGRID_SSO_EMAIL_ID")%></td>
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
