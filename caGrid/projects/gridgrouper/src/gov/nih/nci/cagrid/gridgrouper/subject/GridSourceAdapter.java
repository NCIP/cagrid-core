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
public class GridSourceAdapter extends BaseSourceAdapter {

	public GridSourceAdapter() {
		super();
		this.addSubjectType(SubjectTypeEnum.PERSON.getName());
		this.addSubjectType(SubjectTypeEnum.APPLICATION.getName());
	}


	public GridSourceAdapter(String id, String name) {
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
		if ((subjectId == null) || (subjectId.equals(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID))) {
			return new AnonymousGridUserSubject(this);
		} else if (subjectId.indexOf("CN=host/") != -1) {
			validateGridId(subjectId);
			return new GridHostSubject(subjectId, this);
		} else {
			validateGridId(subjectId);
			return new GridUserSubject(subjectId, this);
		}
	}


	private void validateGridId(String gridId) throws SubjectNotFoundException {
		if (!gridId.startsWith("/")) {
			throw new SubjectNotFoundException("The id " + gridId + " is not a valid grid identity.");
		}

		if (gridId.indexOf("CN=") == -1) {
			throw new SubjectNotFoundException("The id " + gridId + " is not a valid grid identity.");
		}
	}


	public void init() throws SourceUnavailableException {
		// Nothing
	} // public void init()


	public Set search(String searchValue) {
		return null;
	}

}
