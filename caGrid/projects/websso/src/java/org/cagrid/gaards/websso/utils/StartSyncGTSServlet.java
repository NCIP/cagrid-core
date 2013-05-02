/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.websso.utils;

import java.io.InputStreamReader;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.cagrid.gaards.websso.beans.WebSSOServerInformation;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class StartSyncGTSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException{
		try{
			WebApplicationContext ctx =
				WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
			WebSSOProperties webSSOProperties = (WebSSOProperties)ctx.getBean(WebSSOConstants.WEBSSO_PROPERTIES);
			WebSSOServerInformation webSSOServerInformation = webSSOProperties.getWebSSOServerInformation();

			if ("yes".equalsIgnoreCase(webSSOServerInformation.getStartAutoSyncGTS())){
				Resource syncdesc = (Resource)ctx.getBean(WebSSOConstants.SYNC_DESC_RESOURCE);
				//Load Sync Description
				final InputStreamReader inputStreamReader = new InputStreamReader(syncdesc.getInputStream());
				SyncDescription description = Utils.deserializeObject(inputStreamReader,SyncDescription.class);
				inputStreamReader.close();
				// Sync with the Trust Fabric Once
				SyncGTS.getInstance().syncAndResyncInBackground(description, false);
			}
		} catch (Exception e) {
			throw new ServletException("Unable to Start Sync GTS Service: " + e.getMessage(), e);
		}
		super.init(config);
	}
}
