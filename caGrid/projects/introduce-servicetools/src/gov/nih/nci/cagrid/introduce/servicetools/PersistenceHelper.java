package gov.nih.nci.cagrid.introduce.servicetools;

import java.util.List;

import javax.xml.namespace.QName;

import org.globus.wsrf.ResourceException;


public interface PersistenceHelper {
    
    public abstract List list() throws Exception;

    /**
     * Loads and initialize the resource.
     *
     * @param key the key of the potentially new resource
     * @param resource the new resource to load.
     *        Its key and implementation bean are null.
     *        They will be set by a call from this method to
     *        resource.initialize().
     * @see org.globus.wsrf.PersistentResource
     */
    public abstract void load(Object key, ReflectionResource resource) throws ResourceException;
    
    
    /**
     * Loads and returns the object of the given key from the persistent
     * storage.
     *
     * @param key key of object to load.
     * @return loaded Object instance.
     * @throws ResourceException If the object cannot be loaded from file.
     */
    public abstract Object load(Class clazz, Object key) throws ResourceException ;

    /**
     * Store the resource into an XML document (current implementation).
     * The name of the file is governed by {@link #getKeyAsFile(Object)
     * getKeyAsFile()}.
     *
     * This stores the state of the implementation JavaBean. If some resource
     * properties have been implemented with something else (for instance
     * getters and setters from another object) they will not be
     * persisted with the current state. This is not a problem if their state
     * is immutable after initial creation, as their values will be set by
     * {@link ReflectionResource#initialize(Object, QName, Object) initialize},
     * ReflectionResource.initialize()} which is called by
     * this method.
     * (TODO: persist based on each RP?)
     *
     * @param resource the resource to store the state of.
     * @throws ResourceException if the resource could not be stored
     * @see #getKeyAsFile(Object) getKeyAsFile()
     * @see org.globus.wsrf.PersistentResource
     */
    public abstract void store(ReflectionResource resource) throws ResourceException;


    /**
     * Removes the resource from persistent storage.
     *
     * @param resource the resource to remove from storage.
     * @throws ResourceException if the resource could not be removed.
     * @see org.globus.wsrf.RemoveCallback
     */
    public abstract void remove(ReflectionResource resource) throws ResourceException;
    
    public abstract void removeAll() throws Exception;

}