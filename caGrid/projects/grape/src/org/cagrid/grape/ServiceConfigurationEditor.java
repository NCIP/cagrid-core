package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cagrid.grape.configuration.ServiceConfiguration;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.cagrid.grape.configuration.Services;

public class ServiceConfigurationEditor extends ConfigurationBasePanel {

	private static final long serialVersionUID = 1L;

	private JPanel titlePanel = null;

	private JLabel titleLabel = null;

	private JLabel icon = null;

	private JPanel valuesPanel = null;

	private JPanel actionPanel = null;

	private JTextField displayName = null;

	private JButton addButton = null;

	private JButton removeButton = null;

	private JPanel priorityPanel = null;

	private JButton increaseButton = null;

	private JButton decreaseButton = null;

	private JScrollPane jScrollPane1 = null;

	private ServiceTable services = null;

	private JPanel buttonPanel = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JTextField serviceURL = null;

	private JLabel jLabel2 = null;

	private JTextField serviceIdentity = null;

	/**
	 * This is the default constructor
	 */
	public ServiceConfigurationEditor(ConfigurationDescriptorTreeNode treeNode,
			Object conf) {
		super(treeNode, conf);
		initialize();
		loadValues();
	}

	public ServiceConfiguration getServiceConfiguration() {
		return (ServiceConfiguration) this.getConfigurationObject();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		jLabel2 = new JLabel();
		jLabel2.setText("Service Identity");
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.weightx = 1.0D;
		gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints12.gridy = 2;
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.weighty = 1.0D;
		gridBagConstraints31.fill = GridBagConstraints.BOTH;
		gridBagConstraints31.gridy = 1;
		gridBagConstraints31.weightx = 1.0D;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		this.setSize(500, 400);
		this.setLayout(new GridBagLayout());
		this.add(getTitlePanel(), gridBagConstraints);
		this.add(getValuesPanel(), gridBagConstraints31);
		this.add(getActionPanel(), gridBagConstraints12);
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 0.0D;
			gridBagConstraints2.gridy = 0;
			icon = new JLabel(LookAndFeel.getLogoNoText22x22());
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridx = 1;
			titleLabel = new JLabel();
			titleLabel.setText(getServiceConfiguration().getServiceGroupDescription());
			titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.add(icon, gridBagConstraints2);
			titlePanel.add(titleLabel, gridBagConstraints1);
		}
		return titlePanel;
	}

	private void loadValues() {
		this.services.clearTable();
		ServiceConfiguration conf = getServiceConfiguration();
		if (conf != null) {
			Services s = conf.getServices();
			if (s != null) {
				ServiceDescriptor[] list = s.getServiceDescriptor();
				if (list != null) {
					for (int i = 0; i < list.length; i++) {
						getServices().addService(list[i]);
					}
				}
			}
		}
	}

	/**
	 * This method initializes valuesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getValuesPanel() {
		if (valuesPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			valuesPanel = new JPanel();
			valuesPanel.setLayout(new GridBagLayout());
			valuesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, "Services",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					LookAndFeel.getPanelLabelColor()));
			valuesPanel.add(getPriorityPanel(), gridBagConstraints6);
			valuesPanel.add(getJScrollPane1(), gridBagConstraints3);
		}
		return valuesPanel;
	}

	/**
	 * This method initializes actionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridy = 2;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Service URL");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridx = 0;
			jLabel = new JLabel();
			jLabel.setText("Display Name");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.gridy = 3;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.weightx = 1.0D;
			actionPanel = new JPanel();
			actionPanel.setLayout(new GridBagLayout());
			actionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, "Add Service",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					LookAndFeel.getPanelLabelColor()));
			actionPanel.add(getDisplayName(), gridBagConstraints7);
			actionPanel.add(getButtonPanel(), gridBagConstraints9);
			actionPanel.add(jLabel, gridBagConstraints4);
			actionPanel.add(jLabel1, gridBagConstraints8);
			actionPanel.add(getServiceURL(), gridBagConstraints10);
			actionPanel.add(jLabel2, gridBagConstraints11);
			actionPanel.add(getServiceIdentity(), gridBagConstraints13);
		}
		return actionPanel;
	}

	/**
	 * This method initializes displayName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getDisplayName() {
		if (displayName == null) {
			displayName = new JTextField();
		}
		return displayName;
	}

	/**
	 * This method initializes addButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.setIcon(LookAndFeel.getAddIcon());
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addValue();
				}
			});
		}
		return addButton;
	}

	/**
	 * This method initializes removeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.setIcon(LookAndFeel.getRemoveIcon());
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeValue();
				}
			});
		}
		return removeButton;
	}

	private void moveUp() {
		int index = getServices().getSelectedRow();
		if (index > 0) {
			Services s = getServiceConfiguration().getServices();
			if (s != null) {
				ServiceDescriptor[] services = s.getServiceDescriptor();
				ServiceDescriptor temp = services[index - 1];
				services[index - 1] = services[index];
				services[index] = temp;
				loadValues();
				int row = (index - 1);
				getServices().setRowSelectionInterval(row, row);
			}
		}
	}

	private void moveDown() {
		int index = getServices().getSelectedRow();
		if (index != -1) {
			Services s = getServiceConfiguration().getServices();
			if ((s != null) && ((index+1)<getServices().getRowCount())) {
				ServiceDescriptor[] services = s.getServiceDescriptor();
				ServiceDescriptor temp = services[index + 1];
				services[index + 1] = services[index];
				services[index] = temp;
				loadValues();
				int row = (index + 1);
				getServices().setRowSelectionInterval(row, row);
			}
		}
	}

	private void addValue() {
		String displayName = Utils.clean(getDisplayName().getText());
		String serviceURL = Utils.clean(getServiceURL().getText());
		String serviceIdentity = Utils.clean(getServiceIdentity().getText());

		if (displayName == null) {
			GridApplication.getContext().showMessage(
					"Please specify a display name!!!");
			return;
		}

		if (serviceURL == null) {
			GridApplication.getContext().showMessage(
					"Please specify a Service URL!!!");
			return;
		}

		ServiceDescriptor des = new ServiceDescriptor();
		des.setDisplayName(displayName);
		des.setServiceURL(serviceURL);
		des.setServiceIdentity(serviceIdentity);

		ServiceConfiguration conf = getServiceConfiguration();
		Services s = conf.getServices();
		if (s == null) {
			s = new Services();
			conf.setServices(s);
		}
		ServiceDescriptor[] list = s.getServiceDescriptor();
		int size = 1;
		if (list != null) {
			size = list.length + 1;
		}

		ServiceDescriptor[] newList = new ServiceDescriptor[size];

		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				newList[i] = list[i];
			}
		}
		newList[size - 1] = des;

		s.setServiceDescriptor(newList);

		loadValues();
	}

	private void removeValue() {
		try {
			ServiceDescriptor service = getServices().getSelectedService();
			ServiceConfiguration conf = getServiceConfiguration();
			if (conf != null) {
				Services s = conf.getServices();
				if (s != null) {
					ServiceDescriptor[] list = s.getServiceDescriptor();
					if (list != null) {
						List<ServiceDescriptor> newList = new ArrayList<ServiceDescriptor>();
						for (int i = 0; i < list.length; i++) {
							if (!service.equals(list[i])) {
								newList.add(list[i]);
							}
						}
						getServices().clearTable();
						ServiceDescriptor[] des = new ServiceDescriptor[newList
								.size()];
						for (int i = 0; i < newList.size(); i++) {
							des[i] = newList.get(i);
							getServices().addService(des[i]);
						}
						s.setServiceDescriptor(des);
						loadValues();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			GridApplication.getContext().showMessage(
					Utils.getExceptionMessage(e));
		}
	}

	public void showErrorMessage(String title, String msg) {
		showErrorMessage(title, new String[] { msg });
	}

	public void showErrorMessage(String title, String[] msg) {
		JOptionPane.showMessageDialog(this, msg, title,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * This method initializes priorityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPriorityPanel() {
		if (priorityPanel == null) {
			priorityPanel = new JPanel();
			priorityPanel.setLayout(new FlowLayout());
			priorityPanel.add(getIncreaseButton(), null);
			priorityPanel.add(getDecreaseButton(), null);
			priorityPanel.add(getRemoveButton(), null);
		}
		return priorityPanel;
	}

	/**
	 * This method initializes increaseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getIncreaseButton() {
		if (increaseButton == null) {
			increaseButton = new JButton();
			increaseButton.setText("Move Up");
			increaseButton.setIcon(LookAndFeel.getUpIcon());
			increaseButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							moveUp();
						}
					});
		}
		return increaseButton;
	}

	/**
	 * This method initializes decreaseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDecreaseButton() {
		if (decreaseButton == null) {
			decreaseButton = new JButton();
			decreaseButton.setText("Move Down");
			decreaseButton.setIcon(LookAndFeel.getDownIcon());
			decreaseButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							moveDown();
						}
					});
		}
		return decreaseButton;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getServices());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes services
	 * 
	 * @return javax.swing.JTable
	 */
	private ServiceTable getServices() {
		if (services == null) {
			services = new ServiceTable();
		}
		return services;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getAddButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes serviceURL
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getServiceURL() {
		if (serviceURL == null) {
			serviceURL = new JTextField();
		}
		return serviceURL;
	}

	/**
	 * This method initializes serviceIdentity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getServiceIdentity() {
		if (serviceIdentity == null) {
			serviceIdentity = new JTextField();
		}
		return serviceIdentity;
	}
}
