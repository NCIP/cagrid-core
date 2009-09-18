/*-----------------------------------------------------------------------------
 * Copyright (c) 2003-2004, The Ohio State University,
 * Department of Biomedical Informatics, Multiscale Computing Laboratory
 * All rights reserved.
 * 
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3  All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement: This product includes
 *    material developed by the Mobius Project (http://www.projectmobius.org/).
 * 
 * 4. Neither the name of the Ohio State University, Department of Biomedical
 *    Informatics, Multiscale Computing Laboratory nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * 5. Products derived from this Software may not be called "Mobius"
 *    nor may "Mobius" appear in their names without prior written
 *    permission of Ohio State University, Department of Biomedical
 *    Informatics, Multiscale Computing Laboratory
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *---------------------------------------------------------------------------*/

package gov.nih.nci.cagrid.gts.service;

import org.jdom.Element;
import org.projectmobius.common.AbstractMobiusConfiguration;
import org.projectmobius.common.MobiusException;
import org.projectmobius.common.MobiusResourceManager;
import org.projectmobius.db.ConnectionManager;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class Configuration implements AbstractMobiusConfiguration {

	public static final String RESOURCE = "GTSConfiguration";

	public static final String DATABASE = "database";

	public static final String GTS_ID = "gts-internal-id";

	public static final String SYNC_AUTHORITIES = "sync-authorities";

	private ConnectionManager rootConnectionManager;

	private String gtsInternalId;

	private AuthoritySyncTime syncTime;


	public void parse(MobiusResourceManager resourceManager, Element config) throws MobiusException {
		Element rootDatabaseConfig = config.getChild(DATABASE);
		if (rootDatabaseConfig != null) {
			this.rootConnectionManager = new ConnectionManager(rootDatabaseConfig);
		} else {
			throw new MobiusException("No database defined in the GTS Configuration.");
		}
		this.gtsInternalId = config.getChildText(GTS_ID);
		if (gtsInternalId == null) {
			throw new MobiusException("No internal id specified.");
		}

		Element sync = config.getChild(SYNC_AUTHORITIES, config.getNamespace());
		if (sync != null) {
			String shours = sync.getAttributeValue("hours");
			if (shours == null) {
				throw new MobiusException("In the " + SYNC_AUTHORITIES + " configuration element, no hours specified.");
			}

			int hours = 0;
			try {
				hours = Integer.valueOf(shours).intValue();
			} catch (Exception e) {
				throw new MobiusException("In the " + SYNC_AUTHORITIES
					+ " configuration element, hours must be specified as an integer.");
			}

			String sminutes = sync.getAttributeValue("minutes");
			if (sminutes == null) {
				throw new MobiusException("In the " + SYNC_AUTHORITIES
					+ " configuration element, no minutes specified.");
			}

			int minutes = 0;
			try {
				minutes = Integer.valueOf(sminutes).intValue();
			} catch (Exception e) {
				throw new MobiusException("In the " + SYNC_AUTHORITIES
					+ " configuration element, minutes must be specified as an integer.");
			}

			String sseconds = sync.getAttributeValue("seconds");
			if (sseconds == null) {
				throw new MobiusException("In the " + SYNC_AUTHORITIES
					+ " configuration element, no seconds specified.");
			}

			int seconds = 0;
			try {
				seconds = Integer.valueOf(sseconds).intValue();
			} catch (Exception e) {
				throw new MobiusException("In the " + SYNC_AUTHORITIES
					+ " configuration element, seconds must be specified as an integer.");
			}
			syncTime = new AuthoritySyncTime(hours, minutes, seconds);
		} else {
			throw new MobiusException("No " + SYNC_AUTHORITIES + " configuration element specified.");
		}
	}


	public String getGTSInternalId() {
		return gtsInternalId;
	}


	/**
	 * @return Returns the rootConnectionManager.
	 */
	public ConnectionManager getConnectionManager() {
		return rootConnectionManager;
	}


	public AuthoritySyncTime getAuthoritySyncTime() {
		return syncTime;
	}

}
