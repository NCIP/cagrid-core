package gov.nih.nci.cagrid.introduce.servicetools;

/*
 * Portions of this file Copyright 1999-2005 University of Chicago Portions of
 * this file Copyright 1999-2005 The University of Southern California. This
 * file or a portion of this file is licensed under the terms of the Globus
 * Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html. If you redistribute this
 * file, with or without modifications, you must include this notice in the
 * file.
 */

import java.lang.reflect.Method;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.utils.cache.MethodCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.util.I18n;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceIdentifier;
import org.globus.wsrf.ResourceLifetime;
import org.globus.wsrf.ResourceProperties;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertyMetaData;
import org.globus.wsrf.ResourcePropertySet;
import org.globus.wsrf.WSRFConstants;
import org.globus.wsrf.impl.ReflectionResourceProperty;
import org.globus.wsrf.impl.ResourcePropertyTopic;
import org.globus.wsrf.impl.SimpleResourcePropertyMetaData;
import org.globus.wsrf.impl.SimpleResourcePropertySet;
import org.globus.wsrf.impl.SimpleTopicList;
import org.globus.wsrf.utils.Resources;


/**
 * An implementation of {@link ResourceProperties ResourceProperties} and
 * {@link ResourceIdentifier ResourceIdentifier} which frees the developer from
 * having to write a getter and possibly a setter to implement every resource
 * property (RP).
 * <p>
 * This class uses an Axis-generated JavaBean class based on the Schema
 * definition of the RP set used by the service port type in WSDL. Since the
 * generated class contain a JavaBean property as well as QName and Schema type
 * metadata (such as occurence cardinality) for each RP, it is possible to
 * automatically create
 * {@link org.globus.wsrf.impl.ReflectionResourceProperty ReflectionResourceProperty}
 * objects to provide an implementation of the RPs. This is what
 * ReflectionResource does.
 * <p>
 * Advantages of using this class:
 * <ul>
 * <li>Less errors due to forgetting to implement a resource property.</li>
 * <li>Less code to write, so implementing a resource is faster.</li>
 * <li>Easier maintenance when the WSDL/Schema definition changes.</li>
 * <li>Less errors due to namespace mismatch between WSDL and code.</li>
 * <li>Less errors thanks to the handling of a few special cases.</li>
 * </ul>
 * <p>
 * Usage:
 * <p>
 * The specialized resource home class should do the following when creating a
 * resource:
 * <ol>
 * <li>create an object corresponding to the Schema type or element of the
 * resource property set.</li>
 * <li>initialize the object by calling its setters.</li>
 * <li>construct the specialized ReflectionResource-based resource object.</li>
 * <li>call initialize on the resource, passing it the implementation bean
 * created in 1).</li>
 * </ol>
 * <p>
 * Reuse approach:
 * <ul>
 * <li>If no specialized behavior is required from the resource implementation,
 * there is no need for a specialized class. The Home class can just create
 * objects of type ReflectionResource.</li>
 * <li>If specialized behavior is required from the resource implementation, it
 * is easy to reuse this class (via inheritance or delegation) and thus achieve
 * a clean separation of the specialized code from the RP setup code.</li>
 * </ul>
 * <p>
 * Typical goals when extending the class:
 * <ul>
 * <li>to customize the resource property creation behavior for special cases
 * not handled by this class.</li>
 * <li>to add domain-specific behavior, public or not, to the Resource objects.</li>
 * </ul>
 * Extending ReflectionResource:
 * <p>
 * Classes directly or indirectly extending ReflectionResource property must be
 * allow instanciation without parameter. If a parameterless constructor is
 * present it must not do any initialization whatsoever. The reason is to
 * decouple object creation from initialization so as to match the requirements
 * of Resource Home classes such as ResourceHomeImpl, which, when materializing
 * a previously stored persistent resource, creates the resource object first
 * using a parameterless constructor, and then initializes the object. Code that
 * creates the key of the resource automatically (for instance a UUID) - in
 * domain-specific cases where it makes sense - should not be put inside a
 * parameterless constructor but in an a overriding version of the
 * {@link #initialize(Object, QName, Object) initialize()} method, or inside the
 * Resource Home class. In this way a Persistent Resource object can be brought
 * back from a passivated state and be given the ID it used to have in activated
 * mode, as opposed to a newly generated ID.
 * <p>
 * Known limitations:
 * <ul>
 * <li>does not set the 'nillable' JavaBean property on the ResourceProperty
 * objects (Axis does not generate this metadatum).</li>
 * </ul>
 * <p>
 * This class does not provide a getter to the implementation JavaBean, because
 * it is good practice to access the value of a resource property by obtaining
 * the {@link ResourceProperty ResourceProperty} object first, as opposed to
 * calling the getters and setters of its implementation Bean.
 * <p>
 * In fact:
 * <ul>
 * <li>some resource properties are implemented using other objects (for
 * instance wsrl:CurrentTime is implemented via
 * {@link #getCurrentTime() getCurrentTime}, and the resource properties from
 * wsnt:NotificationProducer are implemented directly or indirectly by a
 * {@link SimpleTopicList SimpleTopicList}). </li>
 * <li>the {@link ResourceProperty ResourceProperty} object generic accessors
 * may do extra processing such as firing change notification events (for
 * instance if it is a {@link ResourcePropertyTopic ResourcePropertyTopic}).
 * </li>
 * </ul>
 */
public class ReflectionResource implements Resource, ResourceProperties, ResourceIdentifier, ResourceLifetime {

    private static Log logger = LogFactory.getLog(ReflectionResource.class.getName());

    private static I18n i18n = I18n.getI18n(Resources.class.getName());

    private static final Class[] SET_TERM_TIME_PARAM = new Class[]{Calendar.class};

    private static MethodCache methodCache = MethodCache.getInstance();

    private ResourcePropertySet resourcePropertySet;

    private Object ID;

    private Object resourceBean;

    private Method setTerminationTimeMethod;
    private Method getTerminationTimeMethod;


    /**
     * This should be called before any other resource property addition is made
     * as it will create resource properties object based on the resource
     * properties defined in the schema. It is possible to override the
     * implementation of certain resources properties by adding a new
     * ResourceProperty object to the resource property set after this method
     * has been called. This method should be called in the constructor of the
     * concrete resource class, or shortly after the parameterless constructor
     * has been called.
     * 
     * @param resourceBean
     *            Object An instance of the Axis-generated class corresponding
     *            to the resource property set global Schema type or element
     *            (with local type) used by the port type definition. That class
     *            has JavaBean properties matching all the resource properties
     *            of the port type. The ReflectionResource constructor creates
     *            the ResourceProperty objects based on the resource bean, which
     *            JavaBean properties SHOULD thus have been initialized
     *            beforehand. This object provides an easy way to initialize the
     *            values of the resource properties.
     *            <p>
     * @param resourceElementQName
     *            QName The QName of the resource properties element used by the
     *            port type. This corresponds to the value of the
     *            'wsrp:ResourceProperties' attribute. If the type of this
     *            element is anonymous (i.e. inlined in the element declaration)
     *            then this parameter is optional. If it is non-null though, its
     *            value takes precedence over Axis-generated metadata.
     *            <p>
     * @param key
     *            Object The resource key object for this resource. This is used
     *            to set the ID property of this object as a ResourceIdentifier,
     *            if the resource class doesn't create it automatically.
     *            <p>
     */
    public void initialize(Object resourceBean, QName resourceElementQName, Object key) throws ResourceException {
        if (key == null) {
            throw new IllegalArgumentException(i18n.getMessage("nullArgument", "key"));
        }
        this.ID = key;
        if (logger.isDebugEnabled()) {
            logger.debug("class of key passed to resource:" + key.getClass().getName());
            logger.debug("class of implementation bean passed to resource:" + resourceBean.getClass().getName());
        }
        initializeResourceProperties(resourceBean, resourceElementQName);
    }


    private void initializeResourceProperties(Object resourceBean, QName resourceElementQName) throws ResourceException {

        if (resourceBean == null) {
            throw new IllegalArgumentException(i18n.getMessage("nullArgument", "resourceBean"));
        }

        this.resourceBean = resourceBean;

        Class resourceBeanClazz = resourceBean.getClass();

        TypeDesc typeDesc = TypeDesc.getTypeDescForClass(resourceBeanClazz);
        if (typeDesc == null) {
            throw new ResourceException(i18n.getMessage("noTypeDesc", resourceBeanClazz));
        }

        // is type anonymous?
        String localTypeName = typeDesc.getXmlType().getLocalPart();
        if (localTypeName.startsWith(">")) {
            String globalElementName = localTypeName.substring(1); // this
            // is Axis-dependent of course...

            if (resourceElementQName == null) {
                resourceElementQName = new QName(typeDesc.getXmlType().getNamespaceURI(), globalElementName);
            }

        }
        if (logger.isDebugEnabled()) {
            logger.debug("QName of global element for resource properties is:" + resourceElementQName);
        }

        // get the resource properties
        FieldDesc[] fields = typeDesc.getFields();

        // Create resource properties per se
        this.resourcePropertySet = new SimpleResourcePropertySet(resourceElementQName);

        ResourceProperty prop = null;

        if (fields != null) {
            try {
                for (int i = 0; i < fields.length; i++) {
                    QName rpQName = fields[i].getXmlName();
                    if (logger.isDebugEnabled()) {
                        logger.debug("creating new resource property \"" + rpQName.toString() + "\"");
                    }
                    if (!(fields[i] instanceof ElementDesc)) {
                        String errorMessage = i18n.getMessage("rpNotElement", fields[i].getXmlType().getLocalPart());
                        throw new ResourceException(errorMessage);
                    }
                    ElementDesc elementDesc = (ElementDesc) fields[i];
                    // assume nillable always false since no metadata for that
                    SimpleResourcePropertyMetaData metaData = new SimpleResourcePropertyMetaData(rpQName, elementDesc
                        .getMinOccurs(), elementDesc.getMaxOccurs(), false, Object.class, false);
                    prop = createNewResourceProperty(metaData, this.resourceBean);
                    this.resourcePropertySet.add(prop);
                }
            } catch (ResourceException e) {
                logger.error("", e);
                throw e;
            } catch (Exception e) {
                logger.error("", e);
                throw new ResourceException(i18n.getMessage("resourceInitError"), e);
            }
        }

    }


    /**
     * Warning: this is not a callback (but maybe it should be).
     * 
     * @param rpQName
     *            QName
     * @param resourceBean
     *            Object
     * @throws Exception
     * @return ResourceProperty
     */
    protected ResourceProperty createNewResourceProperty(QName rpQName, Object resourceBean) throws Exception {
        return createNewResourceProperty(new SimpleResourcePropertyMetaData(rpQName), resourceBean);
    }


    /**
     * Override this callback method to specialize the implementation of the
     * resource property value accessors on a per resource property basis. For
     * instance, in the overriden version, do special processing if the QName of
     * the property matches a special QName, or just call this base
     * implementation otherwise.
     * <p>
     * The default behavior is to create a new
     * {@link ReflectionResourceProperty ReflectionResourceProperty} constructed
     * with the QName of the resource property and the resource implementation
     * Bean used to construct this ReflectionResource object.
     * <p>
     * This function handles a few special cases:
     * <ul>
     * <li>If rpQName equals WSRFConstants.CURRENT_TIME, the resource property
     * MUST use a dynamic implementation of getCurrentTime(), since time never
     * stops changing. Therefore the resource bean callback used is not the
     * initial resource Bean but the ReflectionResource object, which bears such
     * an implementation of that function.</li>
     * <li>If rpQName equals WSRFConstants.TERMINATION_TIME, the resource
     * property created is set be nillable.</li>
     * </ul>
     * 
     * @param metaData
     *            Meta data associated with the resource property object
     * @param resourceBean
     *            same as passed to constructor or initialize
     */
    protected ResourceProperty createNewResourceProperty(ResourcePropertyMetaData metaData, Object resourceBean)
        throws Exception {
        Object resourceBeanCallback = resourceBean; // default

        QName rpQName = metaData.getName();

        ReflectionResourceProperty prop = null;

        if (rpQName.equals(WSRFConstants.TERMINATION_TIME)) {
            prop = new ReflectionResourceProperty(SimpleResourcePropertyMetaData.TERMINATION_TIME, resourceBeanCallback);

            this.setTerminationTimeMethod = methodCache.getMethod(resourceBeanCallback.getClass(),
                "setTerminationTime", SET_TERM_TIME_PARAM);

            this.getTerminationTimeMethod = methodCache.getMethod(resourceBeanCallback.getClass(),
                "getTerminationTime", null);

        } else if (rpQName.equals(WSRFConstants.CURRENT_TIME)) {
            prop = new ReflectionResourceProperty(SimpleResourcePropertyMetaData.CURRENT_TIME, this);
        } else {
            prop = new ReflectionResourceProperty(metaData, resourceBeanCallback);
        }

        return prop;
    }


    public void setTerminationTime(Calendar time) {
        if (this.setTerminationTimeMethod != null) {
            try {
                this.setTerminationTimeMethod.invoke(this.getResourceBean(), new Object[]{time});
            } catch (Exception e) {
                logger.error("", e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    public Calendar getTerminationTime() {
        if (this.getTerminationTimeMethod != null) {
            try {
                return (Calendar) this.getTerminationTimeMethod.invoke(this.getResourceBean(), null);
            } catch (Exception e) {
                logger.error("", e);
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;
    }


    public Calendar getCurrentTime() {
        return Calendar.getInstance();
    }


    /**
     * See ResourceProperties.
     * 
     * @return ResourcePropertySet
     */
    public ResourcePropertySet getResourcePropertySet() {
        return this.resourcePropertySet;
    }


    /**
     * See ResourceIdentifier.
     * 
     * @return Object The key of the Resource. This is useful for instance when
     *         creating an endpoint reference that must be qualified with this
     *         resource.
     */
    public Object getID() {
        return this.ID;
    }


    /**
     * @return Object the Axis-generated Java Bean used as the main
     *         implementation of the resource state.
     */
    public Object getResourceBean() {
        return this.resourceBean;
    }

}
