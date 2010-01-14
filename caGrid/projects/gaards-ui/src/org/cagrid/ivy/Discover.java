package org.cagrid.ivy;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.matcher.PatternMatcher;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;

public class Discover {
	URL ivySettings = null;
	String cacheDir = null;
	
	Ivy ivy = null;
	
	public Discover(URL ivySettings, String cacheDir) throws Exception {
		this.ivySettings = ivySettings;
		this.cacheDir = cacheDir;
	
		ivy = Ivy.newInstance();
		ivy.setVariable("cache", cacheDir);
		ivy.setVariable("log", "quiet");

		ivy.getLoggerEngine().setDefaultLogger(new DefaultMessageLogger(Message.MSG_ERR));
		
		ivy.configure(ivySettings);
	}
	
	public ModuleRevisionId[] execute(String organization, String name, String revision) {		
		IvySettings settings = ivy.getSettings();
		
        ModuleRevisionId[] mrids = ivy.listModules(ModuleRevisionId.newInstance(organization,
        		name, "*", revision), settings.getMatcher(PatternMatcher.REGEXP));

        List<ModuleRevisionId> mridsToReturn = new ArrayList<ModuleRevisionId>();
        for (ModuleRevisionId moduleRevisionId : mrids) {
            ResolvedModuleRevision resolvedModuleRevision = ivy.findModule(moduleRevisionId);
            
            Map extraInfo = resolvedModuleRevision.getDescriptor().getExtraInfo();
            String systemName = (String) extraInfo.get("grid:systemName");
            if (StringUtils.isEmpty(systemName)) {
            	systemName = moduleRevisionId.getRevision();
            }
            
            ModuleRevisionId mrid = new ModuleRevisionId(moduleRevisionId.getModuleId(), systemName);
            if (!mridsToReturn.contains(mrid)) {
            	mridsToReturn.add(mrid);
            }
			
		}
        return (ModuleRevisionId[]) mridsToReturn.toArray(new ModuleRevisionId[mridsToReturn.size()]);

	}

	public String getDisplayName(String organization, String name, String revision) {
		ModuleRevisionId moduleRevisionId = ModuleRevisionId.newInstance(organization, name, revision);
		
        return getDisplayName(moduleRevisionId);

	}
	
	public String getDisplayName(ModuleRevisionId moduleRevisionId) {		
        ResolvedModuleRevision resolvedModuleRevision = ivy.findModule(moduleRevisionId);
        Map extraInfo = resolvedModuleRevision.getDescriptor().getExtraInfo();
        String displayName = (String) extraInfo.get("grid:displayName");
        if (StringUtils.isEmpty(displayName)) {
        	displayName = moduleRevisionId.getRevision();
        }
        return displayName;

	}
	
}
