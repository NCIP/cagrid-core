package gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import java.awt.Dimension;
import java.awt.Insets;

public class ResourceFrameworkOptionsManager extends JPanel {

	private JPanel resourceOptionsPanel = null;

	private JCheckBox lifetimeResource = null;

	private JCheckBox singletonResource = null;

	private JCheckBox persistantResource = null;

	private JCheckBox notificationResource = null;

	private JCheckBox customResource = null;

	private JCheckBox secureResource = null;

	private JCheckBox resourceProperty = null;

	private ServiceInformation info = null; // @jve:decl-index=0:

	private ServiceType service = null;

	private boolean newService;

	public ResourceFrameworkOptionsManager(ServiceType service,
			ServiceInformation info, boolean newService) {
		this.service = service;
		this.info = info;
		this.newService = newService;
		initialize();
	}

	public void resetGUI(ServiceType service, ServiceInformation info) {
		this.service = service;
		this.info = info;
		initSettings();
	}

	private void initSettings() {
		if (this.service.getResourceFrameworkOptions().getCustom() != null) {
			customResource.setSelected(true);
		} else {
			customResource.setSelected(false);
		}

		if (service.getResourceFrameworkOptions().getLifetime() != null) {
			lifetimeResource.setSelected(true);
		} else {
			lifetimeResource.setSelected(false);
		}

		if (service.getResourceFrameworkOptions().getPersistent() != null) {
			persistantResource.setSelected(true);
		} else {
			persistantResource.setSelected(false);
		}

		if (service.getResourceFrameworkOptions()
				.getResourcePropertyManagement() != null) {
			resourceProperty.setSelected(true);
		} else {
			resourceProperty.setSelected(false);
		}

		if (service.getResourceFrameworkOptions().getSingleton() != null) {
			singletonResource.setSelected(true);
		} else {
			singletonResource.setSelected(false);
		}

		if (service.getResourceFrameworkOptions().getNotification() != null) {
			notificationResource.setSelected(true);
		} else {
			notificationResource.setSelected(false);
		}

		if (service.getResourceFrameworkOptions().getSecure() != null) {
			secureResource.setSelected(true);
		} else {
			secureResource.setSelected(false);
		}
		
		checkResourcePropertyOptions();

	}

	private void initialize() {
		this.setSize(new Dimension(243, 139));
		this.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weighty = 1.0D;
		this.add(getResourceOptionsPanel(), gridBagConstraints);
		initSettings();
	}

	/**
	 * This method initializes resourceOptionsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getResourceOptionsPanel() {
		if (resourceOptionsPanel == null) {
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints20.gridwidth = 2;
			gridBagConstraints20.gridy = 4;
			gridBagConstraints20.gridx = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints19.gridy = 0;
			gridBagConstraints19.gridx = 0;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints18.gridy = 2;
			gridBagConstraints18.gridx = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints17.gridy = 3;
			gridBagConstraints17.gridx = 0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.gridx = 0;
			resourceOptionsPanel = new JPanel();
			resourceOptionsPanel.setLayout(new GridBagLayout());
			resourceOptionsPanel.setBorder(BorderFactory.createTitledBorder(
					null, "Resource Framework Options",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, IntroduceLookAndFeel
							.getPanelLabelColor()));
			resourceOptionsPanel
					.add(getLifetimeResource(), gridBagConstraints5);
			resourceOptionsPanel.add(getSingletonResource(),
					gridBagConstraints16);
			resourceOptionsPanel.add(getPersistantResource(),
					gridBagConstraints17);
			resourceOptionsPanel.add(getNotificationResource(),
					gridBagConstraints18);
			resourceOptionsPanel.add(getCustomResource(), gridBagConstraints19);
			resourceOptionsPanel.add(getSecureResource(), gridBagConstraints6);
			resourceOptionsPanel.add(getResourceProperty(),
					gridBagConstraints20);
		}
		return resourceOptionsPanel;
	}

	/**
	 * This method initializes lifetimeResource
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getLifetimeResource() {
		if (lifetimeResource == null) {
			lifetimeResource = new JCheckBox();
			lifetimeResource
					.setText(IntroduceConstants.INTRODUCE_LIFETIME_RESOURCE);

			lifetimeResource
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkResourcePropertyOptions();
						}
					});

		}
		return lifetimeResource;
	}

	/**
	 * This method initializes singletonResource
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getSingletonResource() {
		if (singletonResource == null) {
			singletonResource = new JCheckBox();
			singletonResource
					.setText(IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE);

			if (newService) {
				singletonResource
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(
									java.awt.event.ActionEvent e) {
								checkResourcePropertyOptions();
							}
						});
			} else {
				singletonResource.setEnabled(false);
			}

		}
		return singletonResource;
	}

	/**
	 * This method initializes persistantResource
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getPersistantResource() {
		if (persistantResource == null) {
			persistantResource = new JCheckBox();
			persistantResource
					.setText(IntroduceConstants.INTRODUCE_PERSISTENT_RESOURCE);

			persistantResource
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkResourcePropertyOptions();
						}
					});

		}
		return persistantResource;
	}

	/**
	 * This method initializes notificationResource
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getNotificationResource() {
		if (notificationResource == null) {
			notificationResource = new JCheckBox();
			notificationResource
					.setText(IntroduceConstants.INTRODUCE_NOTIFICATION_RESOURCE);

			notificationResource
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkResourcePropertyOptions();
						}
					});

		}
		return notificationResource;
	}

	/**
	 * This method initializes customResource
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getCustomResource() {
		if (customResource == null) {
			customResource = new JCheckBox();
			customResource
					.setText(IntroduceConstants.INTRODUCE_CUSTOM_RESOURCE);
			if (newService) {
				customResource
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(
									java.awt.event.ActionEvent e) {
								checkResourcePropertyOptions();
							}
						});
			} else {
				customResource.setEnabled(false);
			}

		}
		return customResource;
	}

	/**
	 * This method initializes secureResource
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getSecureResource() {
		if (secureResource == null) {
			secureResource = new JCheckBox();
			secureResource
					.setText(IntroduceConstants.INTRODUCE_SECURE_RESOURCE);

			secureResource
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkResourcePropertyOptions();
						}
					});

		}
		return secureResource;
	}

	/**
	 * This method initializes resourceProperty
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getResourceProperty() {
		if (resourceProperty == null) {
			resourceProperty = new JCheckBox();
			resourceProperty
					.setText("resource property access");

			resourceProperty
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkResourcePropertyOptions();
						}
					});

		}
		return resourceProperty;
	}

	private void checkResourcePropertyOptions() {
		if (newService) {
			getSingletonResource().setEnabled(true);
			getCustomResource().setEnabled(true);
		}
		getLifetimeResource().setEnabled(true);
		getPersistantResource().setEnabled(true);
		getNotificationResource().setEnabled(true);
		getSecureResource().setEnabled(true);
		getResourceProperty().setEnabled(true);

		if (getSingletonResource().isSelected()) {
			getLifetimeResource().setSelected(false);
			getLifetimeResource().setEnabled(false);
			getCustomResource().setSelected(false);
			getCustomResource().setEnabled(false);
		} else if (getLifetimeResource().isSelected()) {
			getSingletonResource().setSelected(false);
			getSingletonResource().setEnabled(false);
			getCustomResource().setSelected(false);
			getCustomResource().setEnabled(false);
		} else if (getCustomResource().isSelected()) {
			getSingletonResource().setSelected(false);
			getSingletonResource().setEnabled(false);
			getLifetimeResource().setSelected(false);
			getLifetimeResource().setEnabled(false);
			getPersistantResource().setSelected(false);
			getPersistantResource().setEnabled(false);
			getNotificationResource().setSelected(false);
			getNotificationResource().setEnabled(false);
			getSecureResource().setEnabled(false);
			getSecureResource().setSelected(false);
			getResourceProperty().setEnabled(false);
			getResourceProperty().setSelected(false);
		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"
