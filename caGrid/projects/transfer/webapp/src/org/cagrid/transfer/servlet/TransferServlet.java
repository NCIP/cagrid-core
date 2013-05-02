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
package org.cagrid.transfer.servlet;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.stubs.TransferServiceContextResourceProperties;
import org.cagrid.transfer.descriptor.DataStorageDescriptor;
import org.globus.axis.gsi.GSIConstants;


public class TransferServlet extends HttpServlet {
    private static final Log logger = LogFactory.getLog(TransferServlet.class);

    private int blockSize = 1024; // default block size for transfer and receive


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Properties props = new Properties();
        logger.info("Calling Transfer Servlet PUT: " + getServletContext().getServerInfo() + getServletInfo());
        logger.info("Calling Transfer Servlet at: " + getServletContext().getRealPath("/"));

        // reload every time now so that it can be changed while
        // the container is running.....
        try {
            InputStream propsStream = this.getClass().getClassLoader().getResourceAsStream("server.properties");
            logger.debug("server.properties " + propsStream != null ? "found for reload" : "not found");
            props.load(propsStream);
            propsStream.close();
        } catch (FileNotFoundException e) {
            logger.error("Unable to load server.properties: " + e.getMessage(), e);
            e.printStackTrace();
            resp.sendError(500);
            return;
        }

        String myLocation = getServletContext().getRealPath("/").replace("\\", "/");
        String rootWebappLocation = myLocation.substring(0, myLocation.lastIndexOf("/"));
        rootWebappLocation = rootWebappLocation.substring(0, rootWebappLocation.lastIndexOf("/") + 1);
        logger.debug("Root webapp location determined to be " + rootWebappLocation);
        String persistenceDir = rootWebappLocation
            + props.getProperty("transfer.service.persistence.relative.location");
        logger.info("Storage data is stored in: " + persistenceDir);

        String configBlockSizeS = props.getProperty("transfer.service.block.size");
        if (configBlockSizeS != null) {
            try {
                logger.debug("Custom transfer block size found (" + configBlockSizeS + ")");
                int configBlockSize = Integer.parseInt(configBlockSizeS);
                blockSize = configBlockSize;
            } catch (NumberFormatException e) {
                logger.info("Service attribute block size not configured properly");
                resp.sendError(500);
                return;
            }
        }

        // 1 get the GSI attributes
        String userDN = (String) req.getAttribute(GSIConstants.GSI_USER_DN);
        logger.debug("User DN: " + userDN);
        
        // 2 get the requested ID
        String requestedID = req.getParameter("id");
        logger.debug("Requesting ID " + requestedID);
        if (requestedID == null || requestedID.length() <= 0) {
            logger.info("Not ID");
            resp.sendError(400);
            return;
        }

        // 3 authorize
        TransferServiceContextResourceProperties transferResourceProps = null;
        File resourcePropsFile = new File(persistenceDir, requestedID + ".xml");
        logger.debug("Loading transfer context resource properties from " + resourcePropsFile.getAbsolutePath());
        try {
            FileReader propsReader = new FileReader(resourcePropsFile);
            transferResourceProps = Utils.deserializeObject(propsReader, TransferServiceContextResourceProperties.class);
            propsReader.close();
        } catch (Exception e) {
            logger.info("Cannot find or deserialize the resource properties describing this transfer object: "
                + requestedID, e);
            e.printStackTrace();
            resp.sendError(500);
            return;
        }

        DataStorageDescriptor desc = transferResourceProps.getDataStorageDescriptor();
        // verify that the user calling is the owner or there is no owner
        if (desc.getUserDN() == null || desc.getUserDN().equals(userDN)) {
            logger.info("Storing data using block size of: " + blockSize);
            // 4 read the data from the request and write it
            logger.info("Data file storing is located at: " + desc.getLocation());
            File outFile = new File(desc.getLocation());
            if (outFile.exists()) {
                logger.info("File is already staged for resource: " + requestedID + " at file: " + desc.getLocation());
                resp.sendError(500);
                return;
            }

            FileOutputStream fos = new FileOutputStream(desc.getLocation());
            int len = -1;
            byte[] buffer = new byte[blockSize];
            while ((len = req.getInputStream().read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        } else {
            logger.info("Trouble storing data for requested object: " + requestedID + " at file: " + desc.getLocation());
            resp.sendError(403);
            return;
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Properties props = new Properties();
        logger.info("Calling Transfer Servlet PUT: " + getServletContext().getServerInfo() + getServletInfo());
        logger.info("Calling Transfer Servlet at: " + getServletContext().getRealPath("/"));

        // reload every time now so that it can be changed while
        // the container is running.....
        try {
            InputStream propsStream = this.getClass().getClassLoader().getResourceAsStream("server.properties");
            logger.debug("server.properties " + propsStream != null ? "found for reload" : "not found");
            props.load(propsStream);
            propsStream.close();
        } catch (FileNotFoundException e) {
            logger.error("Unable to load server.properties: " + e.getMessage(), e);
            e.printStackTrace();
            resp.sendError(500);
            return;
        }

        String myLocation = getServletContext().getRealPath("/").replace("\\", "/");
        String rootWebappLocation = myLocation.substring(0, myLocation.lastIndexOf("/"));
        rootWebappLocation = rootWebappLocation.substring(0, rootWebappLocation.lastIndexOf("/") + 1);
        logger.debug("Root webapp location determined to be " + rootWebappLocation);
        String persistenceDir = rootWebappLocation
            + props.getProperty("transfer.service.persistence.relative.location");
        logger.info("Storage data is stored in: " + persistenceDir);

        String configBlockSizeS = props.getProperty("transfer.service.block.size");
        if (configBlockSizeS != null) {
            try {
                logger.debug("Custom transfer block size found (" + configBlockSizeS + ")");
                int configBlockSize = Integer.parseInt(configBlockSizeS);
                blockSize = configBlockSize;
            } catch (NumberFormatException e) {
                logger.info("Service attribute block size not configured properly");
                resp.sendError(500);
                return;
            }
        }

        // 1 get the GSI attributes
        String userDN = (String) req.getAttribute(GSIConstants.GSI_USER_DN);
        logger.debug("User DN: " + userDN);
        
        // 2 get the requested ID
        String requestedID = req.getParameter("id");
        logger.debug("Requesting ID " + requestedID);
        if (requestedID == null || requestedID.length() <= 0) {
            logger.info("Not ID");
            resp.sendError(400);
            return;
        }

        // 3 authorize
        TransferServiceContextResourceProperties transferResourceProps = null;
        File resourcePropsFile = new File(persistenceDir, requestedID + ".xml");
        logger.debug("Loading transfer context resource properties from " + resourcePropsFile.getAbsolutePath());
        try {
            FileReader propsReader = new FileReader(resourcePropsFile);
            transferResourceProps = Utils.deserializeObject(propsReader, TransferServiceContextResourceProperties.class);
            propsReader.close();
        } catch (Exception e) {
            logger.info("Cannot find or deserialize the resource properties describing this transfer object: "
                + requestedID, e);
            e.printStackTrace();
            resp.sendError(500);
            return;
        }

        DataStorageDescriptor desc = transferResourceProps.getDataStorageDescriptor();
        // verify that the user calling is the owner or there is no owner
        if (desc.getUserDN() == null || desc.getUserDN().equals(userDN)) {
            logger.info("Transfering data using block size of: " + blockSize);
            try {
                // 4 write data to the response
                logger.info("Data file requested is located at: " + desc.getLocation());
                FileInputStream fis = new FileInputStream(desc.getLocation());
                ServletOutputStream os = resp.getOutputStream();
                int len = -1;
                byte[] buffer = new byte[blockSize];
                while (isStaging(desc)) {
                    // while it's staging, just keep trying to read.
                    while ((len = fis.read(buffer)) != -1) {
                        if (len > 0) {
                            os.write(buffer, 0, len);
                            os.flush();
                        }
                    }
                }
                // after done staging, read the rest of the file.
                while ((len = fis.read(buffer)) != -1) {
                    if (len > 0) {
                        os.write(buffer, 0, len);
                        os.flush();
                    }
                }
                fis.close();
            } catch (Exception e) {
                logger.info("Trouble retrieving data for requested object: " + requestedID + " at file: "
                    + desc.getLocation(), e);
                e.printStackTrace();
                resp.sendError(500);
                return;
            }
        } else {
            logger.info("Not authorized to recieve: " + requestedID + " at file: " + desc.getLocation());
            resp.sendError(403);
            return;
        }
    }


    private boolean isStaging(DataStorageDescriptor desc) {
        // the staging flag is set when the service creates the resource. 
        // so there should not be timing problems.

        // TCP: normally would need to reload the desc. here we cheat and use a
        // file system flag.
        // the flag is deleted when the staging is done so we don't need to
        // delete it explicitly.
        File flag = new File(desc.getLocation() + TransferServiceContextResource.STAGING_FLAG);
        return flag.exists();
    }
}
