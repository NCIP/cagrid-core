/**
 * 
 */
package gov.nih.nci.cagrid.workflow.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.configuration.GeneralConfiguration;
import org.cagrid.grape.configuration.Properties;
import org.cagrid.grape.configuration.Property;
import org.cagrid.grape.configuration.Values;

/**
 * @author madduri
 *
 */
public class WorkflowUIUtils {
	private static Logger log = Logger.getLogger(WorkflowUIUtils.class);;

	public static final String UI_CONF = "workflow";

	public static final String WORKFlOW_FACTORY = "Workflow Factory Service(s)";

	public static List getWorkflowFactoryURL() {
		return getValues(WORKFlOW_FACTORY);
	}

	public static List getValues(String property) {
		List values = new ArrayList();
		try {
			GeneralConfiguration conf = (GeneralConfiguration) GridApplication
					.getContext().getConfigurationManager()
					.getConfigurationObject(UI_CONF);

			Properties props = conf.getProperties();
			if (props != null) {
				Property[] prop = props.getProperty();
				if (prop != null) {
					for (int i = 0; i < prop.length; i++) {
						if (prop[i].getName().equals(property)) {
							Values vals = prop[i].getValues();
							if (vals != null) {
								String[] val = vals.getValue();
								if (val != null) {
									for (int j = 0; j < val.length; j++) {
										values.add(val[j]);
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("Error loading the property " + property
					+ " from the configuration.");
			log.error(e);
		}
		return values;
	}

}
