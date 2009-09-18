package org.cagrid.gaards.ui.cds;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.cds.common.ProxyLifetime;

public class ProxyLifetimePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel jLabel = null;

	private JComboBox hours = null;

	private JComboBox minutes = null;

	private JLabel jLabel1 = null;

	private JComboBox seconds = null;

	private JLabel jLabel2 = null;

	private ProxyLifetimeListener listener;

	/**
	 * This is the default constructor
	 */
	public ProxyLifetimePanel() {
		this(null);
	}

	public ProxyLifetimePanel(ProxyLifetimeListener listener) {
		super();
		this.listener = listener;
		initialize();
	}

	private void initialize() {
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 5;
		gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints4.anchor = GridBagConstraints.WEST;
		gridBagConstraints4.gridy = 0;
		jLabel2 = new JLabel();
		jLabel2.setText("Seconds");
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.gridx = 4;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints3.weightx = 1.0;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		jLabel1 = new JLabel();
		jLabel1.setText("Minutes");
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints11.gridx = 2;
		gridBagConstraints11.gridy = 0;
		gridBagConstraints11.anchor = GridBagConstraints.WEST;
		gridBagConstraints11.weightx = 1.0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("Hours");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel, gridBagConstraints);
		this.add(getHours(), gridBagConstraints1);
		this.add(getMinutes(), gridBagConstraints11);
		this.add(jLabel1, gridBagConstraints2);
		this.add(getSeconds(), gridBagConstraints3);
		this.add(jLabel2, gridBagConstraints4);
	}

	public void setLifetime(long lifetimeSeconds) {
		hours.removeAllItems();
		minutes.removeAllItems();
		seconds.removeAllItems();
		if (lifetimeSeconds < 0) {
			lifetimeSeconds = 0;
		}
		long lseconds = 0;
		long lminutes = 0; 
		long lhours = 0;
	    if(lifetimeSeconds >= 59){
	        lseconds = 59;
	        long temp1 = (lifetimeSeconds/60);
	        if(temp1>=59){
	            lminutes = 59;
	            lhours = ((lifetimeSeconds/60)/60);
	        }else{
	            lminutes = temp1;
	            lhours = 0;
	        }
	    }else{
	        lseconds = lifetimeSeconds;
	        lminutes = 0;
	        lhours = 0;
	    }

		for (int i = 0; i <= lhours; i++) {
			hours.addItem(new Integer(i));
		}

		for (int i = 0; i <= lminutes; i++) {
			minutes.addItem(new Integer(i));
		}

		for (int i = 0; i <= lseconds; i++) {
			seconds.addItem(new Integer(i));
		}

	}

	public void setLifetime(ProxyLifetime lifetime) {
		hours.removeAllItems();
		minutes.removeAllItems();
		seconds.removeAllItems();
		for (int i = 0; i <= lifetime.getHours(); i++) {
			hours.addItem(new Integer(i));
		}

		for (int i = 0; i <= lifetime.getMinutes(); i++) {
			minutes.addItem(new Integer(i));
		}

		for (int i = 0; i <= lifetime.getSeconds(); i++) {
			seconds.addItem(new Integer(i));
		}

	}

	/**
	 * This method initializes hours
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getHours() {
		if (hours == null) {
			hours = new JComboBox();
			hours.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (listener != null) {
						listener.handleProxyLifetimeChange();
					}
				}
			});
		}
		return hours;
	}

	/**
	 * This method initializes minutes
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getMinutes() {
		if (minutes == null) {
			minutes = new JComboBox();
			minutes.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (listener != null) {
						listener.handleProxyLifetimeChange();
					}
				}
			});
		}
		return minutes;
	}

	/**
	 * This method initializes seconds
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSeconds() {
		if (seconds == null) {
			seconds = new JComboBox();
			seconds.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (listener != null) {
						listener.handleProxyLifetimeChange();
					}
				}
			});
		}
		return seconds;
	}

	public ProxyLifetime getProxyLifetime() {
		ProxyLifetime lifetime = new ProxyLifetime();
		int hours = 0;
		if (getHours().getSelectedItem() != null) {
			hours = ((Integer) getHours().getSelectedItem()).intValue();
		}
		lifetime.setHours(hours);

		int minutes = 0;
		if (getMinutes().getSelectedItem() != null) {
			minutes = ((Integer) getMinutes().getSelectedItem()).intValue();
		}
		lifetime.setMinutes(minutes);

		int seconds = 0;
		if (getSeconds().getSelectedItem() != null) {
			seconds = ((Integer) getSeconds().getSelectedItem()).intValue();
		}

		lifetime.setSeconds(seconds);
		return lifetime;
	}

}
