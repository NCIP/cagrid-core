package org.cagrid.transfer.context.service.globus.resource;

import gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.service.helper.PersistentTransferCallback;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.cagrid.transfer.descriptor.DataStorageDescriptor;
import org.cagrid.transfer.descriptor.Status;
import org.cagrid.transfer.service.TransferServiceConfiguration;
import org.globus.wsrf.Constants;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SimpleResourceKey;

/**
 * The implementation of this TransferServiceContextResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TransferServiceContextResource extends
		TransferServiceContextResourceBase {

	public static final String STAGING_FLAG = ".staging";
	private DataStagedCallback callback = null;
	private boolean shouldDeleteFileOnDestroyDefault = true;

	@Override
	public void initialize(Object resourceBean, QName resourceElementQName,
			Object id) throws ResourceException {
		super.initialize(resourceBean, resourceElementQName, id);
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MINUTE, 30);
		// default termination time is 30 minutes.
		this.setTerminationTime(cal);
	}

	@Override
	public void loadResource(ResourceKey resourceKey, ObjectInputStream ois)
			throws Exception {
		super.loadResource(resourceKey, ois);
		hookupCallBack();
	}

	private void hookupCallBack() throws Exception {
		DataStorageDescriptor desc = getDataStorageDescriptor();
		if (desc.getCreatorResourceHomeJNDI() != null
				&& desc.getCreatorResourceHomeJNDI().length() > 0) {
			javax.naming.Context initialContext = new InitialContext();
			ResourceHome resourceHome = (ResourceHome) initialContext
					.lookup(desc.getCreatorResourceHomeJNDI());
			ResourceKey key = null;
			if (desc.getCreatorResourceKeyName() != null
					&& desc.getCreatorResourceKeyName().length() > 0
					&& desc.getCreatorResourceKeyValue() != null
					&& desc.getCreatorResourceKeyValue().length() > 0) {
				key = new SimpleResourceKey(QName.valueOf(desc
						.getCreatorResourceKeyName()), desc
						.getCreatorResourceKeyValue());
			}
			Resource resource = resourceHome.find(key);
			if (resource instanceof PersistentTransferCallback) {
				String homeName = "java:comp/env/services/cagrid/TransferServiceContext/home";
				org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome thome = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome) initialContext
						.lookup(homeName);

				this
						.setDataStagedCallback(((PersistentTransferCallback) resource)
								.getCallback(thome.getResourceReference(this
										.getResourceKey())));
			}
		}
	}

	public DataStagedCallback getDataStagedCallback() {
		return this.callback;
	}

	public void setDataStagedCallback(DataStagedCallback callback) {
		this.callback = callback;
	}

	public void stage(DataDescriptor dd, DataStagedCallback callback)
			throws Exception {
		setDataStagedCallback(callback);

		File storageFile = new File(getStorageDirectory().getAbsolutePath()
				+ File.separator + (String) getID() + ".cache");
		DataStorageDescriptor desc = new DataStorageDescriptor();

		try {
			// get the resource home of the caller
			MessageContext ctx = MessageContext.getCurrentContext();
			String servicePath = ctx.getTargetService();
			String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath
					+ "/home";
			desc.setCreatorResourceHomeJNDI(jndiName);
			ResourceKey key = ResourceContext.getResourceContext()
					.getResourceKey();
			if (key != null) {
				desc.setCreatorResourceKeyName(key.getName().toString());
				desc.setCreatorResourceKeyValue((String) key.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		desc.setLocation(storageFile.getAbsolutePath());
		if (SecurityUtils.getCallerIdentity() != null) {
			desc.setUserDN(SecurityUtils.getCallerIdentity());
		}
		desc.setDataDescriptor(dd);
		desc.setStatus(Status.Staging);
		desc.setDeleteOnDestroy(shouldDeleteFileOnDestroyDefault);
		setDataStorageDescriptor(desc);
	}

	public void stage(final byte[] data, final DataDescriptor dd)
			throws Exception {

		DataStorageDescriptor desc = new DataStorageDescriptor();
		final File storageFile = new File(getStorageDirectory()
				.getAbsolutePath()
				+ File.separator + (String) getID() + ".cache");
		desc.setLocation(storageFile.getAbsolutePath());
		if (SecurityUtils.getCallerIdentity() != null) {
			desc.setUserDN(SecurityUtils.getCallerIdentity());
		}

		try {
			// get the resource home of the caller
			MessageContext ctx = MessageContext.getCurrentContext();
			String servicePath = ctx.getTargetService();
			String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath
					+ "/home";
			desc.setCreatorResourceHomeJNDI(jndiName);
			ResourceKey key = ResourceContext.getResourceContext()
					.getResourceKey();
			if (key != null) {
				desc.setCreatorResourceKeyName(key.getName().toString());
				desc.setCreatorResourceKeyValue((String) key.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
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

					setStatus(Status.Staged);

					stageFlag.delete();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		}.start();

	}

	public void stage(final InputStream is, final DataDescriptor dd)
			throws Exception {

		DataStorageDescriptor desc = new DataStorageDescriptor();
		final File storageFile = new File(getStorageDirectory()
				.getAbsolutePath()
				+ File.separator + (String) getID() + ".cache");
		desc.setLocation(storageFile.getAbsolutePath());
		if (SecurityUtils.getCallerIdentity() != null) {
			desc.setUserDN(SecurityUtils.getCallerIdentity());
		}

		try {
			// get the resource home of the caller
			MessageContext ctx = MessageContext.getCurrentContext();
			String servicePath = ctx.getTargetService();
			String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath
					+ "/home";
			desc.setCreatorResourceHomeJNDI(jndiName);
			ResourceKey key = ResourceContext.getResourceContext()
					.getResourceKey();
			if (key != null) {
				desc.setCreatorResourceKeyName(key.getName().toString());
				desc.setCreatorResourceKeyValue((String) key.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
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

					setStatus(Status.Staged);

					stageFlag.delete();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		}.start();

	}

	public void stage(File file, DataDescriptor dd,
			boolean shouldDeleteFileOnDestroy) throws Exception {
		DataStorageDescriptor desc = new DataStorageDescriptor();
		desc.setLocation(file.getAbsolutePath());
		if (SecurityUtils.getCallerIdentity() != null) {
			desc.setUserDN(SecurityUtils.getCallerIdentity());
		}

		try {
			// get the resource home of the caller
			MessageContext ctx = MessageContext.getCurrentContext();
			String servicePath = ctx.getTargetService();
			String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath
					+ "/home";
			desc.setCreatorResourceHomeJNDI(jndiName);
			ResourceKey key = ResourceContext.getResourceContext()
					.getResourceKey();
			if (key != null) {
				desc.setCreatorResourceKeyName(key.getName().toString());
				desc.setCreatorResourceKeyValue((String) key.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		desc.setDataDescriptor(dd);
		desc.setDeleteOnDestroy(shouldDeleteFileOnDestroy);
		setDataStorageDescriptor(desc);
		setStatus(Status.Staged);
	}

	private void removeDataFile() throws Exception {
		if (getDataStorageDescriptor() != null
				&& getDataStorageDescriptor().getLocation() != null) {
			String location = getDataStorageDescriptor().getLocation();
			if (getDataStorageDescriptor().isDeleteOnDestroy()) {
				File dataFile = new File(location);
				boolean deleted = dataFile.delete();
				if (!deleted) {
					throw new Exception(
							"Cound not delete file on destroy of resource: "
									+ location);
				}
			}
		}
	}

	private File getStorageDirectory() throws Exception {
		String storageDirS = TransferServiceConfiguration.getConfiguration()
				.getStorageDirectory();
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

	public void setStatus(org.cagrid.transfer.descriptor.Status status)
			throws ResourceException {
		DataStorageDescriptor desc = getDataStorageDescriptor();
		desc.setStatus(status);
		setDataStorageDescriptor(desc);

		if (status.equals(Status.Staged)) {
			DataStagedCallback callback = getDataStagedCallback();
			if (callback == null) {
				// try to refresh
				try {
					hookupCallBack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			final DataStagedCallback fcallback = callback;
			Thread th = new Thread(new Runnable() {
				public void run() {

					if (fcallback != null) {
						fcallback
								.dataStaged(TransferServiceContextResource.this);
					}
				}
			});
			th.start();
		}
	}

}
