package gov.nih.nci.cagrid.introduce.portal.deployment;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.GenericPropertiesPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * ServicePropertiesPanel
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class ServicePropertiesPanel extends GenericPropertiesPanel {

	private JPanel servicePropertiesPanel = null;

	private JScrollPane servicePropertiesScrollPane = null;

	private ServiceInformation info;

	/**
	 * This method initializes
	 */
	public ServicePropertiesPanel(ServiceInformation info) {
		this.info = info;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		this.setLayout(new GridBagLayout());
		this.add(getServicePropertiesScrollPane(), gridBagConstraints);
		
		//load up the service properties
		int i = 0;
		if (info != null
				&& info.getServiceDescriptor().getServiceProperties() != null
				&& info.getServiceDescriptor().getServiceProperties()
						.getProperty() != null) {
			for (i = 0; i < info.getServiceDescriptor()
					.getServiceProperties().getProperty().length; i++) {
				ServicePropertiesProperty prop = info
						.getServiceProperties().getProperty(i);
				this.addTextField(this.getServicePropertiesPanel(), prop
						.getKey(), prop.getValue(), i, true);
				this.getTextField(prop.getKey()).setForeground(Color.BLUE);

			}
		}

	}

	/**
	 * This method initializes servicePropertiesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getServicePropertiesPanel() {
		if (servicePropertiesPanel == null) {
			servicePropertiesPanel = new JPanel();
			servicePropertiesPanel.setBackground(Color.WHITE);
			servicePropertiesPanel.setLayout(new GridBagLayout());
		}
		return servicePropertiesPanel;
	}

	/**
	 * This method initializes servicePropertiesScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getServicePropertiesScrollPane() {
		if (servicePropertiesScrollPane == null) {
			servicePropertiesScrollPane = new JScrollPane();
			servicePropertiesScrollPane
					.setViewportView(getServicePropertiesPanel());
			servicePropertiesScrollPane
					.setPreferredSize(new Dimension(400, 200));
			servicePropertiesScrollPane
					.setViewportView(getServicePropertiesPanel());
			servicePropertiesScrollPane
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Service Properties",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									new Font("Dialog", Font.BOLD, 12), PortalLookAndFeel
											.getPanelLabelColor()));
		}
		return servicePropertiesScrollPane;
	}
	
	public Properties getServiceProperties(){
		Properties serviceProps = new Properties();
		// load up the service properties
		if (info.getServiceDescriptor()
				.getServiceProperties() != null
				&& info.getServiceDescriptor()
						.getServiceProperties()
						.getProperty() != null) {
			for (int i = 0; i < info.getServiceProperties()
					.getProperty().length; i++) {
				ServicePropertiesProperty prop = info
						.getServiceProperties()
						.getProperty(i);
				serviceProps.put(prop.getKey(),
						getTextFieldValue(prop.getKey()));
			}
		}
		
		return serviceProps;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
