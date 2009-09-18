/*
 * Portions of this file Copyright 1999-2005 University of Chicago Portions of
 * this file Copyright 1999-2005 The University of Southern California. This
 * file or a portion of this file is licensed under the terms of the Globus
 * Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html. If you redistribute this
 * file, with or without modifications, you must include this notice in the
 * file.
 */
package gov.nih.nci.cagrid.introduce.servicetools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.encoding.ObjectDeserializationContext;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.xml.sax.InputSource;


/**
 * This helper is used to persist a {@link ReflectionResource
 * ReflectionResource} by serializing the resource implementation JavaBean used
 * in constructing the ReflectionResource. The result of storing the resource is
 * an XML file that corresponds exactly to the XML Schema Resource document
 * defined for this resource. Future versions will offer alternative modes of
 * persistence.
 * <p>
 * <p>
 * <p>
 * Usage:
 * <p>
 * Use this helper on the model of
 * {@link org.globus.wsrf.impl.PersistentReflectionResource
 * PersistentReflectionResource}
 * <p>
 * 
 * @see ReflectionResource
 * @see org.globus.wsrf.impl.PersistentReflectionResource
 * @see org.globus.wsrf.impl.ResourceHomeImpl
 * @see org.globus.wsrf.PersistentResource
 * @see org.globus.wsrf.RemoveCallback
 */
public class XmlPersistenceHelper extends FilePersistenceHelper implements PersistenceHelper {

    private static Log logger = LogFactory.getLog(XmlPersistenceHelper.class.getName());

    private static final String FILE_SUFFIX = ".xml";


    public XmlPersistenceHelper(Class beanClass, ServiceConfiguration configuration) throws IOException {
        super(beanClass, configuration, FILE_SUFFIX);

    }


    /*
     * (non-Javadoc)
     * 
     * @see gov.nih.nci.cagrid.introduce.servicetools.PersistanceHelper#load(java.lang.Object,
     *      gov.nih.nci.cagrid.introduce.servicetools.ReflectionResource)
     */
    public void load(Object key, ReflectionResource resource) throws ResourceException {
        logger.debug("Loading the resource from an XML file");

        File resourceFile = getKeyAsFile(resource.getClass(), key);
        if (!resourceFile.exists()) {
            logger.debug("backingFileNotFound");
            throw new NoSuchResourceException();
        }

        QName resourceElementQName;
        Object loadedResourceBean;

        FileInputStream in = null;
        try {
            in = new FileInputStream(resourceFile);

            ObjectDeserializationContext deserializer = new ObjectDeserializationContext(new InputSource(in),
                this.beanClass);

            deserializer.parse();

            loadedResourceBean = deserializer.getValue();
            resourceElementQName = deserializer.getQName();
        } catch (Exception e) {
            throw new ResourceException("resourceLoadFailed", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ee) {
                }
            }
        }

        resource.initialize(loadedResourceBean, resourceElementQName, key);
    }


    /*
     * (non-Javadoc)
     * 
     * @see gov.nih.nci.cagrid.introduce.servicetools.PersistanceHelper#store(gov.nih.nci.cagrid.introduce.servicetools.ReflectionResource)
     */
    public void store(ReflectionResource resource) throws ResourceException {
        store(resource.getID(), resource.getResourceBean(), resource.getResourcePropertySet().getName());
    }


    /*
     * (non-Javadoc)
     * 
     * @see gov.nih.nci.cagrid.introduce.servicetools.PersistanceHelper#remove(gov.nih.nci.cagrid.introduce.servicetools.ReflectionResource)
     */
    public void remove(ReflectionResource resource) throws ResourceException {
        Class resourceBean = resource.getResourceBean().getClass();
        if (!this.beanClass.isAssignableFrom(resourceBean)) {
            Object[] args = new Object[]{this.beanClass, resourceBean};
            throw new IllegalArgumentException("expectedType");
        }
        remove(resource.getClass(), resource.getID());
    }


    /**
     * Loads and returns the object of the given key from the persistent
     * storage.
     * 
     * @param key
     *            key of object to load.
     * @return loaded Object instance.
     * @throws ResourceException
     *             If the object cannot be loaded from file.
     */
    public Object load(Class clazz, Object key) throws ResourceException {
        logger.debug("Loading object by deserializing an XML file");

        File resourceFile = getKeyAsFile(clazz, key);
        if (!resourceFile.exists()) {
            logger.debug("backingFileNotFound");
            throw new NoSuchResourceException();
        }

        Object loadedBean;

        FileInputStream in = null;
        try {
            in = new FileInputStream(resourceFile);

            ObjectDeserializationContext deserializer = new ObjectDeserializationContext(new InputSource(in),
                this.beanClass);
            deserializer.parse();

            loadedBean = deserializer.getValue();
        } catch (Exception e) {
            throw new ResourceException("resourceLoadFailed");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ee) {
                }
            }
        }

        return loadedBean;
    }


    /**
     * Stores the object of the given key to persistent storage. <br>
     * <b>Note:</b> Calls to this function must be synchronized on per key
     * basis.
     * 
     * @param key
     *            key of object.
     * @param object
     *            object to persist.
     * @param topElementQName
     *            the top element name of the XML object.
     * @throws ResourceException
     *             If the object cannot be saved to a file.
     */
    public void store(Object key, Object object, QName topElementQName) throws ResourceException {
        if (!this.beanClass.isAssignableFrom(object.getClass())) {
            Object[] args = new Object[]{this.beanClass, (object == null) ? null : object.getClass()};
            throw new IllegalArgumentException("expectedType");
        }

        logger.debug("Storing object to an XML file");

        Writer writer = null;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("xph", ".tmp", getStorageDirectory());
            writer = new BufferedWriter(new FileWriter(tmpFile));

            ObjectSerializer.serialize(writer, object, topElementQName);
        } catch (Exception e) {
            if (tmpFile != null) {
                tmpFile.delete();
            }
            throw new ResourceException("resourceStoreFailed: " + e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ee) {
                }
            }
        }

        File file = getKeyAsFile(object.getClass(), key);
        if (file.exists()) {
            file.delete();
        }

        try {
            copyFile(tmpFile, file);
            tmpFile.delete();
        } catch (IOException e) {
            file.delete();
            throw new ResourceException("resourceStoreFailed: unable to write to " + file.getAbsolutePath());
        } finally {
            tmpFile.delete();
        }

    }


    public static void copyFile(File in, File out) throws IOException {
        // avoids copying a file to itself
        if (in.equals(out)) {
            return;
        }
        // ensure the output file location exists
        out.getCanonicalFile().getParentFile().mkdirs();

        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(in));
        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(out));

        // a temporary buffer to read into
        byte[] tmpBuffer = new byte[8192];
        int len = 0;
        while ((len = fis.read(tmpBuffer)) != -1) {
            // add the temp data to the output
            fos.write(tmpBuffer, 0, len);
        }
        // close the input stream
        fis.close();
        // close the output stream
        fos.flush();
        fos.close();
    }
}
