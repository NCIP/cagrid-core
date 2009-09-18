package gov.nih.nci.cagrid.gridgrouper.subject;

import java.util.Map;
import java.util.Set;

import edu.internet2.middleware.subject.Source;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectType;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public abstract class GridSubject implements Subject {

	private String id;

	private String name;

	private SubjectType type;

	private Source source;


	protected GridSubject(String id, SubjectType type, Source source) {
		this(id, id, type, source);
	}


	protected GridSubject(String id, String name, SubjectType type, Source source) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.source = source;
	}


	public String getAttributeValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public Set getAttributeValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public Map getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getDescription() {
		return name;
	}


	public String getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public Source getSource() {
		// TODO Auto-generated method stub
		return source;
	}


	public SubjectType getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
