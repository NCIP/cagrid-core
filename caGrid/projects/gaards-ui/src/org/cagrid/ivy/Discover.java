package org.cagrid.ivy;

import java.net.URL;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.matcher.PatternMatcher;

public class Discover {
	public ModuleRevisionId[] execute(String organization, String name, URL ivysettings) {
		Ivy ivy = Ivy.newInstance();
				
		try {
			ivy.configure(ivysettings);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
				
		IvySettings settings = ivy.getSettings();
		
        ModuleRevisionId[] mrids = ivy.listModules(ModuleRevisionId.newInstance(organization,
        		name, "*", "*"), settings.getMatcher(PatternMatcher.EXACT_OR_REGEXP));
        
        return mrids;

	}
}
