/**
 * 
 */
package org.cagrid.installer.validator;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pietschy.wizard.InvalidStateException;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class CreateFilePermissionValidator implements Validator {

	private static final Log logger = LogFactory
			.getLog(CreateFilePermissionValidator.class);

	private String propName;

	private String message;

	public CreateFilePermissionValidator(String propName, String message) {
		this.propName = propName;
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cagrid.installer.validator.Validator#validate(java.util.Map)
	 */
	public void validate(Map state) throws InvalidStateException {
		String path = (String) state.get(this.propName);
		if (path != null) {
			File f = new File(path);
			if (!f.exists()) {
				try {
					if (f.isDirectory()) {
						f.mkdirs();
					} else {
						File parentFile = f.getParentFile();
						if (!parentFile.exists()) {
							parentFile.mkdirs();
						}
						boolean created = f.createNewFile();
						if(created){
							f.delete();
						}
					}
				} catch (Exception ex) {
					logger
							.error(
									"File '"
											+ f.getAbsolutePath()
											+ "' does not exist and error was encountered trying to create it: "
											+ ex.getMessage(), ex);
					throw new InvalidStateException(this.message);
				}
			} else {
				if (!f.canWrite()) {
					logger.error("File '" + f.getAbsolutePath()
							+ "' exists, but cannot be written to.");
					throw new InvalidStateException(this.message);
				}
			}
		}
	}
}
