package gov.nih.nci.cagrid.authorization;

import java.net.MalformedURLException;
import java.net.URL;

public class GridGroupName {
	
	public static final String NAME_PATTERN = "^\\{http(s?)\\:\\/\\/.*\\}.+";
	
	private String url;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static boolean isGridGroupName(String name){
		boolean matches = false;
		if(name != null){
			matches = name.matches(NAME_PATTERN);
		}
		return matches;
	}
	
	public GridGroupName(){
		
	}
	public GridGroupName(String fullName) throws MalformedURLException{
		if(!isGridGroupName(fullName)){
			throw new IllegalArgumentException("Invalid name '" + fullName + "'");
		}
		int idx = fullName.indexOf("}", 1);
		String urlStr = fullName.substring(1, idx);
		new URL(urlStr);
		setUrl(urlStr);
		setName(fullName.substring(idx + 1));
	}
	
	public static void main(String[] args) throws Exception {
		String fullName = "{https://somehost:1234/yadda/dadda}somestem:somegroup";
		System.out.println(isGridGroupName(fullName));
		GridGroupName name = new GridGroupName(fullName);
		System.out.println("URL=" + name.getUrl());
		System.out.println("Name=" + name.getName());
	}

}
