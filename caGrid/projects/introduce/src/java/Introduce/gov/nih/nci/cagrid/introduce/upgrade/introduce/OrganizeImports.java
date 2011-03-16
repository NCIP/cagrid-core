package gov.nih.nci.cagrid.introduce.upgrade.introduce;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;


public class OrganizeImports {

	File baseDir;
	
	public OrganizeImports(File baseDir) {
		this.baseDir = baseDir;
	}
	
	public void runStep() {
		IOFileFilter javaFilter = new IOFileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".java");
			}

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".java");
			}
		};
		
		@SuppressWarnings("unchecked")
		Iterator<File> javaFileIter = FileUtils.iterateFiles(baseDir, javaFilter, TrueFileFilter.INSTANCE);
		while (javaFileIter.hasNext()) {
			File javaFile = javaFileIter.next();
			
			try {
				File organizedJavaFile = File.createTempFile("cagrid", null);
			
				String content = IOUtils.toString(new FileInputStream(javaFile));
				String newContent = content.replaceAll("org.globus.gsi.jaas.JaasGssUtil", "org.globus.gsi.gssapi.JaasGssUtil");
				if (newContent.contains("org.globus.gsi.gssapi.GlobusGSSCredentialImpl")) {
					newContent = newContent.replaceAll(".getGlobusCredential()", ".getGlobusCredential()");
				}
				if (!newContent.equals(content)) {
					IOUtils.write(newContent, new FileOutputStream(organizedJavaFile));
					FileUtils.copyFile(organizedJavaFile, javaFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OrganizeImports oi = new OrganizeImports(new File("/Users/jgeorge/Documents/Workspaces/cagrid-sha256/caGrid-evs-1.2-ICR"));
		oi.runStep();
		

	}
	
	

}
