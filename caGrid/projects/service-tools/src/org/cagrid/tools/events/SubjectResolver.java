package org.cagrid.tools.events;

import java.util.List;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public interface SubjectResolver {
	public List<String> resolveSubjects(String targetGroup);
	public String lookupAttribute(String targetId, String att);
}
