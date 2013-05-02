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
package gov.nih.nci.cagrid.introduce.servicetools;

/*
 */

import javax.xml.namespace.QName;

import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.RemoveCallback;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.lifetime.SetTerminationTimeProvider;


/**
 * A <code>ResourceHome</code> that always returns a single resource object.
 */
public abstract class SingletonResourceHomeImpl implements ResourceHome {

    /**
     * The resource object that we will return. Starts off null and is populated
     * when the first find() call is made.
     */
    private Resource singleResource = null;


    public Class getKeyTypeClass() {
        return null;
    }


    public QName getKeyTypeName() {
        return null;
    }


    public Resource find(ResourceKey key) throws ResourceException {
        if (key != null) {
            throw new NoSuchResourceException();
        }

        synchronized (this) {

            // if we do not already have a value, call findSingleton()
            // and then cache the result.
            if (this.singleResource == null) {
                // check to see if the resource is persisted
                this.singleResource = createSingleton();
            }
            if (this.singleResource == null) {
                throw new NoSuchResourceException();
            }

        }
        return this.singleResource;
    }


    public void remove(ResourceKey key) throws ResourceException {
        if (this.singleResource instanceof RemoveCallback) {
            ((RemoveCallback) this.singleResource).remove();
            SetTerminationTimeProvider.sendTerminationNotification(this.singleResource);
        }
    }


    /**
     * Finds the single resource to be associated with this resource home. If
     * this operation returns null a <code>NoSuchResourceException</code> will
     * be raised. <br>
     * If this method returns a non-null reference, then the reference will be
     * cached, and findSingleton will not be called again. This allows, for
     * example, <code>findSingleton</code> to create a new resource object
     * without needing to track whether it has been called previously.
     */
    protected abstract Resource createSingleton() throws ResourceException;

}
