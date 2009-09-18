<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="org.acegisecurity.context.SecurityContextHolder"%>
<%@page import="org.cagrid.websso.client.acegi.WebSSOUser;"%><html>
<head>
<title>Content</title>
<link rel="stylesheet" type="text/css" href="../css/styleSheet.css" />
<script src="../js/script.js" type="text/javascript"></script>
</head>
<body>
<table summary="" cellpadding="0" cellspacing="0" border="0"
	width="100%" height="100%">

	<!-- nci hdr begins -->
	<tr>
		<td>
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="hdrBG">
			<tr>
				<td width="283" height="37" align="left"><a
					href="http://www.cancer.gov"><img
					alt="National Cancer Institute" src="../images/logotype.gif"
					width="283" height="37" border="0"></a></td>
				<td>&nbsp;</td>
				<td width="295" height="37" align="right"><a
					href="http://www.cancer.gov"><img
					alt="U.S. National Institutes of Health | www.cancer.gov"
					src="../images/tagline.gif" width="295" height="37" border="0"></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<!-- nci hdr ends -->

	<tr>
		<td height="100%" align="center" valign="top">
		<table summary="" cellpadding="0" cellspacing="0" border="0"
			height="100%" width="771">
			<!-- application hdr begins -->
			<tr>
				<td height="50">
				<table width="100%" height="50" border="0" cellspacing="0"
					cellpadding="0" class="subhdrBG">
					<tr>
						<td height="50" align="left"><a href="#"><img src="../images/appLogo.gif" alt="Application Logo" hspace="10" border="0"></a></td>
					</tr>
					<tr>
						<% WebSSOUser webSSOUser=(WebSSOUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
						   String firstName=webSSOUser.getFirstName();
						   String lastName=webSSOUser.getLastName();
						%> 
						<td class="welcomeContent" height="2" valign="top" align="right">
							<label class="h3">Login User:</label><font class="h3"><%=firstName+" "+lastName %></font>
                        </td>
					</tr>
					<tr>
						<td class="welcomeContent" height="2" valign="top" align="right"> 
							<a href="<%= request.getContextPath() %>/logout"><font class="h3">logout</font></a>
                        </td>
					</tr>
				</table>
				</td>
			</tr>
			<!-- application hdr ends -->
			<tr>
				<td valign="top">
				<table summary="" cellpadding="0" cellspacing="0" border="0" height="100%" width="100%">
				 <tr>
		          <td valign="top">
		            <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%" width="100%">
		             <tr>
		                <td height="20" class="mainMenu">
		                
		                  <!-- main menu begins -->
		                  <table summary="" cellpadding="0" cellspacing="0" border="0" height="20">
		                    <tr>
		                      <td width="1"><!-- anchor to skip main menu --><a href="#content"><img src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" /></a></td>
							  <td height="20" class="mainMenuItemOver" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItemOver'),hideCursor()" onclick="document.location.href='index.jsp'">
		                        <a class="mainMenuLink" href="<%= request.getContextPath()+"/protected" %>">PROTECTED AREA</a>
		                      </td>
		                      <!-- link 1 ends -->
		                      <td><img src="../images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
		                      
		                      <td width="1"><!-- anchor to skip main menu --><a href="#content"><img src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" /></a></td>
							  <td height="20" class="mainMenuItemOver" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItemOver'),hideCursor()" onclick="document.location.href='../index.jsp'">
		                        <a class="mainMenuLink" href="<%= request.getContextPath() %>">PUBLIC AREA</a>
		                      </td>
		                      <!-- link 1 ends -->
		                      <td><img src="../images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
		                    </tr>
		                  </table>
		                  <!-- main menu ends -->		                 
		         	    </td>
		        	</tr>

					<!--_____ main content begins _____-->
					<tr>
						<td valign="top"><!-- target of anchor to skip menus --><a
							name="content" />
						<table summary="" cellpadding="0" cellspacing="0" border="0"
							class="contentPage" width="100%" height="100%">
							<tr>
								<td valign="top">
								<table cellpadding="0" cellspacing="0" border="0" class="contentBegins">
									<jsp:include page="usercredentials.jsp" flush="true" />
								</table>
								</td>
							</tr>
						</table></td>
					</tr>
					<!--_____ main content ends _____-->

					<tr>
						<td height="20" width="100%" class="footerMenu"><!-- application ftr begins -->
						<table summary="" cellpadding="0" cellspacing="0" border="0"
							width="100%">
							<tr>
								<td align="center" height="20" class="footerMenuItem"
									onmouseover="changeMenuStyle(this,'footerMenuItemOver'),showCursor()"
									onmouseout="changeMenuStyle(this,'footerMenuItem'),hideCursor()"
									onclick="document.location.href='#'">&nbsp;&nbsp;<a
									class="footerMenuLink" href="#">CONTACT US</a>&nbsp;&nbsp;</td>
								<td><img src="../images/ftrMenuSeparator.gif" width="1"
									height="16" alt="" /></td>
								<td align="center" height="20" class="footerMenuItem"
									onmouseover="changeMenuStyle(this,'footerMenuItemOver'),showCursor()"
									onmouseout="changeMenuStyle(this,'footerMenuItem'),hideCursor()"
									onclick="document.location.href='#'">&nbsp;&nbsp;<a
									class="footerMenuLink" href="#">PRIVACY NOTICE</a>&nbsp;&nbsp;
								</td>
								<td><img src="../images/ftrMenuSeparator.gif" width="1"
									height="16" alt="" /></td>
								<td align="center" height="20" class="footerMenuItem"
									onmouseover="changeMenuStyle(this,'footerMenuItemOver'),showCursor()"
									onmouseout="changeMenuStyle(this,'footerMenuItem'),hideCursor()"
									onclick="document.location.href='#'">&nbsp;&nbsp;<a
									class="footerMenuLink" href="#">DISCLAIMER</a>&nbsp;&nbsp;</td>
								<td><img src="../images/ftrMenuSeparator.gif" width="1"
									height="16" alt="" /></td>
								<td align="center" height="20" class="footerMenuItem"
									onmouseover="changeMenuStyle(this,'footerMenuItemOver'),showCursor()"
									onmouseout="changeMenuStyle(this,'footerMenuItem'),hideCursor()"
									onclick="document.location.href='#'">&nbsp;&nbsp;<a
									class="footerMenuLink" href="#">ACCESSIBILITY</a>&nbsp;&nbsp;</td>
								<td><img src="es/ftrMenuSeparator.gif" width="1"
									height="16" alt="" /></td>
								<td align="center" height="20" class="footerMenuItem"
									onmouseover="changeMenuStyle(this,'footerMenuItemOver'),showCursor()"
									onmouseout="changeMenuStyle(this,'footerMenuItem'),hideCursor()"
									onclick="document.location.href='#'">&nbsp;&nbsp;<a
									class="footerMenuLink" href="#">APPLICATION SUPPORT</a>&nbsp;&nbsp;
								</td>
							</tr>
						</table>
						<!-- application ftr ends --></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td><!-- footer begins -->
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="ftrTable">
			<tr>
				<td valign="top">
				<div align="center"><a href="http://www.cancer.gov/"><img
					src="../images/footer_nci.gif" width="63" height="31"
					alt="National Cancer Institute" border="0"></a> <a
					href="http://www.dhhs.gov/"><img src="../images/footer_hhs.gif"
					width="39" height="31"
					alt="Department of Health and Human Services" border="0"></a> <a
					href="http://www.nih.gov/"><img src="../images/footer_nih.gif"
					width="46" height="31" alt="National Institutes of Health"
					border="0"></a> <a href="http://www.firstgov.gov/"><img
					src="../images/footer_firstgov.gif" width="91" height="31"
					alt="FirstGov.gov" border="0"></a></div>
				</td>
			</tr>
		</table>
		<!-- footer ends --></td>
	</tr>
</table>
</body>
</html>
