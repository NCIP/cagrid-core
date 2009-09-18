package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.LogicalOperator;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipQuery;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipStatus;
import gov.nih.nci.cagrid.gridgrouper.client.Group;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperTree;
import org.cagrid.gaards.ui.gridgrouper.tree.GroupTreeNode;
import org.cagrid.grape.utils.MultiEventProgressBar;
import org.globus.gsi.GlobusCredential;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGrouperExpressionEditor extends JPanel {

	private static final String EXPRESSION_EDITOR = "ExpressionEditor"; // @jve:decl-index=0:

	private static final String QUERY_EDITOR = "QueryEditor"; // @jve:decl-index=0:

	private static final long serialVersionUID = 1L;

	private JSplitPane jSplitPane = null;

	private JPanel treePanel = null;

	private JPanel expressionPanel = null;

	private JScrollPane jScrollPane = null;

	private GridGrouperTree grouperTree = null;

	private JScrollPane jScrollPane1 = null;

	private ExpressionTree expressionTree = null;

	private MultiEventProgressBar progress = null;

	private MembershipExpression expression;

	private JPanel editorPanel = null;

	private CardLayout editorLayout = null;

	private JPanel expressionEditor = null;

	private JPanel queryEditor = null;

	private JPanel expressionProperties = null;

	private JLabel jLabel = null;

	private JComboBox logicalRelation = null;

	private JPanel expressionButtons = null;

	private JButton addExpression = null;

	private JButton addGroup = null;

	private JButton removeExpression = null;

	private JPanel queryProperties = null;

	private JPanel queryButtons = null;

	private JButton removeGroup = null;

	private JLabel jLabel1 = null;

	private JComboBox membership = null;

	private JLabel jLabel2 = null;

	private JTextField gridGrouper = null;

	private JTextField group = null;

	private JLabel jLabel3 = null;

	private JPanel grouperButtonPanel = null;

	private JComboBox gridGrouperURI = null;

	private JButton loadGridGrouper = null;

	private List gridGrouperURIs;

	/**
	 * This is the default constructor
	 */
	public GridGrouperExpressionEditor(List gridGrouperURIs,
			boolean loadOnStartup) {
		super();
		this.gridGrouperURIs = gridGrouperURIs;
		this.expression = new MembershipExpression();
		this.expression.setLogicRelation(LogicalOperator.AND);
		// this.setBorder(BorderFactory.createTitledBorder(null, "Grid Grouper
		// Expression Editor",
		// TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
		// new Font("Dialog", Font.BOLD, 12),
		// new Color(62, 109, 181)));
		initialize();
		if ((loadOnStartup) && (gridGrouperURIs != null)
				&& (gridGrouperURIs.size() > 0)) {
			this.getGrouperTree().addGridGrouper(
					(String) gridGrouperURIs.get(0), null);
		}
	}

	public GridGrouperExpressionEditor(List gridGrouperURIs,
			boolean loadOnStartup, MembershipExpression expression) {
		super();
		this.gridGrouperURIs = gridGrouperURIs;
		this.expression = expression;
		initialize();
		if ((loadOnStartup) && (gridGrouperURIs != null)
				&& (gridGrouperURIs.size() > 0)) {
			GlobusCredential cred = null;
			try {
				cred = ProxyUtil.getDefaultProxy();
				if (cred.getTimeLeft() <= 0) {
					cred = null;
				}
			} catch (Exception e) {

			}
			this.getGrouperTree().addGridGrouper(
					(String) gridGrouperURIs.get(0), cred);
		}
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.ipadx = 0;
		gridBagConstraints2.ipady = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.weighty = 1.0;
		gridBagConstraints2.gridx = 1;
		// this.setSize(500, 300);
		this.setLayout(new GridBagLayout());
		this.add(getJSplitPane(), gridBagConstraints2);
		setExpressionEditor(expression);
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(0.5D);
			jSplitPane.setResizeWeight(1.0D);
			jSplitPane.setLeftComponent(getTreePanel());
			jSplitPane.setRightComponent(getExpressionPanel());

		}
		return jSplitPane;
	}

	/**
	 * This method initializes treePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTreePanel() {
		if (treePanel == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new Insets(5, 10, 5, 10);
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			treePanel = new JPanel();
			treePanel.setLayout(new GridBagLayout());
			treePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Grid Grouper Browser", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(62, 109, 181)));
			treePanel.add(getProgress(), gridBagConstraints3);
			treePanel.add(getJScrollPane(), gridBagConstraints1);
			treePanel.add(getGrouperButtonPanel(), gridBagConstraints17);
		}
		return treePanel;
	}

	/**
	 * This method initializes expressionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getExpressionPanel() {
		if (expressionPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.ipadx = 0;
			gridBagConstraints4.ipady = 0;
			gridBagConstraints4.weightx = 1.0D;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridx = 0;
			expressionPanel = new JPanel();
			expressionPanel.setLayout(new GridBagLayout());
			expressionPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Grid Grouper Expression Editor",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(62, 109, 181)));
			expressionPanel.add(getJScrollPane1(), gridBagConstraints);
			expressionPanel.add(getEditorPanel(), gridBagConstraints4);
		}
		return expressionPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getGrouperTree());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes grouperTree
	 * 
	 * @return javax.swing.JTree
	 */
	protected GridGrouperTree getGrouperTree() {
		if (grouperTree == null) {
			grouperTree = new GridGrouperTree();
            grouperTree.setVisibleRowCount(5);
			grouperTree.setProgress(getProgress());
			grouperTree
					.addMouseListener(new GrouperTreeExpressionEventListener(
							grouperTree, this));
		}
		return grouperTree;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setName("jScrollPane1");
			jScrollPane1.setViewportView(getExpressionTree());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes expressionTree
	 * 
	 * @return javax.swing.JTree
	 */
	protected ExpressionTree getExpressionTree() {
		if (expressionTree == null) {
			expressionTree = new ExpressionTree(this, expression);
			expressionTree.getRootNode().loadExpression();
            expressionTree.setVisibleRowCount(5);
		}
		return expressionTree;
	}

	/**
	 * This method initializes progress
	 * 
	 * @return javax.swing.JProgressBar
	 */
	protected MultiEventProgressBar getProgress() {
		if (progress == null) {
			progress = new MultiEventProgressBar(false);
			progress.setForeground(PortalLookAndFeel.getPanelLabelColor());
			progress.setString("");
			progress.setStringPainted(true);
		}
		return progress;
	}

	/**
	 * This method initializes editorPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getEditorPanel() {
		if (editorPanel == null) {
			editorPanel = new JPanel();
			editorLayout = new CardLayout();
			editorPanel.setLayout(editorLayout);
			editorPanel.setName("editorPanel");
			editorPanel.add(getExpressionEditor(), EXPRESSION_EDITOR);
			editorPanel.add(getQueryEditor(), QUERY_EDITOR);
		}
		return editorPanel;
	}

	/**
	 * This method initializes expressionEditor
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getExpressionEditor() {
		if (expressionEditor == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Grid Grouper");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.weightx = 1.0D;
			gridBagConstraints5.weighty = 1.0D;
			gridBagConstraints5.gridy = 0;
			expressionEditor = new JPanel();
			expressionEditor.setLayout(new GridBagLayout());
			expressionEditor.setName(EXPRESSION_EDITOR);
			expressionEditor.setBorder(BorderFactory.createTitledBorder(null,
					"Edit Membership Expression",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(62, 109, 181)));
			expressionEditor
					.add(getExpressionProperties(), gridBagConstraints5);
			expressionEditor.add(getExpressionButtons(), gridBagConstraints8);
		}
		return expressionEditor;
	}

	/**
	 * This method initializes queryEditor
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getQueryEditor() {
		if (queryEditor == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.gridy = 2;
			gridBagConstraints10.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints10.gridx = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.weighty = 1.0D;
			gridBagConstraints9.gridy = 0;
			queryEditor = new JPanel();
			queryEditor.setLayout(new GridBagLayout());
			queryEditor.setName(QUERY_EDITOR);
			queryEditor.setBorder(BorderFactory.createTitledBorder(null,
					"Edit Membership Query",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(62, 109, 181)));
			queryEditor.add(getQueryProperties(), gridBagConstraints9);
			queryEditor.add(getQueryButtons(), gridBagConstraints10);
		}
		return queryEditor;
	}

	public void setExpressionEditor(MembershipExpression exp) {
		this.getLogicalRelation().setSelectedItem(exp.getLogicRelation());
		this.editorLayout.show(getEditorPanel(), EXPRESSION_EDITOR);
		repaint();
	}

	public void setExpression(MembershipExpression exp) {
		this.expression = exp;
		getExpressionTree().getRootNode().resetExpression(exp);
		setExpressionEditor(exp);
	}

	public void setExpressionQuery(MembershipQuery query) {
		this.getMembership().setSelectedItem(query.getMembershipStatus());
		this.getGridGrouper().setText(
				query.getGroupIdentifier().getGridGrouperURL());
		this.getGroup().setText(query.getGroupIdentifier().getGroupName());
		this.editorLayout.show(getEditorPanel(), QUERY_EDITOR);
	}

	/**
	 * This method initializes expressionProperties
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getExpressionProperties() {
		if (expressionProperties == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Logical Operator");
			expressionProperties = new JPanel();
			expressionProperties.setLayout(new GridBagLayout());
			expressionProperties.add(jLabel, gridBagConstraints6);
			expressionProperties.add(getLogicalRelation(), gridBagConstraints7);
		}
		return expressionProperties;
	}

	/**
	 * This method initializes logicalRelation
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getLogicalRelation() {
		if (logicalRelation == null) {
			logicalRelation = new JComboBox();
			logicalRelation
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							BaseTreeNode node = getExpressionTree()
									.getCurrentNode();
							if (node instanceof ExpressionNode) {
								ExpressionNode en = (ExpressionNode) node;
								if (!en.getExpression().getLogicRelation()
										.equals(
												getLogicalRelation()
														.getSelectedItem())) {
									en
											.getExpression()
											.setLogicRelation(
													(LogicalOperator) getLogicalRelation()
															.getSelectedItem());
									en.refresh();
								}
							}
						}
					});
			logicalRelation.addItem(LogicalOperator.AND);
			logicalRelation.addItem(LogicalOperator.OR);
		}
		return logicalRelation;
	}

	/**
	 * This method initializes expressionButtons
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getExpressionButtons() {
		if (expressionButtons == null) {
			expressionButtons = new JPanel();
			expressionButtons.setLayout(new FlowLayout());
			expressionButtons.add(getAddExpression(), null);
			expressionButtons.add(getAddGroup(), null);
			expressionButtons.add(getRemoveExpression(), null);
		}
		return expressionButtons;
	}

	/**
	 * This method initializes addExpression
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddExpression() {
		if (addExpression == null) {
			addExpression = new JButton();
			addExpression.setText("Add Expression");
			addExpression.setIcon(PortalLookAndFeel.getAddIcon());
			addExpression
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							DefaultMutableTreeNode currentNode = getExpressionTree()
									.getCurrentNode();
							if (currentNode instanceof ExpressionNode) {
								ExpressionNode n = (ExpressionNode) currentNode;
								n.addAndExpression();
							} else {
								Util
										.showErrorMessage("Please select an expression to add a sub expression to!!!");
							}
						}

					});
		}
		return addExpression;
	}

	/**
	 * This method initializes addGroup
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddGroup() {
		if (addGroup == null) {
			addGroup = new JButton();
			addGroup.setText("Add Group");
			addGroup.setIcon(PortalLookAndFeel.getAddIcon());
			addGroup.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DefaultMutableTreeNode currentNode = getGrouperTree()
							.getCurrentNode();
					if ((currentNode != null)
							&& (currentNode instanceof GroupTreeNode)) {
						GroupTreeNode grp = (GroupTreeNode) currentNode;
						addGroupToCurrentExpression(grp.getGroup());
					} else {
						Util
								.showErrorMessage("Please select a group to add!!!");
					}
				}
			});
		}
		return addGroup;
	}

	/**
	 * This method initializes removeExpression
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveExpression() {
		if (removeExpression == null) {
			removeExpression = new JButton();
			removeExpression.setText("Remove");
			removeExpression.setIcon(PortalLookAndFeel.getRemoveIcon());
			removeExpression
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							DefaultMutableTreeNode currentNode = getExpressionTree()
									.getCurrentNode();
							if (currentNode instanceof ExpressionNode) {
								ExpressionNode n = (ExpressionNode) currentNode;
								if (n.isRootExpression()) {
									Util
											.showErrorMessage("Cannot remove root expression!!!");
									return;
								} else {
									ExpressionNode parent = (ExpressionNode) n
											.getParent();
									parent.removeExpression(n.getExpression());
								}
							} else {
								Util
										.showErrorMessage("Please select an expression to remove!!!");
								return;
							}
						}

					});

		}
		return removeExpression;
	}

	/**
	 * This method initializes queryProperties
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getQueryProperties() {
		if (queryProperties == null) {
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints16.gridy = 2;
			jLabel3 = new JLabel();
			jLabel3.setText("Group Name");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridy = 2;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridy = 1;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.gridy = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.weightx = 1.0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 0;
			jLabel1 = new JLabel();
			jLabel1.setText("Membership");
			queryProperties = new JPanel();
			queryProperties.setLayout(new GridBagLayout());
			queryProperties.setName("jPanel");
			queryProperties.add(jLabel1, gridBagConstraints12);
			queryProperties.add(getMembership(), gridBagConstraints11);
			queryProperties.add(jLabel2, gridBagConstraints13);
			queryProperties.add(getGridGrouper(), gridBagConstraints14);
			queryProperties.add(getGroup(), gridBagConstraints15);
			queryProperties.add(jLabel3, gridBagConstraints16);
		}
		return queryProperties;
	}

	/**
	 * This method initializes queryButtons
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getQueryButtons() {
		if (queryButtons == null) {
			queryButtons = new JPanel();
			queryButtons.setLayout(new FlowLayout());
			queryButtons.add(getRemoveGroup(), null);
		}
		return queryButtons;
	}

	/**
	 * This method initializes removeGroup
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveGroup() {
		if (removeGroup == null) {
			removeGroup = new JButton();
			removeGroup.setText("Remove");
			removeGroup.setIcon(PortalLookAndFeel.getRemoveIcon());
			removeGroup.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DefaultMutableTreeNode currentNode = getExpressionTree()
							.getCurrentNode();
					if (currentNode instanceof QueryNode) {
						QueryNode n = (QueryNode) currentNode;
						ExpressionNode parent = (ExpressionNode) n.getParent();
						parent.removeGroup(n.getQuery());
					} else {
						Util
								.showErrorMessage("Please select a group to remove!!!");
						return;
					}
				}

			});

		}
		return removeGroup;
	}

	/**
	 * This method initializes membership
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getMembership() {
		if (membership == null) {
			membership = new JComboBox();
			membership.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					BaseTreeNode node = getExpressionTree().getCurrentNode();
					if (node instanceof QueryNode) {
						QueryNode n = (QueryNode) node;
						if (!n.getQuery().getMembershipStatus().equals(
								getMembership().getSelectedItem())) {
							n.getQuery().setMembershipStatus(
									(MembershipStatus) getMembership()
											.getSelectedItem());
							n.refresh();
						}
					}
				}
			});
			membership.addItem(MembershipStatus.MEMBER_OF);
			membership.addItem(MembershipStatus.NOT_MEMBER_OF);
		}
		return membership;
	}

	protected void addGroupToCurrentExpression(final Group grp) {

		DefaultMutableTreeNode currentNode = expressionTree.getCurrentNode();
		if (currentNode instanceof ExpressionNode) {
			ExpressionNode exp = (ExpressionNode) currentNode;
			exp.addGroup(grp);
		} else {
			Util
					.showErrorMessage("Please select an expression in which to add the group!!!");
		}
	}

	/**
	 * This method initializes gridGrouper
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridGrouper() {
		if (gridGrouper == null) {
			gridGrouper = new JTextField();
			gridGrouper.setEditable(false);
		}
		return gridGrouper;
	}

	/**
	 * This method initializes group
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGroup() {
		if (group == null) {
			group = new JTextField();
			group.setEditable(false);
		}
		return group;
	}

	/**
	 * This method initializes grouperButtonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGrouperButtonPanel() {
		if (grouperButtonPanel == null) {
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.gridx = 0;
			gridBagConstraints20.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints20.gridy = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.gridy = 0;
			gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints19.weightx = 1.0;
			grouperButtonPanel = new JPanel();
			grouperButtonPanel.setLayout(new GridBagLayout());
			grouperButtonPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Load Grid Grouper", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(62, 109, 181)));
			grouperButtonPanel.add(getGridGrouperURI(), gridBagConstraints19);
			grouperButtonPanel.add(getLoadGridGrouper(), gridBagConstraints20);
		}
		return grouperButtonPanel;
	}

	/**
	 * This method initializes gridGrouperURI
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getGridGrouperURI() {
		if (gridGrouperURI == null) {
			gridGrouperURI = new JComboBox();
			for (int i = 0; i < gridGrouperURIs.size(); i++) {
				String uri = (String) gridGrouperURIs.get(i);
				gridGrouperURI.addItem(uri);
			}
		}
		return gridGrouperURI;
	}

	/**
	 * This method initializes loadGridGrouper
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getLoadGridGrouper() {
		if (loadGridGrouper == null) {
			loadGridGrouper = new JButton();
			loadGridGrouper.setText("Load");
			loadGridGrouper.setIcon(GridGrouperLookAndFeel
					.getGrouperIconNoBackground22X22());
			loadGridGrouper
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							String uri = (String) getGridGrouperURI()
									.getSelectedItem();
							if (!isSameGridGrouper(uri, getExpressionTree()
									.getRootNode().getExpression())) {
								getExpressionTree().getRootNode()
										.clearExpression();
								getExpressionTree().reload();
							}
							getGrouperTree().getRootNode()
									.removeAllGridGroupers();
							getGrouperTree().reload();
							getGrouperTree().addGridGrouper(uri, null);
						}
					});
		}
		return loadGridGrouper;
	}

	private boolean isSameGridGrouper(String uri, MembershipExpression exp) {
		MembershipQuery[] mq = exp.getMembershipQuery();
		if ((mq != null) && (mq.length > 0)) {
			if (mq[0].getGroupIdentifier().getGridGrouperURL().equals(uri)) {
				return true;
			} else {
				return false;
			}
		}
		MembershipExpression[] list = exp.getMembershipExpression();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				if (isSameGridGrouper(uri, list[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public MembershipExpression getMembershipExpression() {
		return getExpressionTree().getRootNode().getExpression();
	}
}
