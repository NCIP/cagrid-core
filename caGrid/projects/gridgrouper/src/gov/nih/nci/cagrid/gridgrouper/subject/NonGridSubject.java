package gov.nih.nci.cagrid.gridgrouper.subject;

import java.util.Map;
import java.util.Set;

import edu.internet2.middleware.subject.Source;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectType;
import edu.internet2.middleware.subject.provider.SubjectTypeEnum;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class NonGridSubject implements Subject {

	private String id;
	private SubjectType type;
	private Source source;


	protected NonGridSubject(String id, Source source) {
		this.id = id;
		this.type = SubjectTypeEnum.PERSON;
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
		return id;
	}


	public String getId() {
		return id;
	}


	public String getName() {
		return id;
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
