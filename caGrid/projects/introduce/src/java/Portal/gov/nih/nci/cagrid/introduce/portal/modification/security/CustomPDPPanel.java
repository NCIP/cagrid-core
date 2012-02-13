package gov.nih.nci.cagrid.introduce.portal.modification.security;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.security.CustomPDPChainAuthorization;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */

public class CustomPDPPanel extends JPanel {


	private JTextField pdpAuthField = null;
	private JLabel pdpChainLabel = null;

	/**
	 * This method initializes
	 * 
	 */
	public CustomPDPPanel() {
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.gridy = 0;
		pdpChainLabel = new JLabel();
		pdpChainLabel.setText("PDP Authorization Chain");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.gridx = 1;
		this.setLayout(new GridBagLayout());

		this.setBorder(BorderFactory.createTitledBorder(
            null, "Custom PDP Authorization Chain", TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), 
            PortalLookAndFeel.getPanelLabelColor()));

		this.setSize(new Dimension(473, 214));
		this.add(getPdpAuthField(), gridBagConstraints);
		this.add(pdpChainLabel, gridBagConstraints1);
	}
	
	public CustomPDPChainAuthorization getAuthorization(){
		CustomPDPChainAuthorization pdp = new CustomPDPChainAuthorization(getPdpAuthField().getText());
		return pdp;
	}
	
	public void setAuthorization(CustomPDPChainAuthorization auth){
		this.getPdpAuthField().setText(auth.getPDPChain());
	}

	/**
	 * This method initializes pdpAuthField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPdpAuthField() {
		if (pdpAuthField == null) {
			pdpAuthField = new JTextField();
		}
		return pdpAuthField;
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.getContentPane().add(new CustomPDPPanel());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
} // @jve:decl-index=0:visual-constraint="10,10"
