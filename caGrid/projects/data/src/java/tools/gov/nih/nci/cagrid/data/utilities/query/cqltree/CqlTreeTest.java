package gov.nih.nci.cagrid.data.utilities.query.cqltree;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.introduce.common.FileFilters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/** 
 *  CqlTreeTest
 *  TODO:DOCUMENT ME
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class CqlTreeTest extends JFrame {
	
	private JScrollPane cqlScrollPane = null;  //  @jve:decl-index=0:visual-constraint="174,37"
	private QueryTree tree = null;
	private JButton jButton = null;  //  @jve:decl-index=0:visual-constraint="24,89"

	public CqlTreeTest() {
		super("CQL Tree Test");
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints cons1 = new GridBagConstraints();
		cons1.gridx = 0;
		cons1.gridy = 0;
		cons1.weightx = 1.0d;
		cons1.weighty = 1.0d;
		cons1.fill = GridBagConstraints.BOTH;
		getContentPane().add(getJScrollPane(), cons1);
		GridBagConstraints cons2 = new GridBagConstraints();
		cons2.gridx = 0;
		cons2.gridy = 1;
		getContentPane().add(getJButton(), cons2);
		setVisible(true);
	}
	

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (cqlScrollPane == null) {
			cqlScrollPane = new JScrollPane();
			cqlScrollPane.setSize(new java.awt.Dimension(277,212));
			cqlScrollPane.setViewportView(getQueryTree());
		}
		return cqlScrollPane;
	}
	
	
	private QueryTree getQueryTree() {
		if (tree == null) {
			tree = new QueryTree();
		}
		return tree;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setSize(new java.awt.Dimension(90,51));
			jButton.setText("Open CQL");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(FileFilters.XML_FILTER);
					int choice = chooser.showOpenDialog(CqlTreeTest.this);
					if (choice == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();
						try {
							CQLQuery query = (CQLQuery) Utils.deserializeDocument(file.getAbsolutePath(), CQLQuery.class);
							getQueryTree().setQuery(query);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			});
		}
		return jButton;
	}


	public static void main(String[] args) {
		CqlTreeTest test = new CqlTreeTest();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
