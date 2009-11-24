package org.cagrid.ivy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.cache.DefaultResolutionCacheManager;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.plugins.matcher.PatternMatcher;
import org.apache.ivy.util.StringUtils;
import org.cagrid.grape.configuration.Grid;

public class Retrieve {

	public int execute(URL ivySettings, URL ivyDependencies, String baseDownloadDir, String organisation, String module, Grid grid) {
		int retrieved = 0;
		Ivy ivy = Ivy.newInstance();
		String targetGridName = grid.getSystemName();
		
		try {
			ivy.configure(ivySettings);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		DefaultResolutionCacheManager resolveEngine = (DefaultResolutionCacheManager) ivy.getResolutionCacheManager();
		resolveEngine.setResolvedIvyPattern("[organisation]/[module]/ivy-[revision].xml");
		
		ivy.setVariable("target.grid", targetGridName);
		ivy.setVariable("organisation", organisation);
		ivy.setVariable("module", module);
		
		ResolveReport report = null;
		try {
			report = ivy.resolve(ivyDependencies);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		ModuleRevisionId mrid = ModuleRevisionId.newInstance(organisation, module, targetGridName);
		RetrieveOptions options = new RetrieveOptions();

		options.setResolveId("org.cagrid-target-grids");

		String[] confs = {"default"};
		options.setConfs(confs);
		
        ModuleRevisionId[] mrids = ivy.listModules(ModuleRevisionId.newInstance(organisation,
        		module, "*", targetGridName + ".*?"), ivy.getSettings().getMatcher(PatternMatcher.REGEXP));
        
        Arrays.sort(mrids, new Comparator<ModuleRevisionId>() {
			public int compare(ModuleRevisionId o1, ModuleRevisionId o2) {
				return o1.getRevision().compareTo(o2.getRevision());
			}
		});
              
        String version = grid.getVersion();
        if (version == null) {
        	version = targetGridName;
        }
        File gridDir = new File(baseDownloadDir	+ File.separator + targetGridName);
		if (!gridDir.exists() || ((version.compareTo(mrids[mrids.length - 1].getRevision())) < 0)) {

			try {
				retrieved = ivy.retrieve(mrid, baseDownloadDir
						+ File.separator + targetGridName + File.separator
						+ "[artifact].[ext]", options);
				ResolvedModuleRevision resolved = ivy
						.findModule(mrids[mrids.length - 1]);
				grid.setVersion(resolved.getDescriptor().getRevision());
				grid.setPublicationDate(resolved.getPublicationDate());

				Map extraInfo = resolved.getDescriptor().getExtraInfo();
				String displayName = (String) extraInfo.get("grid:displayName");

				if (displayName == null || (displayName.length() == 0)) {
					grid.setDisplayName(grid.getSystemName());
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		return retrieved;
	}
	
	private String getSystemName(ResolveReport report) {
		List dependencies = report.getDependencies();
		IvyNode ivyNode = (IvyNode) dependencies.get(0);
		ModuleDescriptor descriptor = ivyNode.getDescriptor();
		Map map = descriptor.getExtraInfo();
		return (String) map.get("grid:systemName");
	}

	private String getDisplayName(ResolveReport report) {
		List dependencies = report.getDependencies();
		IvyNode ivyNode = (IvyNode) dependencies.get(0);
		ModuleDescriptor descriptor = ivyNode.getDescriptor();
		Map map = descriptor.getExtraInfo();
		return (String) map.get("grid:displayName");
	}

}
