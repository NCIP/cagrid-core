package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.cql.validation.CqlDomainValidator;
import gov.nih.nci.cagrid.data.cql.validation.CqlStructureValidator;
import gov.nih.nci.cagrid.data.cql.validation.DomainModelValidator;
import gov.nih.nci.cagrid.data.cql.validation.ObjectWalkingCQLValidator;
import gov.nih.nci.cagrid.data.utilities.query.cqltree.AssociationTreeNode;
import gov.nih.nci.cagrid.data.utilities.query.cqltree.AttributeTreeNode;
import gov.nih.nci.cagrid.data.utilities.query.cqltree.GroupTreeNode;
import gov.nih.nci.cagrid.data.utilities.query.cqltree.IconTreeNode;
import gov.nih.nci.cagrid.data.utilities.query.cqltree.QueryTree;
import gov.nih.nci.cagrid.data.utilities.query.cqltree.QueryTreeNode;
import gov.nih.nci.cagrid.data.utilities.query.cqltree.TargetTreeNode;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.grape.utils.ErrorDialog;

/** 
 *  QueryBuilder
 *  Graphical tool to build queries against a domain model
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 4, 2006 
 * @version $Id$ 
 */
public class QueryBuilder extends JFrame {
	
	public static final String LOGIC_PANEL = "logic";
	public static final String PREDICATES_PANEL = "predicates";
	
	private QueryTree queryTree = null;
	private JScrollPane queryTreeScrollPane = null;
	private JPanel contextButtonPanel = null;
	private JButton setTargetButton = null;
	private JButton addAssociationButton = null;
	private JButton addAttributeButton = null;
	private JButton addGroupButton = null;
	private JMenuBar mainMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem newQueryMenuItem = null;
	private JMenuItem loadQueryMenuItem = null;
	private JMenuItem saveQueryMenuItem = null;
	private JMenuItem exitMenuItem = null;
	private JButton removeItemButton = null;
	private JMenu domainModelMenu = null;
	private JMenuItem loadModelMenuItem = null;
	private JMenuItem retrieveDomainModelMenuItem = null;
	private JMenuItem saveDomainModelMenuItem = null;
	private JButton changePredicateButton = null;
	private JButton changeLogicButton = null;
	private JButton changeValueButton = null;
	private JPanel queryComponentsPanel = null;
	private JSplitPane mainSplitPane = null;
	private TypeDisplayPanel typeDisplayPanel = null;
	private JPanel modifierConfigurationPanel = null;
	private JCheckBox countResultsCheckBox = null;
	private JRadioButton distinctAttributeRadioButton = null;
	private JRadioButton multipleAttributesRadioButton = null;
	private JList returnedAttributesList = null;
	private JScrollPane returnedAttributesScrollPane = null;
	private JButton selectAttributesButton = null;
	private JTabbedPane queryBuildingTabbedPane = null;
	private JPanel attributeSelectionPanel = null;
	private JPanel queryModsPanel = null;
	private JCheckBox useQueryModsCheckBox = null;
	
	private String lastDirectory = null;
	private transient CqlDomainValidator domainValidator = null;	
	private transient DomainModel domainModel = null;
	
	public QueryBuilder() {
		super();
		setTitle("CQL Query Builder");
		domainValidator = new DomainModelValidator();
		ErrorDialog.setOwnerFrame(this);
		initialize();
	}
	
	
	private void initialize() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				handleExit();
			}
		});
        this.setSize(new java.awt.Dimension(834,546));
        this.setContentPane(getMainSplitPane());
        this.setJMenuBar(getMainMenuBar());
        this.pack();
        this.setVisible(true);
	}
	
	
	private QueryTree getQueryTree() {
		if (queryTree == null) {
			queryTree = new QueryTree();
			queryTree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					if (e.isAddedPath()) {
						// selection made
						TreePath selectionPath = e.getPath();
						if (selectionPath.getLastPathComponent() instanceof IconTreeNode) {
							IconTreeNode node = (IconTreeNode) selectionPath.getLastPathComponent();
							// set up editor based on type of node
							if (node instanceof QueryTreeNode) {
								// can only set the target at this point
								enableQueryBuildingButtons(new JButton[] {getSetTargetButton()});
							} else if (node instanceof TargetTreeNode ||
								node instanceof AssociationTreeNode) {
								// find the query object
								Object queryObject = null;
								if (node instanceof TargetTreeNode) {
									queryObject = ((TargetTreeNode) node).getTarget();
								} else {
									queryObject = ((AssociationTreeNode) node).getAssociation();
								}
								
								BaseType type = new BaseType(queryObject.getName());
								System.out.println("Changing type diaplay to: " + type.getTypeName());
								// change the selection of type
								getTypeDisplayPanel().setSelectedType(type);
								
								// count children of the target node
								if (node.getChildCount() == 0) {
									enableQueryBuildingButtons(new JButton[] {
										getAddAssociationButton(), getAddAttributeButton(), getAddGroupButton(),
										getRemoveItemButton()
									});
								} else {
									// node already has children, turn off the add buttons
									enableQueryBuildingButtons(new JButton[] {getRemoveItemButton()});
								}
							} else if (node instanceof AttributeTreeNode) {
								enableQueryBuildingButtons(new JButton[] {
									getChangePredicateButton(), getChangeValueButton(), getRemoveItemButton()
								});
							} else if (node instanceof GroupTreeNode) {
								enableQueryBuildingButtons(new JButton[] {
									getAddAssociationButton(), getAddAttributeButton(), getAddGroupButton(),
									getRemoveItemButton(), getChangeLogicButton()
								});
							} else {
								throw new IllegalArgumentException("What the heck is " + node.getClass().getName() + " doing in the tree??");
							}
						}						
					} else {
						enableQueryBuildingButtons(new JButton[] {});
					}
				}
			});
		}
		return queryTree;
	}
	
	
	private JScrollPane getQueryTreeScrollPane() {
		if (queryTreeScrollPane == null) {
			queryTreeScrollPane = new JScrollPane();
			queryTreeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			queryTreeScrollPane.setBorder(BorderFactory.createTitledBorder(
				null, "Query", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			queryTreeScrollPane.setViewportView(getQueryTree());
		}
		return queryTreeScrollPane;
	}
	

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getContextButtonPanel() {
		if (contextButtonPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(2);
			gridLayout.setVgap(2);
			gridLayout.setColumns(4);
			gridLayout.setHgap(2);
			contextButtonPanel = new JPanel();
			contextButtonPanel.setLayout(gridLayout);
			contextButtonPanel.add(getSetTargetButton());
			contextButtonPanel.add(getAddAssociationButton());
			contextButtonPanel.add(getAddAttributeButton());
			contextButtonPanel.add(getAddGroupButton());
			contextButtonPanel.add(getChangeValueButton());
			contextButtonPanel.add(getChangePredicateButton());
			contextButtonPanel.add(getChangeLogicButton());
			contextButtonPanel.add(getRemoveItemButton());
			// disable all buttons until a domain model has been loaded...
			enableQueryBuildingButtons(new JButton[] {});
		}
		return contextButtonPanel;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetTargetButton() {
		if (setTargetButton == null) {
			setTargetButton = new JButton();
			setTargetButton.setText("Set Target");
			setTargetButton.setName("setTargetButton");
			setTargetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// see if there is an existing target node
					QueryTreeNode queryNode = getQueryTree().getQueryTreeNode();
					if (queryNode.getQuery().getTarget() != null) {
						String[] message = {
							"The query already has a target.  Setting a new one",
							"will remove all information from the query."
						};
						int choice = JOptionPane.showConfirmDialog(
							QueryBuilder.this, message, "Confirm", JOptionPane.YES_NO_OPTION);
						if (choice != JOptionPane.YES_OPTION) {
							return;
						}
					}
					// get the selected target out of the types panel
					BaseType selectedType = getTypeDisplayPanel().getSelectedType();
					if (selectedType != null) {
						Object targetObject = new Object();
						targetObject.setName(selectedType.getTypeName());
						queryNode.getQuery().setTarget(targetObject);
						queryNode.rebuild();
						getQueryTree().refreshTree();
						// get the just added target node and make it visible
						TargetTreeNode targetNode = (TargetTreeNode) queryNode.getFirstChild();
						TreePath path = new TreePath(targetNode.getPath());
						getQueryTree().makeVisible(path);
					} else {
						JOptionPane.showMessageDialog(QueryBuilder.this, "Please select a type to target");
					}
				}
			});
		}
		return setTargetButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddAssociationButton() {
		if (addAssociationButton == null) {
			addAssociationButton = new JButton();
			addAssociationButton.setText("Add Association");
			addAssociationButton.setName("addAssociationButton");
			addAssociationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// verify the base type selected matches the one in the query tree
					IconTreeNode node = (IconTreeNode) getQueryTree().getSelectionPath().getLastPathComponent();
					Object parentQueryObject = getParentQueryObject(node);
					BaseType selectedType = getTypeDisplayPanel().getSelectedType();
					if (selectedType == null || !selectedType.getTypeName().equals(parentQueryObject.getName())) {
						JOptionPane.showMessageDialog(
							QueryBuilder.this, "Please select associations from the type " + parentQueryObject.getName());
						return;
					}
					
					// see what association is selected
					AssociatedType assocType = getTypeDisplayPanel().getSelectedAssociation();
					if (assocType != null) {
						Association association = new Association();
						association.setRoleName(assocType.getRoleName());
						association.setName(assocType.getTypeName());
						if (node instanceof AssociationTreeNode || node instanceof TargetTreeNode) {
							parentQueryObject.setAssociation(association);
						} else { // group
							Group group = ((GroupTreeNode) node).getGroup();
							Association[] currentAssociations = group.getAssociation();
							if (currentAssociations != null) {
								group.setAssociation((Association[]) Utils.appendToArray(currentAssociations, association));
							} else {
								group.setAssociation(new Association[] {association});
							}
						}
						node.rebuild();
						getQueryTree().refreshTree();
						// get the group node and make it visible
						AssociationTreeNode assocNode = null;
						Enumeration nodeChildren = node.children();
						while (nodeChildren.hasMoreElements()) {
							IconTreeNode childNode = (IconTreeNode) nodeChildren.nextElement();
							if (childNode instanceof AssociationTreeNode) {
								if (((AssociationTreeNode) childNode).getAssociation() == association) {
									assocNode = (AssociationTreeNode) childNode;
									break;
								}
							}
						}
						TreePath path = new TreePath(assocNode.getPath());
						getQueryTree().makeVisible(path);
					} else {
						JOptionPane.showMessageDialog(QueryBuilder.this, "Please select an association");
					}
				}
			});
		}
		return addAssociationButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddAttributeButton() {
		if (addAttributeButton == null) {
			addAttributeButton = new JButton();
			addAttributeButton.setText("Add Attribute");
			addAttributeButton.setName("addAttributeButton");
			addAttributeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// verify the base type selected matches the one in the query tree
					IconTreeNode node = (IconTreeNode) getQueryTree().getSelectionPath().getLastPathComponent();
					Object parentQueryObject = getParentQueryObject(node);
					BaseType selectedType = getTypeDisplayPanel().getSelectedType();
					if (selectedType == null || !selectedType.getTypeName().equals(parentQueryObject.getName())) {
						JOptionPane.showMessageDialog(
							QueryBuilder.this, "Please select attributes from the type " + parentQueryObject.getName());
						return;
					}
					
					// see if an attribute is selected
					AttributeType attribType = getTypeDisplayPanel().getSelectedAttribute();
					if (attribType != null) {
						Attribute attrib = AttributeModifyDialog.getAttribute(QueryBuilder.this, attribType);
						if (attrib != null) {
							// get the selected target / assocition / group node
							if (node instanceof TargetTreeNode || node instanceof AssociationTreeNode) {
								parentQueryObject.setAttribute(attrib);
							} else if (node instanceof GroupTreeNode) {
								Group group = ((GroupTreeNode) node).getGroup();
								if (group.getAttribute() != null) {
									Attribute[] addedAttribs = (Attribute[]) Utils.appendToArray(group.getAttribute(), attrib);
									group.setAttribute(addedAttribs);
								} else {
									group.setAttribute(new Attribute[] {attrib});
								}
							}
							// rebuild the tree for the new node
							node.rebuild();
							getQueryTree().refreshTree();
							// make the node just added visible in the tree
							AttributeTreeNode attribNode = null;
							Enumeration nodeChildren = node.children();
							while (nodeChildren.hasMoreElements()) {
								IconTreeNode tempNode = (IconTreeNode) nodeChildren.nextElement();
								if (tempNode instanceof AttributeTreeNode) {
									if (((AttributeTreeNode) tempNode).getAttribute() == attrib) {
										attribNode = (AttributeTreeNode) tempNode;
										break;
									}
								}
							}
							TreePath path = new TreePath(((DefaultTreeModel) getQueryTree().getModel())
								.getPathToRoot(attribNode));
							getQueryTree().makeVisible(path);
						}
					} else {
						JOptionPane.showMessageDialog(QueryBuilder.this, "Please select an attribute first!");
					}
				}
			});
		}
		return addAttributeButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddGroupButton() {
		if (addGroupButton == null) {
			addGroupButton = new JButton();
			addGroupButton.setText("Add Group");
			addGroupButton.setName("addGroupButton");
			addGroupButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// present user with a choice of logic for the group
					LogicalOperator operator = (LogicalOperator) JOptionPane.showInputDialog(
						QueryBuilder.this, "Select a logical operator", "Group Logic", JOptionPane.QUESTION_MESSAGE, null, 
						new java.lang.Object[] {LogicalOperator.AND, LogicalOperator.OR}, LogicalOperator.AND);
					if (operator != null) {
						Group group = new Group();
						group.setLogicRelation(operator);
						// add the group to the selected node
						IconTreeNode node = (IconTreeNode) getQueryTree().getSelectionPath().getLastPathComponent();
						if (node instanceof TargetTreeNode) {
							Object target = ((TargetTreeNode) node).getTarget();
							target.setGroup(group);
						} else if (node instanceof AssociationTreeNode) {
							Association assoc = ((AssociationTreeNode) node).getAssociation();
							assoc.setGroup(group);
						} else if (node instanceof GroupTreeNode) {
							Group g = ((GroupTreeNode) node).getGroup();
							if (g.getGroup() != null) {
								Group[] allGroups = (Group[]) Utils.appendToArray(g.getGroup(), group);
								g.setGroup(allGroups);
							} else {
								g.setGroup(new Group[] {group});
							}
						}
						node.rebuild();
						getQueryTree().refreshTree();
						// get the group node and make it visible in the tree
						GroupTreeNode groupNode = null;
						Enumeration nodeChildren = node.children();
						while (nodeChildren.hasMoreElements()) {
							IconTreeNode childNode = (IconTreeNode) nodeChildren.nextElement();
							if (childNode instanceof GroupTreeNode) {
								if (((GroupTreeNode) childNode).getGroup() == group) {
									groupNode = (GroupTreeNode) childNode;
									break;
								}
							}
						}
						TreePath path = new TreePath(groupNode.getPath());
						getQueryTree().makeVisible(path);
					}
				}
			});
		}
		return addGroupButton;
	}


	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			mainMenuBar.add(getFileMenu());
			mainMenuBar.add(getDomainModelMenu());
		}
		return mainMenuBar;
	}


	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getNewQueryMenuItem());
			fileMenu.add(getLoadQueryMenuItem());
			fileMenu.add(getSaveQueryMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}
	
	
	private JMenuItem getNewQueryMenuItem() {
		if (newQueryMenuItem == null) {
			newQueryMenuItem = new JMenuItem();
			newQueryMenuItem.setText("New Query");
			newQueryMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newQuery();
				}
			});
		}
		return newQueryMenuItem;
	}


	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getLoadQueryMenuItem() {
		if (loadQueryMenuItem == null) {
			loadQueryMenuItem = new JMenuItem();
			loadQueryMenuItem.setText("Load Query");
			loadQueryMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					loadQuery();
				}
			});
		}
		return loadQueryMenuItem;
	}


	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveQueryMenuItem() {
		if (saveQueryMenuItem == null) {
			saveQueryMenuItem = new JMenuItem();
			saveQueryMenuItem.setText("Save Query");
			saveQueryMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveQuery();
				}
			});
		}
		return saveQueryMenuItem;
	}


	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					handleExit();
				}
			});
		}
		return exitMenuItem;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveItemButton() {
		if (removeItemButton == null) {
			removeItemButton = new JButton();
			removeItemButton.setText("Remove Query Item");
			removeItemButton.setName("removeItemButton");
			removeItemButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// get the selected node (which will be removed) and its parent
					IconTreeNode selected = (IconTreeNode) getQueryTree().getSelectionPath().getLastPathComponent();
					IconTreeNode parent = (IconTreeNode) selected.getParent();
					if (parent instanceof QueryTreeNode) {
						if (selected instanceof TargetTreeNode) {
							((QueryTreeNode) parent).getQuery().setTarget(null);
						} else {
							// TODO: hande query modifier removal
						}
					} else if (parent instanceof TargetTreeNode) {
						Object target = ((TargetTreeNode) parent).getTarget();
						if (selected instanceof AttributeTreeNode) {
							target.setAttribute(null);
						} else if (selected instanceof AssociationTreeNode) {
							target.setAssociation(null);
						} else { // group
							target.setGroup(null);
						}
					} else if (parent instanceof AssociationTreeNode) {
						Association assoc = ((AssociationTreeNode) parent).getAssociation();
						if (selected instanceof AttributeTreeNode) {
							assoc.setAttribute(null);
						} else if (selected instanceof AssociationTreeNode) {
							assoc.setAssociation(null);
						} else { // group
							assoc.setGroup(null);
						}
					} else if (parent instanceof GroupTreeNode) {
						Group group = ((GroupTreeNode) parent).getGroup();
						if (selected instanceof AttributeTreeNode) {
							Attribute attrib = ((AttributeTreeNode) selected).getAttribute();
							Attribute[] cleaned = (Attribute[]) Utils.removeFromArray(group.getAttribute(), attrib);
							group.setAttribute(cleaned);
						} else if (selected instanceof AssociationTreeNode) {
							Association assoc = ((AssociationTreeNode) selected).getAssociation();
							Association[] cleaned = (Association[]) Utils.removeFromArray(group.getAssociation(), assoc);
							group.setAssociation(cleaned);
						} else { // group
							Group g = ((GroupTreeNode) selected).getGroup();
							Group[] cleaned = (Group[]) Utils.removeFromArray(g.getGroup(), g);
							group.setGroup(cleaned);
						}
					}
					parent.rebuild();
					getQueryTree().refreshTree();
				}
			});
		}
		return removeItemButton;
	}


	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getDomainModelMenu() {
		if (domainModelMenu == null) {
			domainModelMenu = new JMenu();
			domainModelMenu.setText("Domain Model");
			domainModelMenu.add(getLoadModelMenuItem());
			domainModelMenu.add(getRetrieveDomainModelMenuItem());
			domainModelMenu.addSeparator();
			domainModelMenu.add(getSaveDomainModelMenuItem());
		}
		return domainModelMenu;
	}


	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getLoadModelMenuItem() {
		if (loadModelMenuItem == null) {
			loadModelMenuItem = new JMenuItem();
			loadModelMenuItem.setText("Load From File");
			loadModelMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					loadDomainModel();
				}
			});
		}
		return loadModelMenuItem;
	}


	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getRetrieveDomainModelMenuItem() {
		if (retrieveDomainModelMenuItem == null) {
			retrieveDomainModelMenuItem = new JMenuItem();
			retrieveDomainModelMenuItem.setText("From Data Service");
			retrieveDomainModelMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getDomainModelFromService();
				}
			});
		}
		return retrieveDomainModelMenuItem;
	}


	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveDomainModelMenuItem() {
		if (saveDomainModelMenuItem == null) {
			saveDomainModelMenuItem = new JMenuItem();
			saveDomainModelMenuItem.setText("Save To Disk");
			saveDomainModelMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveDomainModel();
				}
			});
		}
		return saveDomainModelMenuItem;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getChangePredicateButton() {
		if (changePredicateButton == null) {
			changePredicateButton = new JButton();
			changePredicateButton.setText("Change Predicate");
			changePredicateButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// walk the static predicate fields
					Field[] fields = Predicate.class.getFields();
					List<Predicate> predicates = new ArrayList<Predicate>();
					for (int i = 0; i < fields.length; i++) {
						int mods = fields[i].getModifiers();
						if (Modifier.isStatic(mods) && Modifier.isPublic(mods)
							&& fields[i].getType().equals(Predicate.class)) {
							try {
								Predicate p = (Predicate) fields[i].get(null);
								predicates.add(p);
							} catch (IllegalAccessException ex) {
								ex.printStackTrace();
							}
						}
					}
					// sort the predicates by value
					Collections.sort(predicates, new Comparator<Predicate>() {
						public int compare(Predicate o1, Predicate o2) {
							return o1.toString().compareTo(o2.toString());
						}
					});
					Predicate[] predArray = new Predicate[predicates.size()];
					predicates.toArray(predArray);
					// get the selected node
					AttributeTreeNode node = (AttributeTreeNode) getQueryTree().getSelectionPath().getLastPathComponent();
					Predicate choice = (Predicate) JOptionPane.showInputDialog(QueryBuilder.this, 
						"Select a predicate", "Prediacate", JOptionPane.QUESTION_MESSAGE, 
						null, predArray, node.getAttribute().getPredicate());
					if (choice != null) {
						node.getAttribute().setPredicate(choice);
						node.rebuild();
						getQueryTree().refreshTree();
					}
				}
			});
		}
		return changePredicateButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getChangeLogicButton() {
		if (changeLogicButton == null) {
			changeLogicButton = new JButton();
			changeLogicButton.setText("Change Logic");
			changeLogicButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// get selected group to see current logic
					GroupTreeNode node = (GroupTreeNode) getQueryTree().getSelectionPath().getLastPathComponent();
					LogicalOperator[] ops = new LogicalOperator[] {LogicalOperator.OR, LogicalOperator.AND};
					LogicalOperator choice = (LogicalOperator) JOptionPane.showInputDialog(QueryBuilder.this,
						"Choose Logical Operator", "Logic", JOptionPane.QUESTION_MESSAGE, 
						null, ops, node.getGroup().getLogicRelation());
					if (choice != null) {
						node.getGroup().setLogicRelation(choice);
						node.rebuild();
						getQueryTree().refreshTree();
					}
				}
			});
		}
		return changeLogicButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getChangeValueButton() {
		if (changeValueButton == null) {
			changeValueButton = new JButton();
			changeValueButton.setText("Change Value");
			changeValueButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// get the current node
					AttributeTreeNode node = (AttributeTreeNode) getQueryTree().getSelectionPath().getLastPathComponent();
					String choice = JOptionPane.showInputDialog(
						QueryBuilder.this, "Enter new value", node.getAttribute().getValue());
					if (choice != null) {
						// TODO: Typed attribute values!
						node.getAttribute().setValue(choice);
						node.rebuild();
						getQueryTree().refreshTree();
					}
				}
			});
		}
		return changeValueButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getQueryPanel() {
		if (queryComponentsPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.gridx = 0;
			queryComponentsPanel = new JPanel();
			queryComponentsPanel.setLayout(new GridBagLayout());
			queryComponentsPanel.add(getQueryTreeScrollPane(), gridBagConstraints);
			queryComponentsPanel.add(getQueryBuildingTabbedPane(), gridBagConstraints2);
		}
		return queryComponentsPanel;
	}
	
	
	private TypeDisplayPanel getTypeDisplayPanel() {
		if (typeDisplayPanel == null) {
			typeDisplayPanel = new TypeDisplayPanel();
		}
		return typeDisplayPanel;
	}


	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getMainSplitPane() {
		if (mainSplitPane == null) {
			mainSplitPane = new JSplitPane();
			mainSplitPane.setOneTouchExpandable(true);
			mainSplitPane.setLeftComponent(getQueryPanel());
			mainSplitPane.setRightComponent(getTypeDisplayPanel());
		}
		return mainSplitPane;
	}
	
	
	private void enableQueryBuildingButtons(JButton[] buttons) {
		Set<JButton> enabledButtons = new HashSet<JButton>();
		Collections.addAll(enabledButtons, buttons);
		for (int i = 0; i < getContextButtonPanel().getComponentCount(); i++) {
			JButton button = (JButton) getContextButtonPanel().getComponent(i);
			button.setEnabled(enabledButtons.contains(button));
		}
	}
	
	
	private void handleExit() {
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirm", 
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (choice == JOptionPane.YES_OPTION) {
			dispose();
			System.exit(0);
		}
	}
	
	
	private void newQuery() {
		// see if there's already a query in the works
		QueryTreeNode node = getQueryTree().getQueryTreeNode();
		if (node != null) {
			int choice = JOptionPane.showConfirmDialog(this, "Clear current query?", 
				"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION) {
				return;
			}
		}
		// ok, clear 'er out
		getQueryTree().setQuery(new CQLQuery());
	}
	
	
	private void loadQuery() {
		if (domainModel != null) {
			JFileChooser chooser = new JFileChooser(lastDirectory);
			chooser.setFileFilter(FileFilters.XML_FILTER);
			int choice = chooser.showOpenDialog(this);
			if (choice == JFileChooser.APPROVE_OPTION) {
				File cqlFile = chooser.getSelectedFile();
				lastDirectory = cqlFile.getAbsolutePath();
				
				CQLQuery query = null;
				try {
					query = (CQLQuery) Utils.deserializeDocument(cqlFile.getAbsolutePath(), CQLQuery.class);
				} catch (Exception ex) {
					ex.printStackTrace();
					CompositeErrorDialog.showErrorDialog("Error loading CQL Query", ex);
					return;
				}
				
				try {
					// validate the query syntax
					CqlStructureValidator structureValidator = new ObjectWalkingCQLValidator();
					structureValidator.validateCqlStructure(query);
				} catch (MalformedQueryException ex) {
				    CompositeErrorDialog.showErrorDialog("The specified query is not structuraly valid CQL", ex);
					return;
				}
				
				// validate domain model
				try {
					domainValidator.validateDomainModel(query, domainModel);
				} catch (MalformedQueryException ex) {
				    CompositeErrorDialog.showErrorDialog("Error validating query", ex);
				}
				
				// set the query into the query tree
				getQueryTree().setQuery(query);
				getQueryTree().refreshTree();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please load a domain model first", 
				"No model loaded", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	
	private void saveQuery() {
		JFileChooser chooser = new JFileChooser(lastDirectory);
		chooser.setFileFilter(FileFilters.XML_FILTER);
		int choice = chooser.showSaveDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			String fileName = chooser.getSelectedFile().getAbsolutePath();
			if (!fileName.endsWith(".xml") || !fileName.endsWith(".XML")) {
				fileName += ".xml";
			}
			File cqlFile = new File(fileName);
			lastDirectory = cqlFile.getAbsolutePath();
			// get the CQL query from the tree
			QueryTreeNode queryNode = getQueryTree().getQueryTreeNode();
			CQLQuery query = queryNode.getQuery();
			try {
				FileWriter writer = new FileWriter(cqlFile);
				Utils.serializeObject(query, DataServiceConstants.CQL_QUERY_QNAME, writer);
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				CompositeErrorDialog.showErrorDialog("Error saving the CQL Query to disk", ex);
			}
		}
	}
	
	
	private void loadDomainModel() {
		JFileChooser chooser = new JFileChooser(lastDirectory);
		chooser.setFileFilter(FileFilters.XML_FILTER);
		int choice = chooser.showOpenDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File dmFile = chooser.getSelectedFile();
			lastDirectory = dmFile.getAbsolutePath();
			DomainModel tempModel = null;
			try {
				FileReader fileReader = new FileReader(dmFile);
				tempModel = MetadataUtils.deserializeDomainModel(fileReader);
			} catch (Exception ex) {
				ex.printStackTrace();
				CompositeErrorDialog.showErrorDialog("Error loading the domain model", ex);
				return;
			}			
			attemptInstallDomainModel(tempModel);
		}
	}
	
	
	private void getDomainModelFromService() {
		String url = JOptionPane.showInputDialog(this, "Enter Service URL");
		if (url != null) {
			// contact the data service for the domain model
			DomainModel tempModel = null;
			try {
				DataServiceClient client = new DataServiceClient(url);
				tempModel = MetadataUtils.getDomainModel(client.getEndpointReference());
			} catch (Exception ex) {
				ex.printStackTrace();
				CompositeErrorDialog.showErrorDialog("Error retrieving domain model from service", ex);
				return;
			}
			attemptInstallDomainModel(tempModel);
		}
	}
	
	
	private void attemptInstallDomainModel(DomainModel tempModel) {
		// we can now add the query node to the query tree
		// see if there is currently a query in the query tree
		QueryTreeNode node = getQueryTree().getQueryTreeNode();
		if (node != null) {
			// validate the existing query against the new dommain model
			CQLQuery query = node.getQuery();
			try {
				domainValidator.validateDomainModel(query, tempModel);
			} catch (MalformedQueryException ex) {
				String[] error = {
					"The current query is not compatible with the selected domain model:",
					ex.getMessage(), "\n",
					"Please select another, or save this query and start a new one."
				};
				JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		// domain model checked out, set it as the currently loaded one
		domainModel = tempModel;
		getTypeDisplayPanel().setTypeTraverser(new DomainModelTypeTraverser(domainModel));
		getQueryTree().setQuery(new CQLQuery());
	}
	
	
	private void saveDomainModel() {
		if (domainModel != null) {
			JFileChooser chooser = new JFileChooser(lastDirectory);
			chooser.setFileFilter(FileFilters.XML_FILTER);
			int choice = chooser.showSaveDialog(this);
			if (choice == JFileChooser.APPROVE_OPTION) {
				String fileName = chooser.getSelectedFile().getAbsolutePath();
				if (!fileName.endsWith(".xml") || !fileName.endsWith(".XML")) {
					fileName += ".xml";
				}
				File outFile = new File(fileName);
				lastDirectory = outFile.getAbsolutePath();
				try {
					FileWriter writer = new FileWriter(outFile);
					MetadataUtils.serializeDomainModel(domainModel, writer);
					writer.flush();
					writer.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					CompositeErrorDialog.showErrorDialog("Error saving domain model to disk", ex);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please load a domain model first.", 
				"No domain model", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	
	private Object getParentQueryObject(IconTreeNode node) {
		Object parentQueryObject = null;
		if (node instanceof AssociationTreeNode) {
			parentQueryObject = ((AssociationTreeNode) node).getAssociation();
		} else if (node instanceof TargetTreeNode) {
			parentQueryObject = ((TargetTreeNode) node).getTarget();
		} else if (node instanceof GroupTreeNode) {
			IconTreeNode testNode = node;
			while (!(testNode instanceof AssociationTreeNode)
				&& !(testNode instanceof TargetTreeNode)) {
				testNode = (IconTreeNode) testNode.getParent();
			}
			if (testNode instanceof AssociationTreeNode) {
				parentQueryObject = ((AssociationTreeNode) testNode).getAssociation();
			} else {
				parentQueryObject = ((TargetTreeNode) testNode).getTarget();
			}
		}
		return parentQueryObject;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getModifierConfigurationPanel() {
		if (modifierConfigurationPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridheight = 3;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints7.weightx = 1.0D;
			gridBagConstraints7.weighty = 1.0D;
			gridBagConstraints7.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints4.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints1.gridy = 0;
			modifierConfigurationPanel = new JPanel();
			modifierConfigurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Modifiers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			modifierConfigurationPanel.setLayout(new GridBagLayout());
			modifierConfigurationPanel.add(getCountResultsCheckBox(), gridBagConstraints1);
			modifierConfigurationPanel.add(getDistinctAttributeRadioButton(), gridBagConstraints3);
			modifierConfigurationPanel.add(getMultipleAttributesRadioButton(), gridBagConstraints4);
			modifierConfigurationPanel.add(getAttributeSelectionPanel(), gridBagConstraints7);
			PortalUtils.setContainerEnabled(getModifierConfigurationPanel(), 
				getUseQueryModsCheckBox().isSelected());
			ButtonGroup group = new ButtonGroup();
			group.add(getDistinctAttributeRadioButton());
			group.add(getMultipleAttributesRadioButton());
			group.setSelected(getDistinctAttributeRadioButton().getModel(), true);
		}
		return modifierConfigurationPanel;
	}


	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCountResultsCheckBox() {
		if (countResultsCheckBox == null) {
			countResultsCheckBox = new JCheckBox();
			countResultsCheckBox.setText("Count Results Only");
			countResultsCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					QueryTreeNode queryNode = getQueryTree().getQueryTreeNode();
					queryNode.getQuery().getQueryModifier().setCountOnly(
						countResultsCheckBox.isSelected());
				}
			});
		}
		return countResultsCheckBox;
	}


	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getDistinctAttributeRadioButton() {
		if (distinctAttributeRadioButton == null) {
			distinctAttributeRadioButton = new JRadioButton();
			distinctAttributeRadioButton.setText("Distinct Attribute");
			distinctAttributeRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					QueryTreeNode queryNode = getQueryTree().getQueryTreeNode();
					if (queryNode != null) {
						QueryModifier mods = queryNode.getQuery().getQueryModifier();
						String firstAttribute = null;
						if (getReturnedAttributesList().getModel().getSize() != 0) {
							firstAttribute = (String) getReturnedAttributesList().getModel().getElementAt(0);
							getReturnedAttributesList().setListData(new String[] {firstAttribute});
						}
						mods.setDistinctAttribute(firstAttribute);
						mods.setAttributeNames(null);
					}
				}
			});
		}
		return distinctAttributeRadioButton;
	}


	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getMultipleAttributesRadioButton() {
		if (multipleAttributesRadioButton == null) {
			multipleAttributesRadioButton = new JRadioButton();
			multipleAttributesRadioButton.setText("Multiple Attributes");
			multipleAttributesRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					QueryTreeNode queryNode = getQueryTree().getQueryTreeNode();
					QueryModifier mods = queryNode.getQuery().getQueryModifier();
					mods.setDistinctAttribute(null);
					String[] attribNames = null;
					if (getReturnedAttributesList().getModel().getSize() != 0) {
						int count = getReturnedAttributesList().getModel().getSize();
						attribNames = new String[count];
						for (int i = 0; i < count; i++) {
							attribNames[i] = (String) getReturnedAttributesList()
								.getModel().getElementAt(i);
						}
					}
					mods.setAttributeNames(attribNames);
				}
			});
		}
		return multipleAttributesRadioButton;
	}


	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getReturnedAttributesList() {
		if (returnedAttributesList == null) {
			returnedAttributesList = new JList();
		}
		return returnedAttributesList;
	}


	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getReturnedAttributesScrollPane() {
		if (returnedAttributesScrollPane == null) {
			returnedAttributesScrollPane = new JScrollPane();
			returnedAttributesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			returnedAttributesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Attributes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			returnedAttributesScrollPane.setViewportView(getReturnedAttributesList());
		}
		return returnedAttributesScrollPane;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSelectAttributesButton() {
		if (selectAttributesButton == null) {
			selectAttributesButton = new JButton();
			selectAttributesButton.setText("Select Attribute(s)");
			selectAttributesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					QueryTreeNode queryNode = getQueryTree().getQueryTreeNode();
					CQLQuery query = queryNode.getQuery();
					AttributeType[] availableAttributes = getTypeDisplayPanel().getTypeTraverser()
						.getAttributes(new BaseType(query.getTarget().getName()));
					AttributeType[] selection = AttributeSelectionDialog.selectAttributes(
						QueryBuilder.this, availableAttributes, getDistinctAttributeRadioButton().isSelected());
					QueryModifier mods = query.getQueryModifier();
					if (selection != null) {
						String[] attribNames = new String[selection.length];
						for (int i = 0; i < selection.length; i++) {
							attribNames[i] = selection[i].getName();
						}
						if (getDistinctAttributeRadioButton().isSelected()) {
							mods.setDistinctAttribute(attribNames[0]);
						} else {
							mods.setAttributeNames(attribNames);
						}
						getReturnedAttributesList().setListData(attribNames);
					} else {
						// selection canceled / cleared
						if (getDistinctAttributeRadioButton().isSelected()) {
							mods.setDistinctAttribute(null);
						} else {
							mods.setAttributeNames(null);
						}
						getReturnedAttributesList().setListData(new String[] {});
					}
				}
			});
		}
		return selectAttributesButton;
	}


	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getQueryBuildingTabbedPane() {
		if (queryBuildingTabbedPane == null) {
			queryBuildingTabbedPane = new JTabbedPane();
			queryBuildingTabbedPane.addTab("Query Components", getContextButtonPanel());
			queryBuildingTabbedPane.addTab("Query Modifications", getQueryModsPanel());
		}
		return queryBuildingTabbedPane;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAttributeSelectionPanel() {
		if (attributeSelectionPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 1.0D;
			gridBagConstraints5.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints5.gridx = 0;
			attributeSelectionPanel = new JPanel();
			attributeSelectionPanel.setLayout(new GridBagLayout());
			attributeSelectionPanel.add(getReturnedAttributesScrollPane(), gridBagConstraints5);
			attributeSelectionPanel.add(getSelectAttributesButton(), gridBagConstraints6);
		}
		return attributeSelectionPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getQueryModsPanel() {
		if (queryModsPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.weighty = 1.0D;
			gridBagConstraints9.gridy = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints8.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints8.gridy = 0;
			queryModsPanel = new JPanel();
			queryModsPanel.setLayout(new GridBagLayout());
			queryModsPanel.setSize(new java.awt.Dimension(292,127));
			queryModsPanel.add(getUseQueryModsCheckBox(), gridBagConstraints8);
			queryModsPanel.add(getModifierConfigurationPanel(), gridBagConstraints9);
		}
		return queryModsPanel;
	}


	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getUseQueryModsCheckBox() {
		if (useQueryModsCheckBox == null) {
			useQueryModsCheckBox = new JCheckBox();
			useQueryModsCheckBox.setText("Use Query Modifiers");
			useQueryModsCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					QueryTreeNode queryNode = getQueryTree().getQueryTreeNode();
					if (queryNode != null) {
						if (useQueryModsCheckBox.isSelected()) {
							queryNode.getQuery().setQueryModifier(new QueryModifier());
						} else {
							// remove the query mods from the query
							queryNode.getQuery().setQueryModifier(null);
							// clear out the GUI
							getReturnedAttributesList().setListData(new String[] {});
						}
						PortalUtils.setContainerEnabled(getModifierConfigurationPanel(), 
							useQueryModsCheckBox.isSelected());
					} else {
						// can't turn this on unless you've got a query
						JOptionPane.showMessageDialog(QueryBuilder.this, 
							"Please create a query before enabling modifications!");
					}
				}
			});
		}
		return useQueryModsCheckBox;
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Error setting system look and feel.");
		}
		JFrame builder = new QueryBuilder();
		builder.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
}
