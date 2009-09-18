package gov.nih.nci.cagrid.workflow.context.service.globus.resource;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.workflow.context.stubs.types.StartCalledOnStartedWorkflow;
import gov.nih.nci.cagrid.workflow.service.ActiveBPELAdapter;
import gov.nih.nci.cagrid.workflow.service.WorkflowFactoryServiceConfiguration;
import gov.nih.nci.cagrid.workflow.service.globus.resource.WorkflowFactoryServiceResource;
import gov.nih.nci.cagrid.workflow.stubs.types.StartInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WMSInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WSDLReferences;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowOutputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusEventType;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.Constants;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.RemoveCallback;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.ResourceLifetime;
import org.globus.wsrf.ResourceProperties;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;
import org.globus.wsrf.Topic;
import org.globus.wsrf.TopicList;
import org.globus.wsrf.TopicListAccessor;
import org.globus.wsrf.WSRFConstants;
import org.globus.wsrf.impl.ReflectionResourceProperty;
import org.globus.wsrf.impl.ResourcePropertyTopic;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.impl.SimpleResourceProperty;
import org.globus.wsrf.impl.SimpleResourcePropertyMetaData;
import org.globus.wsrf.impl.SimpleResourcePropertySet;
import org.globus.wsrf.impl.SimpleTopic;
import org.globus.wsrf.impl.SimpleTopicList;


/** 
 * The implementation of this WorkflowServiceImplResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowServiceImplResource extends WorkflowServiceImplResourceBase implements 
	ResourceLifetime, ResourceProperties, TopicListAccessor, RemoveCallback{

static final Log logger = LogFactory.getLog(WorkflowFactoryServiceResource.class);
	
	public String workflowName = null;

	public String bpelFileName = null;

	public String bpelProcess = null;

	public static String BPEL_EXTENSION = ".bpel";

	private boolean started = false;
	
	private WorkflowStatusType workflowStatus = WorkflowStatusType.Pending;
	
	//Discover this from jndi config
	private  ActiveBPELAdapter abAdapter = null;
	
	private WorkflowServiceImplResourceConfiguration configuration = null;
	
	private WorkflowOutputType outputType = null;
	
	private Calendar terminationTime = null;
	
	private ResourcePropertySet propSet = null;
	
	protected ResourceProperty statusRP;
	
	private Object key = null;
	
	public static final QName WF_RP_SET = 
		new QName("http://workflow.cagrid.nci.nih.gov/WorkflowServiceImpl", "WorkflowRPSet");
	
	public static final QName WF_STATUS_QNAME =
		new QName("http://workflow.cagrid.nci.nih.gov/WorkflowServiceImpl", "Status");
	
	private TopicList topicList = null;
	
	
	protected void initialize(Object key) {
		this.key = key;
		this.propSet = new SimpleResourcePropertySet(WF_RP_SET);
		this.topicList = new SimpleTopicList(this);
		ResourceProperty prop = null;
		try {
			this.statusRP = new ResourcePropertyTopic(
					new SimpleResourceProperty(WF_STATUS_QNAME));
			this.propSet.add(this.statusRP);
			this.topicList.addTopic((Topic)this.statusRP);
			this.statusRP.add(WorkflowStatusType.Pending);
			prop =  new ReflectionResourceProperty(SimpleResourcePropertyMetaData.TERMINATION_TIME, this);
			this.propSet.add(prop);
			this.topicList.addTopic(new SimpleTopic(
					WSRFConstants.TERMINATION_TOPIC));
			prop = new ReflectionResourceProperty(SimpleResourcePropertyMetaData.CURRENT_TIME, this);
			this.propSet.add(prop);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	public WorkflowServiceImplResource (WMSInputType input, Calendar terminationTime)
			throws Exception {
		File bpelFile = null;
		if (terminationTime != null) {
			this.setTerminationTime(terminationTime);
		} else {
			this.terminationTime = Calendar.getInstance();
			// default TTL 12 hrs
			this.terminationTime.add(Calendar.HOUR, 12);
		}
		this.workflowName = input.getWorkflowName();
		this.bpelProcess = input.getBpelDoc();
		
		//This was done in the previous version of the service.
//		this.key = new SimpleResourceKey( WorkflowServiceHome.workflowQName, input.getWorkflowName());
		this.key = this.getResourceKey();
		
		this.initialize(key);
		String bpelFileName = System.getProperty("java.io.tmpdir")
				+ File.separator + workflowName + BPEL_EXTENSION;
		bpelFile = new File(bpelFileName);
		bpelFile.deleteOnExit();
		Utils.stringBufferToFile(new StringBuffer(bpelProcess), bpelFileName);
		WSDLReferences[] wsdlRefArray = input.getWsdlReferences();
		this.outputType = new WorkflowOutputType();
		this.abAdapter = new ActiveBPELAdapter(
				WorkflowFactoryServiceConfiguration.getConfiguration().getAbEndpoint(), 
				this.outputType, this.workflowStatus);
		String returnString = deploy(bpelFileName, workflowName, wsdlRefArray);
		logger.debug("deployment summary: " + returnString);
		logger.debug("Created a Workflow resource with key:" 
				+ input.getWorkflowName());
	}

	private String deploy(String bpelFileName, String workflowName,
			WSDLReferences[] wsdlRefArray) throws Exception {
		return this.abAdapter.deployBpr(bpelFileName,
				workflowName, wsdlRefArray);
	}

	public WorkflowStatusType start(StartInputType startInput) 
		throws WorkflowException, StartCalledOnStartedWorkflow {
		if (this.started) {
			throw new StartCalledOnStartedWorkflow();
		}
		try {
			this.abAdapter.startWorkflow(startInput);
			this.workflowStatus = WorkflowStatusType.Active;
		} catch (Exception e) {
			this.workflowStatus = WorkflowStatusType.Failed;
			throw new WorkflowException();
		}
		this.setStatusRP(this.workflowStatus);
		return this.workflowStatus;
	}

	public WorkflowStatusType getStatus() throws WorkflowException {
		this.workflowStatus = this.abAdapter.getWorkflowStatus();
		return this.workflowStatus;
	}
	
	public WorkflowStatusType pause() throws WorkflowException {
		this.abAdapter.suspend();
		this.workflowStatus = WorkflowStatusType.Pending;
		this.setStatusRP(this.workflowStatus);
		return this.workflowStatus;
	}
	
	public WorkflowStatusType resume() throws WorkflowException {
		this.abAdapter.resume();
		this.workflowStatus = WorkflowStatusType.Active;
		this.setStatusRP(this.workflowStatus);
		return this.workflowStatus;
	}
	
	public void cancel() throws WorkflowException {
		this.workflowStatus = WorkflowStatusType.Cancelled;
		this.setStatusRP(this.workflowStatus);
		this.abAdapter.cancel();
	}
	
	public WorkflowOutputType getWorkflowOutput() {
		return this.abAdapter.getWorkflowOutput();
	}
	
	public void setStatusRP(WorkflowStatusType status) {
		this.statusRP.set(0, status);
	}
	
	public WorkflowStatusType getStatusRP() {
		return (WorkflowStatusType) this.statusRP.get(0);
	}
	
	public WorkflowStatusEventType[] getDetailedStatus() throws WorkflowException {
		
		return this.abAdapter.getWorkflowStatusEventsArray();
	}
	
	public WorkflowServiceImplResourceConfiguration getConfiguration() {
		if (this.configuration != null) {
			return this.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = 
			Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			this.configuration = (WorkflowServiceImplResourceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			try {
				throw new Exception("Unable to instantiate service configuration.", e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return this.configuration;
	}

	public void setTerminationTime(Calendar arg0) {
		this.terminationTime = arg0;
	}

	public Calendar getTerminationTime() {
		return this.terminationTime;
	}

	public Calendar getCurrentTime() {
		return Calendar.getInstance();
	}

	public ResourcePropertySet getResourcePropertySet() {
		return this.propSet;
	}

	public TopicList getTopicList() {
		return this.topicList;
	}
	
	public void remove() throws ResourceException {
		try {
			this.abAdapter.remove();
		} catch (RemoteException e) {
			throw new ResourceException();
		}
		
	}
	
}
