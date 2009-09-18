/*
 * Created on Jul 31, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * This step sets the mysql username and password fields in dorian-conf.xml of a deployed Dorian service
 * @author Patrick McConnell
 */
public class DorianConfigureStep
	extends Step
{
	private GlobusHelper globus;
	private String user;
	private String password;
	
	public DorianConfigureStep(GlobusHelper globus)
	{
		this(globus,
			System.getProperty("mysql.user", "root"),
			System.getProperty("mysql.password", "")
		);
	}
	
	public DorianConfigureStep(GlobusHelper globus, String user, String password)
	{
		super();
		
		this.globus = globus;
		this.user = user;
		this.password = password;
	}

	public void runStep() throws Throwable
	{
		File configFile = new File(globus.getTempGlobusLocation(), 
			"etc" + File.separator + "cagrid_Dorian" + File.separator + "dorian.properties"
		);
		Properties props = new Properties();
		props.load(new FileInputStream(configFile));
		props.setProperty("gaards.dorian.db.user", user);
		props.setProperty("gaards.dorian.db.password", password);
		props.store(new FileOutputStream(configFile), "");
	}
}
