package org.cagrid.ivy;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.cache.DefaultResolutionCacheManager;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.retrieve.RetrieveOptions;

public class Retrieve {

	public void execute(URL ivySettings, URL ivyDependencies, String baseDownloadDir, String organisation, String module, String targetGridName) {
		Ivy ivy = Ivy.newInstance();
		
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
		
		try {
			ivy.resolve(ivyDependencies);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		ModuleRevisionId mrid = ModuleRevisionId.newInstance(organisation, module, targetGridName);
		RetrieveOptions options = new RetrieveOptions();

		options.setResolveId("org.cagrid-target-grids");

		String[] confs = {"default"};
		options.setConfs(confs);

		try {
			ivy.retrieve(mrid, baseDownloadDir + File.separator + "[revision]" + File.separator + "[artifact].[ext]", options);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}
}
