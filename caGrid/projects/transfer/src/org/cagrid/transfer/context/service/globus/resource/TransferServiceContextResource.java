package org.cagrid.transfer.context.service.globus.resource;

import gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.namespace.QName;

import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.cagrid.transfer.descriptor.DataStorageDescriptor;
import org.cagrid.transfer.descriptor.Status;
import org.cagrid.transfer.service.TransferServiceConfiguration;
import org.globus.wsrf.ResourceException;


/**
 * The implementation of this TransferServiceContextResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TransferServiceContextResource extends TransferServiceContextResourceBase {

    public static final String STAGING_FLAG = ".staging";
    private DataStagedCallback callback = null;
    private boolean shouldDeleteFileOnDestroyDefault = true;


    @Override
    public void initialize(Object resourceBean, QName resourceElementQName, Object id) throws ResourceException {
        super.initialize(resourceBean, resourceElementQName, id);
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        // default termination time is 30 minutes.
        this.setTerminationTime(cal);
    }


    public DataStagedCallback getDataStagedCallback() {
        return this.callback;
    }


    public void stage(DataDescriptor dd, DataStagedCallback callback) throws Exception {
        this.callback = callback;

        File storageFile = new File(getStorageDirectory().getAbsolutePath() + File.separator + (String) getID()
            + ".cache");
        DataStorageDescriptor desc = new DataStorageDescriptor();
        desc.setLocation(storageFile.getAbsolutePath());
        if (SecurityUtils.getCallerIdentity() != null) {
            desc.setUserDN(SecurityUtils.getCallerIdentity());
        }
        desc.setDataDescriptor(dd);
        desc.setStatus(Status.Staging);
        desc.setDeleteOnDestroy(shouldDeleteFileOnDestroyDefault);
        setDataStorageDescriptor(desc);
    }


    public void stage(final byte[] data, final DataDescriptor dd) throws Exception {

        DataStorageDescriptor desc = new DataStorageDescriptor();
        final File storageFile = new File(getStorageDirectory().getAbsolutePath() + File.separator + (String) getID()
            + ".cache");
        desc.setLocation(storageFile.getAbsolutePath());
        if (SecurityUtils.getCallerIdentity() != null) {
            desc.setUserDN(SecurityUtils.getCallerIdentity());
        }
        desc.setDataDescriptor(dd);
        desc.setStatus(Status.Staging);
        desc.setDeleteOnDestroy(shouldDeleteFileOnDestroyDefault);
        setDataStorageDescriptor(desc);
        final File stageFlag = new File(desc.getLocation() + STAGING_FLAG);
        stageFlag.createNewFile();

        // TCP: create a thread to do the actual writing. leaving the staging
        // flags
        // outside the thread ensures that when "stage" is called the flags
        // are created and thus when the resource is created the staging flag is
        // set.
        final DataStorageDescriptor fdesc = desc;
        new Thread() {
            @Override
            public void run() {
                try {
                    FileOutputStream fw = new FileOutputStream(storageFile);
                    fw.write(data);
                    fw.close();

                    fdesc.setStatus(Status.Staged);
                    setDataStorageDescriptor(fdesc);
                    stageFlag.delete();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();

    }


    public void stage(final InputStream is, final DataDescriptor dd) throws Exception {

        DataStorageDescriptor desc = new DataStorageDescriptor();
        final File storageFile = new File(getStorageDirectory().getAbsolutePath() + File.separator + (String) getID()
            + ".cache");
        desc.setLocation(storageFile.getAbsolutePath());
        if (SecurityUtils.getCallerIdentity() != null) {
            desc.setUserDN(SecurityUtils.getCallerIdentity());
        }
        desc.setDataDescriptor(dd);
        desc.setStatus(Status.Staging);
        desc.setDeleteOnDestroy(shouldDeleteFileOnDestroyDefault);
        setDataStorageDescriptor(desc);
        final File stageFlag = new File(desc.getLocation() + STAGING_FLAG);
        stageFlag.createNewFile();

        // TCP: create a thread to do the actual writing. leaving the staging
        // flags
        // outside the thread ensures that when "stage" is called the flags
        // are created and thus when the resource is created the staging flag is
        // set.
        final DataStorageDescriptor fdesc = desc;
        new Thread() {
            @Override
            public void run() {
                try {
                    FileOutputStream fw = new FileOutputStream(storageFile);
                    byte[] data = new byte[1024];
                    int length;
                    while ((length = is.read(data)) != -1) {
                        if (length > 0) {
                            fw.write(data, 0, length);
                            fw.flush();
                        }
                    }
                    fw.close();

                    fdesc.setStatus(Status.Staged);
                    setDataStorageDescriptor(fdesc);
                    stageFlag.delete();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
        
    }


    public void stage(File file, DataDescriptor dd, boolean shouldDeleteFileOnDestroy) throws Exception {
        DataStorageDescriptor desc = new DataStorageDescriptor();
        desc.setLocation(file.getAbsolutePath());
        if (SecurityUtils.getCallerIdentity() != null) {
            desc.setUserDN(SecurityUtils.getCallerIdentity());
        }

        desc.setDataDescriptor(dd);
        desc.setStatus(Status.Staged);
        desc.setDeleteOnDestroy(shouldDeleteFileOnDestroy);
        setDataStorageDescriptor(desc);
    }


    private void removeDataFile() throws Exception {
        if (getDataStorageDescriptor() != null && getDataStorageDescriptor().getLocation() != null) {
            String location = getDataStorageDescriptor().getLocation();
            if (getDataStorageDescriptor().isDeleteOnDestroy()) {
                File dataFile = new File(location);
                boolean deleted = dataFile.delete();
                if (!deleted) {
                    throw new Exception("Cound not delete file on destroy of resource: " + location);
                }
            }
        }
    }


    private File getStorageDirectory() throws Exception {
        String storageDirS = TransferServiceConfiguration.getConfiguration().getStorageDirectory();
        File storageDir = new File(storageDirS);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir;
    }


    @Override
    public void remove() throws ResourceException {
        super.remove();
        try {
            removeDataFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
