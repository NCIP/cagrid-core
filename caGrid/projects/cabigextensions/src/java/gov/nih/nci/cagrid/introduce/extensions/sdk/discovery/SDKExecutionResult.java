package gov.nih.nci.cagrid.introduce.extensions.sdk.discovery;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * SDKExecutionResult Provides access to the results of a run of SDKExecutor
 * 
 * @author oster
 * @created Jun 14, 2007 4:09:58 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class SDKExecutionResult {

    /**
     * Comment for <code>SRC_DIR</code>
     */
    private static final String SRC_DIR = "src";

    private static final String OUTPUT_DIR = "output";

    protected File sdkDir;
    protected SDKGenerationInformation info;

    protected static Log LOG = LogFactory.getLog(SDKExecutionResult.class.getName());


    public SDKExecutionResult(File sdkDir, SDKGenerationInformation info) {
        this.sdkDir = sdkDir;
        this.info = info;
    }


    /**
     * @return true iff the results can be considered valid
     * @throws SDKExecutionException
     *             if results were not valid
     */
    public void validate() throws SDKExecutionException {
        File output = getOutputDirectory();
        boolean dirValid = output.exists() && output.canRead();
        if (!dirValid) {
            throw new SDKExecutionException("Expected result directory was not readible:" + output.getAbsolutePath());
        }
    }


    protected File getOutputDirectory() {
        return new File(this.sdkDir, OUTPUT_DIR + File.separator + this.info.getProjectName());
    }


    public void destroy() {
        if (this.sdkDir != null) {
            LOG.debug("Cleaning up working directory:" + this.sdkDir.getAbsolutePath());
            try {
                FileUtils.forceDelete(this.sdkDir);
            } catch (IOException e) {
                LOG.error("Problem cleaning up working directory:" + this.sdkDir.getAbsolutePath(), e);
            }
        } else {
            LOG.debug("Working directory was null!");
        }

    }


    public Collection<File> getGeneratedXMLSchemas() {
        File schemaDir = new File(getOutputDirectory(), SRC_DIR);
        return FileUtils.listFiles(schemaDir, new String[]{"xsd"}, false);

    }
}
