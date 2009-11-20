package org.cagrid.websso.common;

import java.io.InputStream;
import java.io.InputStreamReader;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.support.ServletContextResource;

public class StartSyncGTSServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			String isSyncGtsAuto = config.getInitParameter("start-auto-syncgts");
			if (isSyncGtsAuto.equals("true")) {

				ServletContextResource contextResource=new ServletContextResource(config.getServletContext(), "/WEB-INF/sync-description.xml");
				InputStream inputStream = contextResource.getInputStream();
				final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				SyncDescription description = (SyncDescription) Utils.deserializeObject(inputStreamReader,SyncDescription.class);
				inputStreamReader.close();
				SyncGTS.getInstance().syncAndResyncInBackground(description, false);
			}
    	} catch (Exception e) {
			throw new ServletException("Unable to Start Sync GTS Service." + e);
		}
		super.init(config);
	}
}
