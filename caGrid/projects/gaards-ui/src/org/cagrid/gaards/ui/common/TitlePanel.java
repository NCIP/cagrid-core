package org.cagrid.gaards.ui.common;

import gov.nih.nci.cagrid.common.Utils;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class TitlePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private String title;

	private String subtitle;

	private JLabel logo = null;

	private JLabel titleLabel = null;

	private JLabel subTitleLabel = null;

	/**
	 * This is the default constructor
	 */
	public TitlePanel(String title, String subtitle) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.weightx = 1.0D;
		gridBagConstraints2.gridy = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.weightx = 1.0D;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		logo = new JLabel(GAARDSLookAndFeel.getLogoNoText32x32());
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(logo, gridBagConstraints);

		boolean hasSubTitle = true;
		if (Utils.clean(subtitle) == null) {
			hasSubTitle = false;
		}
		titleLabel = new JLabel();
		titleLabel.setText(title);
		if (hasSubTitle) {
			titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		} else {
			titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		}
		this.add(titleLabel, gridBagConstraints1);

		if (hasSubTitle) {
			subTitleLabel = new JLabel();
			subTitleLabel.setText(subtitle);
			subTitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
			this.add(subTitleLabel, gridBagConstraints2);
		}
	}

}
