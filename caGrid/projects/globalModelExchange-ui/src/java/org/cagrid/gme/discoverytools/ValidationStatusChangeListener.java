package org.cagrid.gme.discoverytools;

import com.jgoodies.validation.ValidationResult;

public interface ValidationStatusChangeListener {

    public void validationStatusChanged(ValidationResult validationResult);

}
