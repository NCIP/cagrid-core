package org.cagrid.gridftp.authorization.plugin.gridgrouper;


import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;

import org.globus.wsrf.encoding.SerializationException;

class GrouperConfig {

	private String _operation;
	
	private String _path;
	
	private MembershipExpression _expression;

	public GrouperConfig(String _operation, String _path,
			MembershipExpression _expression) {
		super();
		this._operation = _operation;
		this._path = _path;
		this._expression = _expression;
	}

	public String get_operation() {
		return _operation;
	}

	public String get_path() {
		return _path;
	}

	public MembershipExpression get_expression() {
		return _expression;
	}
	
	public String toString() {
		String toString = "";
		try {
			toString += "op: " + _operation + ", path: " + _path + ", exp: " + GridGrouperConfigurationManager.membershipExpressionToString(_expression);
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toString;
	}

}
