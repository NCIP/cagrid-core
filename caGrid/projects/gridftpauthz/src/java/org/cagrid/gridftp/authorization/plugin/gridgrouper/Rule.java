package org.cagrid.gridftp.authorization.plugin.gridgrouper;

class Rule {

	private String _operation;
	
	private String _path;
	
	public Rule(String op, String path) {
		_operation = op;
		_path = path;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Rule)) {
			return false;
		}
		
		Rule other = (Rule)o;
		
		return ((_operation.equals(other._operation)) &&
			    (_path.equals(other._path)));
	}
	
	public int hashCode() {
		return _operation.hashCode() & _path.hashCode();
	}
	
	public String toString() {
		return "(" + _operation + "," + _path + ")";
	}
}
