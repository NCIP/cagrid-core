package gov.nih.nci.cagrid.testing.system.utils.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

public class AntStep extends Step {
    public static String getAntCommand() {
        // ant home
        String ant = System.getenv("ANT_HOME");
        if (ant == null) {
            throw new IllegalArgumentException("ANT_HOME not set");
        }

        // ant home/bin
        if (!ant.endsWith(File.separator)) {
            ant += File.separator;
        }
        ant += "bin" + File.separator;

        // ant home/bin/ant
        if (isWindows()) {
            ant += "ant.bat";
        } else {
            ant += "ant";
        }

        if (!new File(ant).exists()) {
            throw new IllegalArgumentException(ant + " does not exist");
        }
        return ant;
    }

	@Override
	public void runStep() throws Throwable {
		// TODO Auto-generated method stub
		
	}
	
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    }

}
