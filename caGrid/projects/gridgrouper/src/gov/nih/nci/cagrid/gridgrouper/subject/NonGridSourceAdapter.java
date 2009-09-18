package gov.nih.nci.cagrid.gridgrouper.subject;

import java.util.Set;

import edu.internet2.middleware.subject.SourceUnavailableException;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.provider.BaseSourceAdapter;
import edu.internet2.middleware.subject.provider.SubjectTypeEnum;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class NonGridSourceAdapter extends BaseSourceAdapter {

	public NonGridSourceAdapter() {
		super();
		this.addSubjectType(SubjectTypeEnum.PERSON.getName());
		this.addSubjectType(SubjectTypeEnum.APPLICATION.getName());
	}


	public NonGridSourceAdapter(String id, String name) {
		super(id, name);
		this.addSubjectType(SubjectTypeEnum.PERSON.getName());
		this.addSubjectType(SubjectTypeEnum.APPLICATION.getName());
	}


	public Subject getSubject(String subjectId) throws SubjectNotFoundException {
		return createSubject(subjectId);
	}


	public Subject getSubjectByIdentifier(String subjectName) throws SubjectNotFoundException {
		return createSubject(subjectName);
	}


	private Subject createSubject(String subjectId) throws SubjectNotFoundException {
		return new NonGridSubject(subjectId, this);
	}


	public void init() throws SourceUnavailableException {
		// Nothing
	} // public void init()


	public Set search(String searchValue) {
		return null;
	}

}
